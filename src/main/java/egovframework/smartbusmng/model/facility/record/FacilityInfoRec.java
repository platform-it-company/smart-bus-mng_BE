package egovframework.smartbusmng.model.facility.record;

import java.util.List;

import egovframework.smartbusmng.model.facility.FacilityDto;
import egovframework.smartbusmng.model.route.BusBeanDto;

public record FacilityInfoRec(
    FacilityDto facilityInfo,
    List<BusBeanDto> busList
) {}