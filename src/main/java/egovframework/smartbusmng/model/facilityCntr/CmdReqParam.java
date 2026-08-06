package egovframework.smartbusmng.model.facilityCntr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmdReqParam {
	private Long reqId;          // auto inc PK (useGeneratedKeys)
	private String requestId;    // UUID
	private Integer cmdTp;
	private String cmdMsg;
	
	private Integer qos;
	private String retainYn;     // "N"
	
	private Integer totalTargets;
	
	private String userId;
	private String tool;
}