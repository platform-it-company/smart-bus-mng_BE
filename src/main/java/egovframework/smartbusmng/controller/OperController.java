package egovframework.smartbusmng.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.model.oper.DriverAddReq;
import egovframework.smartbusmng.model.oper.DriverInfo;
import egovframework.smartbusmng.model.oper.RouteRowDto;
import egovframework.smartbusmng.model.oper.VehicleInfo;
import egovframework.smartbusmng.model.oper.VehicleSearchReq;
import egovframework.smartbusmng.service.OperService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/operMng")
@RequiredArgsConstructor
public class OperController {

	private final OperService operService;
	// 노선리스트(
	@GetMapping("/dispatch/Dash")
	public ResponseEntity<?> routeDash(
			@RequestParam(value="userGroupId") String userGroupId) {

		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
		return ResponseEntity.ok(operService.routeDash());
	}
	
	@GetMapping("/dispatch/search")
	public ResponseEntity<?> routeSearch(@RequestParam(value="userGroupId") String userGroupId,
			@RequestParam(value = "routeId", required = false) String routeId,
			@RequestParam(value = "routeTp", required = false) String routeTp,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "20") int size) {
		
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
		return ResponseEntity.ok(operService.routeSearch(userGroupId, routeId, routeTp, page, size));
	}
	
	@GetMapping("/dispatch/edit")
	public ResponseEntity<?> routeView(@RequestParam(value="userGroupId") String userGroupId,
			@RequestParam(value = "routeId", required = false) String routeId) {

		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
		return ResponseEntity.ok(operService.getRouteById(routeId));
		
	}
	
	@PostMapping("/dispatch/save")
	public ResponseEntity<?> routeEdit(@RequestParam(value="userGroupId") String userGroupId, 
			@RequestBody RouteRowDto dto) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
		if (dto.getRouteId() == null || dto.getRouteId().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("routeId is required.");
		}
		
		RouteRowDto saved = operService.editDispatchInfo(dto);
		
		// 저장한 결과를 보여주도록 저장 결과를 전송. 확인하고 창 닫기
		return ResponseEntity.ok(saved);
	}
	
	@GetMapping("/vehicle/dash")
	public ResponseEntity<?> vehicleDash(@RequestParam(value="userGroupId") String userGroupId) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
		String userRole = SecurityUtil.getGroupRole();
		if (userRole == null || userRole.isBlank()) {
			return ResponseEntity.status(401).build();
		}
		if (!("ADMIN".equals(userRole) || "USER".equals(userRole))) {
			return ResponseEntity.status(403).build();
		}
		
		return ResponseEntity.ok(operService.vehicleDash(userGroupId, userRole));
	}

	@PostMapping("/vehicle/search")
	public ResponseEntity<?> vehicleSearch(@RequestParam(value="userGroupId") String userGroupId,
		@RequestBody VehicleSearchReq req) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;

		String userRole = SecurityUtil.getGroupRole();
		if (userRole == null || userRole.isBlank()) {
			return ResponseEntity.status(401).build();
		}
		
		if ("ADMIN".equals(userRole)) {
			userGroupId = req.getSelGroupId();
		}
			
		return ResponseEntity.ok(operService.vehicleSearch(userGroupId, userRole, req));
	}
	
	@PostMapping("/vehicle/add")
	public ResponseEntity<?> addVehicle(@RequestParam(value="userGroupId") String userGroupId,
		@RequestBody VehicleInfo dto
	){
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
	    if (dto.getVehNo() == null || dto.getVehNo().isBlank()) {
	        return ResponseEntity.badRequest().body("vehNo is required.");
	    }
	    
	    String userRole = SecurityUtil.getGroupRole();
	    if ("USER".equals(userRole)) {
	    	ResponseEntity.badRequest().body("Not Authentificated");
	    }
	    
	    VehicleInfo saved = operService.addVehicle(userGroupId, dto);
	    
		return  ResponseEntity.ok(saved);
		
	}

	@GetMapping("/vehicle/edit")
	public ResponseEntity<?> vehicleView(@RequestParam(value="userGroupId") String userGroupId,
		@RequestParam(value="vehId") int vehId
	) {
//		ResponseEntity<?> err = validateGroup(userGroupId);
//		if (err != null) return err;
		
		String userRole = SecurityUtil.getGroupRole();
		if (userRole == null || userRole.isBlank()) {
			return ResponseEntity.status(401).build();
		}
		
		return ResponseEntity.ok(operService.getVehicleInfoById(userGroupId, userRole, vehId));
	}
	
	@PostMapping("/vehicle/save")
	public ResponseEntity<?> vehicleSave(@RequestParam(value="userGroupId") String userGroupId,
		@RequestBody VehicleInfo dto
	) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
	    if (dto.getVehId() == null) {
	        return ResponseEntity.badRequest().body("vehId is required.");
	    }
	    
	    String userRole = SecurityUtil.getGroupRole();
	    if (userRole == null || userRole.isBlank()) return ResponseEntity.status(401).build();
	    if (!("ADMIN".equals(userRole) || "USER".equals(userRole))) return ResponseEntity.status(403).build();
	    
		VehicleInfo saved = operService.saveVehicle(userGroupId, dto);
	    return ResponseEntity.ok(saved);
	}

	@GetMapping("/driver/pendingList")
	public ResponseEntity<?> driverPending(@RequestParam(value="userGroupId") String userGroupId) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
		return ResponseEntity.ok(operService.searchPending(userGroupId));
	}
	
	@PostMapping("/driver/approve")
	public ResponseEntity<?> driverApprove(@RequestParam(value="userGroupId") String userGroupId, 
		@RequestBody long[] driverIds
	) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
		operService.reqApprove(userGroupId, driverIds);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/driver/search")
	public ResponseEntity<?> driverSearch(@RequestParam(value="userGroupId") String userGroupId, 
	    @RequestParam(required=false) String driverName,
	    @RequestParam(required=false) Integer status,
	    @RequestParam(defaultValue="1") int page,
	    @RequestParam(defaultValue="20") int size) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
				
		return ResponseEntity.ok(operService.driverSearch(userGroupId, driverName, status, page, size));
	}
	
	@PostMapping("/driver/add")
	public ResponseEntity<?> driverAdd(@RequestParam(value="userGroupId") String userGroupId,
		@RequestBody DriverAddReq req
	) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
		return ResponseEntity.ok(operService.addDriver(userGroupId, req));
	}
	
	@GetMapping("/driver/edit")
	public ResponseEntity<?> driverView(@RequestParam(value="userGroupId") String userGroupId,
		@RequestParam("driverId") long driverId) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;	
		
		return ResponseEntity.ok(operService.getByDriverId(userGroupId, driverId));
	}

	@PostMapping("/driver/save")
	public ResponseEntity<?> driverSave(@RequestParam(value="userGroupId") String userGroupId,
		@RequestBody DriverInfo dto
	) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
		if (dto.getDriverId() == null) return ResponseEntity.badRequest().body("driverId is required.");
		return ResponseEntity.ok(operService.updateDriver(userGroupId,  dto));
	}

	@PostMapping("/driver/delete")
	public ResponseEntity<?> deleteDriver(@RequestParam(value="userGroupId") String userGroupId,
		@RequestBody long[] driverIds
	) {
		ResponseEntity<?> err = validateGroup(userGroupId);
		if (err != null) return err;
		
		operService.softDelete(userGroupId, driverIds);
		return ResponseEntity.ok().build();
	}

	private ResponseEntity<?> validateGroup(String userGroupId) {
		if (userGroupId == null || userGroupId.trim().isEmpty()) {
			return ResponseEntity.badRequest().build(); 	// 400
		}
		String myGroupId = SecurityUtil.getGroupId();
		if (!userGroupId.equals(myGroupId)) {
			return ResponseEntity.status(403).build();
		}
		return null;
	}

}
