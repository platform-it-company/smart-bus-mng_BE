package egovframework.smartbusmng.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import egovframework.smartbusmng.model.facilityCntr.CmdReqParam;
import egovframework.smartbusmng.model.facilityCntr.CmdReqRow;
import egovframework.smartbusmng.model.facilityCntr.CmdTargetParam;
import egovframework.smartbusmng.model.facilityCntr.CmdTargetRow;
import egovframework.smartbusmng.model.facilityCntr.FacilityCntr;
import egovframework.smartbusmng.model.facilityCntr.FacilityCntrMsg;

@Mapper
public interface FacilityCntrMapper {

	void insertFacilityCntr(FacilityCntr faCntr);
	void insertFacilityCntrMsg(FacilityCntrMsg faCntrMsg);

    // create command
    void insertCmdReq(CmdReqParam req);
    void insertCmdTarget(CmdTargetParam target);
    void updateTargetMsgId(Map<String, Long> p);
    void insertEmerMsgLog(Map<String, Object> p);

    // latest only cancel
    List<Long> selectInflightReqIdsByFacilityIds(Map<String, Object> p);
    void cancelInflightTargetsByFacilityIds(Map<String, Object> p);

	// --- dispatch
    int claimTargetsBatch(Map<String, Object> p);
    List<CmdTargetRow> selectPickedTargets(Map<String, Object> p);
    void releasePickedByWorker(Map<String, Object> p);

    int markSent(Map<String, Object> p);
    int markRetry(Map<String, Object> p);

    // ack
    CmdTargetRow selectTargetByMsgId(Map<String, Object> p);
    int applyAckByMsgId(Map<String, Object> p);

    // aggregate increment
    int incReqSentCnt(Map<String, Object> p);
    int incReqAckOkCnt(Map<String, Object> p);
    int incReqAckFailCnt(Map<String, Object> p);

    // repair / admin
    int recalcReqAgg(Map<String, Object> p);

    CmdReqRow selectReqById(Map<String, Object> p);
    List<CmdTargetRow> selectTargetsByReqId(Map<String, Object> p);
}