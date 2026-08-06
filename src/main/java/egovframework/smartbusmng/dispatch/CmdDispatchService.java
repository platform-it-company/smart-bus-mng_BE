package egovframework.smartbusmng.dispatch;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.smartbusmng.mapper.FacilityCntrMapper;
import egovframework.smartbusmng.model.facilityCntr.CmdTargetRow;
import egovframework.smartbusmng.mqtt.MqttCmdPublisher;

@Service
public class CmdDispatchService {
	
	private final FacilityCntrMapper faCntrMapper;
	private final MqttCmdPublisher mqtt;
	private final ObjectMapper om;
	
    private final String workerId = "spring-" + UUID.randomUUID().toString().substring(0, 8);

    @Value("${dispatch.batch-size:20}")
    private int batchSize;

    @Value("${dispatch.stuck-sec:30}")
    private int stuckSec;

    @Value("${dispatch.retry-delay-sec:10}")
    private int retryDelaySec;

    @Value("${dispatch.max-retry:1}")
    private int maxRetry;
	
	public CmdDispatchService(FacilityCntrMapper faCntrMapper, MqttCmdPublisher mqtt, ObjectMapper om) {
		this.faCntrMapper = faCntrMapper;
		this.mqtt = mqtt;
		this.om = om;
	}
		
	public void dispatchOnce() {
        dispatchBatch();
	}
	
    @Scheduled(fixedDelayString = "${dispatch.fixed-delay-ms:1000}")
    public void dispatchLoop() {
        dispatchBatch();
    }

    public void dispatchBatch() {
        // 1) 한 번에 여러 건 claim
        int claimed = faCntrMapper.claimTargetsBatch(Map.of(
            "workerId", workerId,
            "stuckSec", stuckSec,
            "batchSize", batchSize
        ));

        if (claimed <= 0) {
            return;
        }

        // 2) 내가 claim한 건들 조회
        List<CmdTargetRow> targets = faCntrMapper.selectPickedTargets(Map.of(
            "workerId", workerId,
            "limit", batchSize
        ));

        if (targets == null || targets.isEmpty()) {
            faCntrMapper.releasePickedByWorker(Map.of(
                "workerId", workerId,
                "nextDelaySec", 0,
                "error", "picked_empty"
            ));
            return;
        }

        for (CmdTargetRow t : targets) {
            try {
                processOne(t);
            } catch (Exception e) {
                try {
                    faCntrMapper.markRetry(Map.of(
                        "targetId", t.getTargetId(),
                        "workerId", workerId,
                        "maxRetry", maxRetry,
                        "nextDelaySec", retryDelaySec,
                        "error", "dispatch_exception:" + safeMsg(e)
                    ));
                } catch (Exception ignore) {
                    // 여기서 또 죽지 않도록 무시
                }
            }
        }
    }

    private void processOne(CmdTargetRow t) throws Exception {
        long msgId = t.getMsgId();

        if (msgId <= 0) {
            faCntrMapper.markRetry(Map.of(
                "targetId", t.getTargetId(),
                "workerId", workerId,
                "maxRetry", 0,
                "nextDelaySec", 0,
                "error", "invalid_msgId"
            ));
            return;
        }
        
		// 2) publish
		int cmd = t.getCmdTp();
		String topic = "cmds/" + t.getFacilityId() + "/cmd";

		boolean retained = "Y".equalsIgnoreCase(t.getRetainYn());
		int qos = (t.getQos() == null ? 1 : t.getQos());
		
//		boolean retained = (cmd == 0 || cmd == 3 || cmd == 4 || cmd == 5);
		

		String payload = buildCmdPayload(msgId, cmd, t.getCmdMsg());

		mqtt.publish(topic, payload, qos, retained);

        int sent = faCntrMapper.markSent(Map.of(
            "targetId", t.getTargetId(),
            "workerId", workerId
        ));

        if (sent == 1) {
            faCntrMapper.incReqSentCnt(Map.of(
                "reqId", t.getReqId()
            ));
        }
	}
	
	private String buildCmdPayload(Long msgId, int cmdTp, String cmdMsg) throws Exception {
		if (cmdTp == 5) {
            return om.writeValueAsString(Map.of(
        		"msgId", msgId,
        		"cmdTp", cmdTp,
        		"emerMsg", cmdMsg == null ? "" : cmdMsg
			));
		}
        return om.writeValueAsString(Map.of(
        	"msgId", msgId,
        	"cmdTp", cmdTp
		));
	}
		
	private String safeMsg(Exception e) {
		String m = e.getMessage();
		if (m == null) return e.getClass().getSimpleName();
		return m.length() > 200 ? m.substring(0, 200) : m;
	}
}
