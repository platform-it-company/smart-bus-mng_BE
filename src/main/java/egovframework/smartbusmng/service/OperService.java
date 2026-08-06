package egovframework.smartbusmng.service;

import egovframework.smartbusmng.model.oper.DriverAddReq;
import egovframework.smartbusmng.model.oper.DriverInfo;
import egovframework.smartbusmng.model.oper.DriverListRow;
import egovframework.smartbusmng.model.oper.DriverPendingRow;
import egovframework.smartbusmng.model.oper.PagedResponse;
import egovframework.smartbusmng.model.oper.RouteDashResponse;
import egovframework.smartbusmng.model.oper.RouteRowDto;
import egovframework.smartbusmng.model.oper.VehicleDash;
import egovframework.smartbusmng.model.oper.VehicleInfo;
import egovframework.smartbusmng.model.oper.VehicleListRow;
import egovframework.smartbusmng.model.oper.VehicleSearchReq;

public interface OperService {

	RouteDashResponse routeDash();

	PagedResponse<RouteRowDto> routeSearch(String groupId, String routeId, String routeTp, int page, int size);

	RouteRowDto editDispatchInfo(RouteRowDto dto);

	RouteRowDto getRouteById(String routeId);

	VehicleDash vehicleDash(String userGroupId, String userRole);

	PagedResponse<VehicleListRow> vehicleSearch(String groupId, String userRole, VehicleSearchReq req);

	VehicleInfo getVehicleInfoById(String groupId, String userRole, int vehId);

	VehicleInfo addVehicle(String groupId, VehicleInfo dto);
	
	VehicleInfo saveVehicle(String groupId, VehicleInfo dto);

//	PagedResponse<DriverListRow> driverSearch(String groupId, DriverSearchReq req);
	PagedResponse<DriverListRow> driverSearch(String groupId, String driverName, Integer status, int page, int size);

	PagedResponse<DriverPendingRow> searchPending(String groupId);

	void reqApprove(String groupId, long[] driverIds);

	DriverInfo addDriver(String groupId, DriverAddReq req);

	DriverInfo getByDriverId(String groupId, long driverId);

	DriverInfo updateDriver(String groupId, DriverInfo dto);

	void softDelete(String groupId, long[] driverIds);
	
}
