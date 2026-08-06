package egovframework.smartbusmng.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.mapper.OperMapper;
import egovframework.smartbusmng.model.common.record.InitVehListItem;
import egovframework.smartbusmng.model.oper.DriverAddReq;
import egovframework.smartbusmng.model.oper.DriverInfo;
import egovframework.smartbusmng.model.oper.DriverListRow;
import egovframework.smartbusmng.model.oper.DriverPendingRow;
import egovframework.smartbusmng.model.oper.PagedResponse;
import egovframework.smartbusmng.model.oper.RouteDashResponse;
import egovframework.smartbusmng.model.oper.RouteRowDto;
import egovframework.smartbusmng.model.oper.RouteSimpleDto;
import egovframework.smartbusmng.model.oper.RouteTpCountDto;
import egovframework.smartbusmng.model.oper.VehicleDash;
import egovframework.smartbusmng.model.oper.VehicleInfo;
import egovframework.smartbusmng.model.oper.VehicleListRow;
import egovframework.smartbusmng.model.oper.VehicleSearchReq;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperServiceImpl implements OperService {

	@Autowired
	private OperMapper operMapper;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	
	@Override
	public RouteDashResponse routeDash() {	
		// total
		long total = operMapper.countAllRoutes();
		// 노선 유형별 count
		List<RouteTpCountDto> counts = operMapper.selectRouteTpCounts();
		// 노선리스트
		List<RouteSimpleDto> routeList = operMapper.selectRouteList(null);
		// 노선 유형리스트
		List<String> routeTpList = operMapper.selectRouteTpList();
		
		return RouteDashResponse.builder()
				.totalCount(total)
				.routeTpCounts(counts)
				.routeList(routeList)
				.routeTpList(routeTpList)
				.build();
	}
	
	@Override
	public PagedResponse<RouteRowDto> routeSearch(String groupId, String routeId, 
			String routeTp, int page, int size) {
		int safePage = Math.max(page,  1);
		int safeSize = Math.min(Math.max(size,  1), 200);
		int offset = (safePage - 1) * safeSize;
		
		long total = operMapper.countRoutesByCond(routeId, routeTp);
		List<RouteRowDto> items = (total > 0)
				? operMapper.selectRoutesByCond(routeId, routeTp, offset, safeSize)
				: Collections.emptyList();
		
		return PagedResponse.<RouteRowDto>builder()
				.total(total)
				.page(safePage)
				.size(safeSize)
				.items(items)
				.build();
	}
	
	@Override
	public RouteRowDto editDispatchInfo(RouteRowDto dto) {
		String routeId = dto.getRouteId();
		
		int updated = operMapper.updateRoute(dto);
		if (updated == 0) {
			throw new IllegalStateException("No route updated: " + routeId);
		}
		
		RouteRowDto after = operMapper.selectRouteById(routeId);
		if (after == null) {
			throw new IllegalStateException("route not found after update: " + routeId);
		}
		
		return after;
	}
	
	@Override
	public RouteRowDto getRouteById(String routeId) {
		
		RouteRowDto routeInfo = operMapper.selectRouteById(routeId);
		if (routeInfo == null) {
			throw new IllegalStateException("route not found : " + routeId);
		}
		
		return routeInfo;
	}
	
	@Override
	public VehicleDash vehicleDash(String groupId, String userRole) {
		long total = operMapper.countVehicles(groupId, userRole);
		List<InitVehListItem> vehList = operMapper.selectVehNoList(groupId, userRole);
		List<String> makerList = operMapper.selectMakerList(groupId, userRole);
		
		return VehicleDash.builder()
				.total(total)
				.vehList(vehList)
				.makerList(makerList)
				.build();
	}
	
	@Override
	public PagedResponse<VehicleListRow> vehicleSearch(String groupId, String userRole, VehicleSearchReq req) {
		int safePage = Math.max(req.getPage(), 1);
		int safeSize = Math.min(Math.max(req.getSize(), 1), 200);
		Integer offset = (safePage -1) * safeSize;
		
		long total = operMapper.countVehiclesByCond(groupId, userRole, req.getVehId(), req.getMakerNm());
		
		List<VehicleListRow> items = (total > 0) 
			? operMapper.selectVehiclesByCond(groupId, userRole, req.getVehId(), req.getMakerNm(), offset, safeSize) 
			: java.util.Collections.emptyList();
		
		return PagedResponse.<VehicleListRow>builder()
			.total(total)
			.page(safePage)
			.size(safeSize)
			.items(items)
			.build();
	}

	@Override
	public VehicleInfo getVehicleInfoById(String groupId, String userRole, int vehId) {
			
		VehicleInfo info = operMapper.selectVehicleById(groupId, userRole, vehId);
		if (info == null) {
			throw new IllegalStateException("vehicle not found: " + vehId);
		}
		
		return info;
	}
	

	@Override
	@Transactional
	public VehicleInfo addVehicle(String groupId, VehicleInfo dto) {

		dto.setGroupId(groupId);
		if (dto.getVehNo() == null || dto.getVehNo().isBlank()) {
			throw new IllegalArgumentException("vehNo is requred.");
		}
		
		long dup = operMapper.countVehNoDup(dto.getGroupId(), dto.getVehNo());
		if (dup > 0) {
			throw new IllegalStateException("VehNo already exists: " + dto.getVehNo());
		}
		
		operMapper.incVehSeq();
		int newVehId = operMapper.selectNewVehId();
		dto.setVehId(newVehId);
		
		try {
			int inserted = operMapper.insertVehicle(dto);
			if (inserted == 0) throw new IllegalStateException("insert failed");
		} catch (DuplicateKeyException e) {
			throw new IllegalStateException("vehNo already exists: " + dto.getVehNo(), e);
		}
		
		String userRole = SecurityUtil.getGroupRole();
		VehicleInfo saved = operMapper.selectVehicleById(dto.getGroupId(), userRole, dto.getVehId());
		
		if (saved == null) throw new IllegalStateException("vehicle not found after insert:" + dto.getVehId());
		
		return saved;
	}

	@Override
	@Transactional
	public VehicleInfo saveVehicle(String groupId, VehicleInfo dto) {

		String userRole = SecurityUtil.getGroupRole();
		VehicleInfo before = operMapper.selectVehicleById(groupId, userRole, dto.getVehId());
		if (before == null) {
			throw new IllegalStateException("vehicle not found: " + dto.getVehId());
		}
		
		dto.setGroupId(before.getGroupId());
		
		if (dto.getVehNo() != null && !dto.getVehNo().isBlank() && !dto.getVehNo().equals(before.getVehNo())) {
			long dup = operMapper.countVehNoDup(before.getGroupId(), dto.getVehNo());
			if (dup > 0) {
				throw new IllegalStateException("vehNo already exists: " + dto.getVehNo());
			}
		}
		
		try {
			int updated = operMapper.updateVehicle(dto);
			if (updated == 0) throw new IllegalStateException("no vehicle updated:" + dto.getVehId());
		} catch (DuplicateKeyException e) {
			throw new IllegalStateException("vehNo already exists:"+dto.getVehNo(), e);
		}
		
		VehicleInfo after = operMapper.selectVehicleById(groupId, userRole, dto.getVehId());
		if (after == null) throw new IllegalStateException("vehicle not found after update:" + dto.getVehId());
		
		return after;
	}
	
	@Override
	public PagedResponse<DriverListRow> driverSearch(String groupId, String driverName, Integer status, int page, int size) {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), 200);
		int offset = (safePage - 1) * safeSize;
		
		long total = operMapper.countDriversByCond(groupId, driverName, status);
		
		List<DriverListRow> items = (total > 0)
			? operMapper.selectDriversByCond(groupId, driverName, status, offset, safeSize)
			: java.util.Collections.emptyList();
		
		return PagedResponse.<DriverListRow>builder()
			.total(total)
			.page(safePage)
			.size(safePage)
			.items(items)
			.build();
	}

	@Override
	public PagedResponse<DriverPendingRow> searchPending(String groupId) {
	
		long total = operMapper.countPendingList(groupId);
		List<DriverPendingRow> items = (total > 0)
			? operMapper.selectPendingList(groupId) : java.util.Collections.emptyList();
		
		return PagedResponse.<DriverPendingRow>builder()
			.total(total)
			.page(0)
			.size(0)
			.items(items)
			.build();
	}
	
	@Override
	@Transactional
	public void reqApprove(String groupId, long[] driverIds) {
		if (driverIds == null || driverIds.length == 0) {
			throw new IllegalArgumentException("driverIds is required");
		}
		
		String writer = safeActor();
		List<Long> ids = Arrays.stream(driverIds).boxed().toList();
		int updated = operMapper.reqApprove(groupId, ids, writer);
		
		if (updated == 0) {
			throw new IllegalStateException("No drivers approved.(already approved? not pending? forbidden group?)");
		}
	}
	
	@Override
	@Transactional
	public DriverInfo addDriver(String groupId, DriverAddReq req) {
		if (req.getLoginId() == null || req.getLoginId().isBlank()) throw new IllegalArgumentException("loginId is required");
	    if (req.getPassword() == null || req.getPassword().isBlank()) throw new IllegalArgumentException("password is required");
	    if (req.getDriverName() == null || req.getDriverName().isBlank()) throw new IllegalArgumentException("driverName is required");
	    if (req.getPhone() == null || req.getPhone().isBlank()) throw new IllegalArgumentException("phone is required");

	    if (operMapper.countLoginIdDup(req.getLoginId()) > 0) {
	    	throw new IllegalStateException("loginId already exists:" + req.getLoginId());
	    }
	    
	    String writer = safeActor();
	    String hashed = passwordEncoder.encode(req.getPassword());
	    
	    DriverInfo dto = new DriverInfo();
	    dto.setLoginId(req.getLoginId());
	    dto.setPassword(hashed);
	    dto.setDriverName(req.getDriverName());
	    dto.setPhone(req.getPhone());
	    dto.setEmail(req.getEmail());
	    dto.setGroupId(groupId);
	    
	    dto.setStatus(0);
	    dto.setAcceptedYn(0);
	    dto.setCreatedBy(writer);
	    dto.setUpdatedBy(writer);
	    dto.setAcceptedBy(writer);
	    
	    dto.setVehId(req.getVehId());
	    dto.setVehNo(req.getVehNo());
	    dto.setRouteId(req.getRouteId());
	    dto.setRouteNm(req.getRouteNm());
	    dto.setRouteTp(req.getRouteTp());
	    
	    operMapper.insertDriver(dto);
	    
	    int newId = operMapper.selectLastInsertId();
	    
	    return operMapper.selectDriverById(groupId, newId);
	}
	
	@Override
	public DriverInfo getByDriverId(String groupId, long driverId) {
		DriverInfo info = operMapper.selectDriverById(groupId, driverId);
		
		if (info == null) throw new IllegalStateException("driver not found: " + driverId);
		
		return info;
	}
	
	@Override
	@Transactional
	public DriverInfo updateDriver(String groupId, DriverInfo dto) {
		DriverInfo before = operMapper.selectDriverById(groupId, dto.getDriverId());
		if (before == null) throw new IllegalStateException("driver not found: " + dto.getDriverId());
		
		dto.setUpdatedBy(safeActor());
		
		int updated = operMapper.updateDriver(dto);
		if (updated == 0) throw new IllegalStateException("no driver updated: " + dto.getDriverId());
		
		return operMapper.selectDriverById(groupId, dto.getDriverId());
	}
	
	@Override
	@Transactional
	public void softDelete(String groupId, long[] driverIds) {
		String writer = safeActor();
		List<Long> ids = Arrays.stream(driverIds).boxed().toList();
		operMapper.softDeleteDrivers(groupId, ids, writer);
	}
	
	private String safeActor() {
		String user = SecurityUtil.getUserName();
		return (user == null || user.isBlank()) ? "SYSTEM" : user;
	}
}