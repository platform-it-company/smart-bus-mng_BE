package egovframework.smartbusmng.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.mapper.FacilityCntrMapper;
import egovframework.smartbusmng.model.facilityCntr.AckRequest;
import egovframework.smartbusmng.model.facilityCntr.CmdReqDetailResponse;
import egovframework.smartbusmng.model.facilityCntr.CmdReqParam;
import egovframework.smartbusmng.model.facilityCntr.CmdReqRow;
import egovframework.smartbusmng.model.facilityCntr.CmdTargetParam;
import egovframework.smartbusmng.model.facilityCntr.CmdTargetRow;
import egovframework.smartbusmng.model.facilityCntr.CreateCmdResponse;
import egovframework.smartbusmng.model.facilityCntr.FacilityCntr;
import egovframework.smartbusmng.model.facilityCntr.FacilityCntrMsg;

@Service
public class FacilityCntrServiceImpl implements FacilityCntrService {

	@Autowired
	private FacilityCntrMapper faCntrMapper;

	@Override
	@Transactional
	public void savefacilityCmdToDb(List<String> selFacilityIds, String cmd, int msgType, String emerMsg) {
		
		String userId = SecurityUtil.getUserName();
		
		try {
			for (String facilityId : selFacilityIds) {
				FacilityCntr faCntr = new FacilityCntr();
				faCntr.setFacilityId(facilityId);
				faCntr.setCommandTp(cmd);
				faCntr.setFrstUserId(userId);
				faCntr.setFrstRegistProgrm("smartbusmng");
				
				faCntrMapper.insertFacilityCntr(faCntr);
				
				if (msgType == 1 && emerMsg != null && !emerMsg.isBlank()) {
					FacilityCntrMsg faCntrMsg = new FacilityCntrMsg();
					faCntrMsg.setFacilityId(facilityId);
					faCntrMsg.setEmerMsg(emerMsg);
					faCntrMsg.setFrstUserId(userId);
					faCntrMsg.setFrstRegistProgrm("smartbusmng");
					
					faCntrMapper.insertFacilityCntrMsg(faCntrMsg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    @Transactional
    public CreateCmdResponse createCmdRequest(List<String> facilityIds, int cmdTp, String cmdMsg, String userId, String tool) {
    	    	
    	boolean keepLatestOnly = (cmdTp == 0 || cmdTp == 3 || cmdTp == 4 || cmdTp == 5);
    	
    	if (keepLatestOnly) {
        	Map<String, Object> p0 = Map.of("facilityIds", facilityIds);
        	faCntrMapper.cancelInflightTargetsByFacilityIds(p0);
    	}
    	   	
    	String requestId = UUID.randomUUID().toString().replace("-", "");
    	
    	String retainYn = (cmdTp == 0 || cmdTp == 3 || cmdTp == 4 || cmdTp == 5) ? "Y" : "N";
    	
    	CmdReqParam req = new CmdReqParam();
    	req.setRequestId(requestId);
    	req.setCmdTp(cmdTp);
    	req.setCmdMsg(cmdMsg);
    	req.setQos(1);
    	req.setRetainYn(retainYn);
    	req.setTotalTargets(facilityIds.size());
    	req.setUserId(userId);
    	req.setTool(tool);
    	
    	faCntrMapper.insertCmdReq(req); 	// reqId 채워짐
    	
    	// target 생성 + msg_id=target_id 세팅 + (긴급->본문로그)
    	for (String fid : facilityIds) {
    		CmdTargetParam t = new CmdTargetParam();
    		
    		t.setReqId(req.getReqId());
    		t.setFacilityId(fid);
    		t.setCmdTp(cmdTp);
    		t.setCmdMsg(cmdMsg);
    		t.setUserId(userId);
    		t.setTool(tool);
    		
    		faCntrMapper.insertCmdTarget(t);	// targetId 채워짐 (키 생성)
    		long msgId = t.getTargetId();		// msgId = targetId (long, unique)
            faCntrMapper.updateTargetMsgId(Map.of(
                "targetId", t.getTargetId(),
                "msgId", msgId
            ));

            if (cmdTp == 5 && cmdMsg != null && !cmdMsg.isBlank()) {
                faCntrMapper.insertEmerMsgLog(Map.of(
                    "targetId", t.getTargetId(),
                    "facilityId", fid,
                    "emergencyMsg", cmdMsg,
                    "userId", userId,
                    "tool", tool
                ));
            }
        }

        // 여기서는 recalcReqAgg() 호출 안 함
        // total_targets는 insertCmdReq 시점에 이미 정확히 들어감
        // sent/ack 계수는 dispatch/ack 시점에 increment 방식으로 처리

        return new CreateCmdResponse(req.getReqId(), requestId, facilityIds.size());
    }

    @Override
    @Transactional
    public int applyAck(AckRequest ack) {
        CmdTargetRow target = faCntrMapper.selectTargetByMsgId(Map.of(
            "msgId", ack.getMsgId()
        ));

        if (target == null) {
            return 0;
        }

        int updated = faCntrMapper.applyAckByMsgId(Map.of(
            "msgId", ack.getMsgId(),
            "ok", ack.getOk(),
            "ackCode", ack.getAckCode(),
            "ackMsg", ack.getAckMsg()
        ));

        if (updated == 1) {
            boolean ok = isAckOk(ack.getOk());

            if (ok) {
                faCntrMapper.incReqAckOkCnt(Map.of(
                    "reqId", target.getReqId()
                ));
            } else {
                faCntrMapper.incReqAckFailCnt(Map.of(
                    "reqId", target.getReqId()
                ));
            }
        }

        return updated;
    }

	@Override
    @Transactional(readOnly = true)
    public CmdReqDetailResponse getCmdRequestDetail(Long reqId) {
        CmdReqRow req = faCntrMapper.selectReqById(Map.of(
            "reqId", reqId
        ));

        List<CmdTargetRow> targets = faCntrMapper.selectTargetsByReqId(Map.of(
            "reqId", reqId
        ));

        return new CmdReqDetailResponse(req, targets);
    }

    @Override
    @Transactional
    public int repairReqAgg(Long reqId) {
        return faCntrMapper.recalcReqAgg(Map.of(
            "reqId", reqId
        ));
    }

    private boolean isAckOk(int ok) {
        return ok == 1;
    }
}