package egovframework.smartbusmng.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.model.common.record.InitRouteListItem;
import egovframework.smartbusmng.model.common.record.InitStopListItem;
import egovframework.smartbusmng.model.common.record.InitVehListItem;
import egovframework.smartbusmng.model.common.record.MakerDto;
import egovframework.smartbusmng.service.CommonApiService;

@RestController
@RequestMapping("/api/common")
public class CommonApiController {
	
	@Autowired
	private CommonApiService commonApiService;
	
	@GetMapping("/vehicleList")
	public ResponseEntity<List<InitVehListItem>> commonVehicleList(@RequestParam(value = "userGroupId", required = false) String userGroupId) {
		
		final String userRole = SecurityUtil.getGroupRole();
		
		if ("ADMIN".equals(userRole) && (userGroupId == "" || userGroupId.isEmpty())) {
			userGroupId = null;
		}

        List<InitVehListItem> initVehicles = commonApiService.getInitVehList(userGroupId);
           
        return ResponseEntity.ok(initVehicles);
	}
	
	@GetMapping("/routeList")
	public ResponseEntity<List<InitRouteListItem>> commonRouteList(@RequestParam(value = "userGroupId", required = false) String userGroupId) {
		final String userRole = SecurityUtil.getGroupRole();
		
		if ("ADMIN".equals(userRole) && (userGroupId == "" || userGroupId.isEmpty())) {
			userGroupId = null;
		}

        List<InitRouteListItem> initRoutes = commonApiService.getInitRouteList(userGroupId);
        
        return ResponseEntity.ok(initRoutes);
	}
	
	@GetMapping("/stopList")
	public ResponseEntity<List<InitStopListItem>> commonStopList() {

		List<InitStopListItem> initStops = commonApiService.getInitStopList();
        
		return ResponseEntity.ok(initStops);
	}
	
	@GetMapping("vehMakerList")
	public ResponseEntity<List<MakerDto>> getMakerModelList(@RequestParam("userGroupId") String userGroupId) {
		final String userRole = SecurityUtil.getGroupRole();
		
		if ("ADMIN".equals(userRole) && (userGroupId == "" || userGroupId.isEmpty())) {
			userGroupId = null;
		}
		
		List<MakerDto> infoMakerModels = commonApiService.getMakerModels(userGroupId);
		
		return ResponseEntity.ok(infoMakerModels);
	}
}
