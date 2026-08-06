package egovframework.smartbusmng.model.facility.record;

public record BusesInRoute(
	int vehId,
	String vehNo,
	int routeId,
	int routeVer,
	String routeNm,
	String roueteDc
) {}
