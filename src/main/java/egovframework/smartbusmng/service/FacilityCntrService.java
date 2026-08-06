package egovframework.smartbusmng.service;

import java.util.List;

import egovframework.smartbusmng.model.facilityCntr.AckRequest;
import egovframework.smartbusmng.model.facilityCntr.CmdReqDetailResponse;
import egovframework.smartbusmng.model.facilityCntr.CreateCmdResponse;

public interface FacilityCntrService {
	
	void savefacilityCmdToDb(List<String> selFacilityIds, String cmd, int msgType, String emerMsg);
	
    CreateCmdResponse createCmdRequest(List<String> facilityIds, int cmdTp, String cmdMsg, String userId, String tool);

    int applyAck(AckRequest ack);

    CmdReqDetailResponse getCmdRequestDetail(Long reqId);

    int repairReqAgg(Long reqId);
}