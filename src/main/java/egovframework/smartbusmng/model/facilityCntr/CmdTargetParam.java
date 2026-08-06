package egovframework.smartbusmng.model.facilityCntr;

public class CmdTargetParam {
	private Long targetId;    // useGeneratedKeys
	private Long reqId;
	private String facilityId;
	private Long msgId;

	private Integer cmdTp;
	private String cmdMsg;
	 
	private String userId;
	private String tool;

	  // getters/setters
	public Long getTargetId() { return targetId; }
	public void setTargetId(Long targetId) { this.targetId = targetId; }
	public Long getReqId() { return reqId; }
	public void setReqId(Long reqId) { this.reqId = reqId; }
	public String getFacilityId() { return facilityId; }
	public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
	public Long getMsgId() { return msgId; }
	public void setMsgId(Long msgId) { this.msgId = msgId; }
	public Integer getCmdTp() { return cmdTp; }
	public void setCmdTp(Integer cmdTp) { this.cmdTp = cmdTp; }
	public String getCmdMsg() { return cmdMsg; }
	public void setCmdMsg(String cmdMsg) { this.cmdMsg = cmdMsg; }
	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	public String getTool() { return tool; }
	public void setTool(String tool) { this.tool = tool; }
}