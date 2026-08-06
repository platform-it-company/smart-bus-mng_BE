package egovframework.smartbusmng.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.model.facilityCntr.AckRequest;
import egovframework.smartbusmng.model.facilityCntr.CmdReqDetailResponse;
import egovframework.smartbusmng.model.facilityCntr.CreateCmdRequest;
import egovframework.smartbusmng.model.facilityCntr.CreateCmdResponse;
import egovframework.smartbusmng.service.FacilityCntrService;

@RestController
@RequestMapping("/api/facilityCntr")
public class FacilityCntrController {
	
	@Autowired
	private FacilityCntrService facilityCntrService;

    /**
     * 제어 명령 생성
     * - DB에 REQ / TARGET 생성
     * - 실제 MQTT publish는 scheduler(dispatch)가 처리
     */
	@PostMapping("/changeCntr")
	public ResponseEntity<?> changeCntrCommand(@RequestBody CreateCmdRequest req) {
		final String userId = SecurityUtil.getUserName();
		final String tool = "smartbusmng";

		try {
			// 1) 대상 검증
			if (req.getSelFacilityIds() == null || req.getSelFacilityIds().isEmpty()) {
				return ResponseEntity.badRequest()
                        .body(Map.of("message", "선택된 시설물이 없습니다."));
			}
			
			String cmd = req.getCmdTp();
			String cmdMsg = req.getCmdMsg();
			
			// 2) cmd 검증
	        if (cmd == null || cmd.isBlank()) {
	            return ResponseEntity.badRequest().body(Map.of("message", "명령 코드(cmd)가 없습니다."));
	        }
	        
	        // 3) cmdTp 변환
	        int cmdTp;
	        try {
	        	cmdTp = Integer.parseInt(cmd.trim());
        	} catch (NumberFormatException nfe) {
        		return ResponseEntity.badRequest()
                        .body(Map.of("message", "cmd는 숫자 문자열이어야 합니다.(예:0~5)"));
        	}
	        
            // 4) 범위 검증
            if (cmdTp < 0 || cmdTp > 5) {
				return ResponseEntity.badRequest()
                        .body(Map.of("message", "cmd 범위 오류(0~5)"));
	        }
	        
            // 5) 긴급 메시지 검증
            if (cmdTp == 5) {
	        	if (cmdMsg == null || cmdMsg.isBlank()) {
	        		return ResponseEntity.badRequest()
							.body(Map.of("message", "긴급메시지(cmd=5)일 때 메시지(cmdMsg)가 필요합니다."));
	        	}
	        	cmdMsg = cmdMsg.trim();
	        } else {
	        	cmdMsg = null;
	        }
	        
			// 6) 저장(REQ/TARGET 생성) - 여기서는 publish 하지 말고 DB에만 저장
	        // 서비스는 앞으로 (req_id/request_id 생성 + target rows 생성(PENDING))을 수행
	        CreateCmdResponse res = facilityCntrService.createCmdRequest(
	        	req.getSelFacilityIds(),
	        	cmdTp,
	        	cmdMsg,
	        	userId,
	        	tool
    		);
	        
	        return ResponseEntity.ok(res);
   
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of(
							"message", "internal_error",
							"detail", e.getMessage() == null ? "" : e.getMessage()
					));
		}
	}

    /**
     * 요청 상세 조회
     * - req 1건 + targets 목록
     */
    @GetMapping("/requests/{reqId}")
    public ResponseEntity<?> getCmdRequestDetail(@PathVariable Long reqId) {
        try {
            CmdReqDetailResponse res = facilityCntrService.getCmdRequestDetail(reqId);

            if (res == null || res.getRequest() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "message", "요청 정보를 찾을 수 없습니다.",
                                "reqId", reqId
                        ));
            }

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "internal_error",
                            "detail", e.getMessage() == null ? "" : e.getMessage()
                    ));
        }
    }

    /**
     * 집계 복구
     * - req의 sent/ack 카운트를 target 기준으로 강제 재계산
     * - 평소에는 필요 없고, 데이터 보정용
     */
    @PostMapping("/requests/{reqId}/repairAgg")
    public ResponseEntity<?> repairReqAgg(@PathVariable Long reqId) {
        try {
            int updated = facilityCntrService.repairReqAgg(reqId);

            return ResponseEntity.ok(Map.of(
                    "message", "OK",
                    "reqId", reqId,
                    "updated", updated
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "internal_error",
                            "detail", e.getMessage() == null ? "" : e.getMessage()
                    ));
        }
    }

    /**
     * 차량 ACK 반영
     * - 차량 또는 중계 서버가 msgId 기준으로 ACK 전달
     */
    @PostMapping("/ack")
    public ResponseEntity<?> applyAck(@RequestBody AckRequest ack) {
        try {
            if (ack.getMsgId() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "msgId가 올바르지 않습니다."));
            }

            int updated = facilityCntrService.applyAck(ack);

            if (updated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "message", "대상 msgId를 찾지 못했거나 이미 ACK 처리되었습니다.",
                                "msgId", ack.getMsgId()
                        ));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "OK",
                    "msgId", ack.getMsgId(),
                    "updated", updated
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "internal_error",
                            "detail", e.getMessage() == null ? "" : e.getMessage()
                    ));
        }
    }   
}
