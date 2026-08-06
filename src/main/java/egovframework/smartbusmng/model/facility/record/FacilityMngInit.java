package egovframework.smartbusmng.model.facility.record;

import java.util.List;

import egovframework.smartbusmng.model.common.record.InitRouteListItem;
import egovframework.smartbusmng.model.common.record.InitVehListItem;

public record FacilityMngInit(
	FacilityDashInfo facilityDashInfo,
	List<InitRouteListItem> initRoutes,
	List<InitVehListItem> initVehicles
) {}
