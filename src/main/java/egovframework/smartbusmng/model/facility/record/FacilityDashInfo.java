package egovframework.smartbusmng.model.facility.record;

import java.util.List;
import egovframework.smartbusmng.model.route.BusBeanDto;

public record FacilityDashInfo(
    int totalErCnt,
    int normalErCnt,
    int rebootErCnt,
    int scrOffErCnt,
    int defaultErCnt,
    int faultErCnt,
    int disconErCnt,
    int testErCnt,
    int emergencyErCnt,
    int errTotal,
    int controlTotal,
    List<BusBeanDto> busList
) {}