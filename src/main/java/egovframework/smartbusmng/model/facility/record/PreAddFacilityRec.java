package egovframework.smartbusmng.model.facility.record;

import java.util.List;

public record PreAddFacilityRec(
	FacilityDashInfo faDashInfo, 
	List<String> manufacturers
) {}