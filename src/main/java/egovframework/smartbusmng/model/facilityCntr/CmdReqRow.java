package egovframework.smartbusmng.model.facilityCntr;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmdReqRow {

	private Long reqId;
	private String requestId;
	
	private Integer cmdTp;
	private String cmdMsg;
	
	private Integer qos;
	private String retainYn;
	private LocalDateTime expireDt;
	
	private String status;
	
	private Integer totalTargets;
	private Integer sentCnt;
	private Integer ackOkCnt;
	private Integer ackFailCnt;
	
	private LocalDateTime frstRegistDt;
	private String frstUserId;
	private String frstRegistTool;
	
}
