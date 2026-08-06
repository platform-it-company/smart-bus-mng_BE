package egovframework.smartbusmng.model.facilityCntr;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CmdTargetRow {
	private long targetId;
	private long reqId;
	private String facilityId;
	
	private long msgId;
	
	private int cmdTp;
	private String cmdMsg;
	
	private String retainYn;
	private Integer qos;
	
	private String targetStatus;
	private LocalDateTime publishDt;
	private LocalDateTime ackDt;
	
	private String ackStatus;
	private String ackCode;
	private String ackMsg;
	
	private int retryCnt;
	private LocalDateTime nextRetryDt;
	
	private String workerId;
	private LocalDateTime pickedDt;
	private String lastError;
}