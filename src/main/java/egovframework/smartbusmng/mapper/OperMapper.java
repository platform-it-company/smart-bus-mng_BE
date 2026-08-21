package egovframework.smartbusmng.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import egovframework.smartbusmng.model.common.record.InitVehListItem;
import egovframework.smartbusmng.model.oper.DriverInfo;
import egovframework.smartbusmng.model.oper.DriverListRow;
import egovframework.smartbusmng.model.oper.DriverPendingRow;
import egovframework.smartbusmng.model.oper.RouteRowDto;
import egovframework.smartbusmng.model.oper.RouteSimpleDto;
import egovframework.smartbusmng.model.oper.RouteTpCountDto;
import egovframework.smartbusmng.model.oper.VehicleInfo;
import egovframework.smartbusmng.model.oper.VehicleListRow;

@Mapper
public interface OperMapper {

	long countAllRoutes();

	List<String> selectRouteTpList();

	List<RouteSimpleDto> selectRouteList(@Param("routeTp") String routeTp);

	List<RouteTpCountDto> selectRouteTpCounts();

    long countRoutesByCond(@Param("routeId") String routeId, @Param("routeTp") String routeTp);

	List<RouteRowDto> selectRoutesByCond(@Param("routeId") String routeId, @Param("routeTp") String routeTp,
            @Param("offset") int offset, @Param("size") int size);
	
	RouteRowDto editDispatchInfo(@Param("routeId") String routeId, @Param("useAt") String useAt);

	int updateRoute(RouteRowDto dto);

	RouteRowDto selectRouteById(String routeId);

	long countVehicles(@Param("groupId") String groupId, @Param("userRole") String userRole);

	List<String> selectMakerList(@Param("groupId") String groupId, @Param("userRole") String userRole);

	List<InitVehListItem> selectVehNoList(@Param("groupId") String groupId, @Param("userRole") String userRole);

	long countVehiclesByCond(@Param("groupId") String groupId, @Param("userRole") String userRole, @Param("vehId") Integer vehId,
		@Param("makerNm") String makerNm);
	

	List<VehicleListRow> selectVehiclesByCond(@Param("groupId") String groupId,@Param("userRole") String userRole,
		@Param("vehId") Integer vehId, @Param("makerNm") String makerNm, 
		@Param("offset") int offset, @Param("size") int size);

	VehicleInfo selectVehicleById(@Param("groupId") String groupId, @Param("userRole") String userRole,
		@Param("vehId") Integer vehId);
	
	int incVehSeq();
	
	int selectNewVehId();
	
	long countVehNoDup(@Param("groupId") String groupId, @Param("vehNo") String vehNo);
	
	int insertVehicle(VehicleInfo dto);
	
	int updateVehicle(VehicleInfo dto);

	long countDriversByCond(@Param("groupId") String groupId, @Param("driverName") String driverName,
		@Param("status") Integer status);

	long countPendingList(String groupId);

	List<DriverPendingRow> selectPendingList(String groupId);

	List<DriverListRow> selectDriversByCond(@Param("groupId") String groupId, @Param("driverName") String driverName,
		@Param("status") Integer status, @Param("offset") int offset, @Param("size") int size);

	int reqApprove(@Param("groupId") String groupId, @Param("driverIds") List<Long> driverIds, @Param("writer") String writer);

	int countLoginIdDup(@Param("loginId") String loginId);

	DriverInfo selectDriverById(@Param("groupId") String groupId, @Param("driverId") long driverId);

	int insertDriver(DriverInfo dto);
	
	int selectLastInsertId();
	
	int updateDriver(DriverInfo dto);

	int softDeleteDrivers(@Param("groupId") String groupId, @Param("driverIds") List<Long> driverIds,
		@Param("writer") String writer);
	
}
