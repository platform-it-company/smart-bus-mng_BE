package egovframework.smartbusmng.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.model.route.BusBeanDto;
import egovframework.smartbusmng.model.route.BusBeanInRouteDto;
import egovframework.smartbusmng.service.RouteService;

@RestController
@RequestMapping("/api/facilityMng")
public class RouteController {

	@Autowired
	private RouteService routeService;
	
	
	/**
	 * 노선 선택시 해당 노선 운행 버스 목록 조회
	 * @param selRouteId 선택한 노선ID
	 * @param selRouteVer 선택한 노선 버전
	 * @return 버스 목록 JSON
	 */
	@GetMapping("/updateBusListInRoute")
	@ResponseBody
	public Map<String, Object> getBusListInRoute(
			@RequestParam("selRouteId")  int selRouteId, 
			@RequestParam("selRouteVer") int selRouteVer,
			@RequestParam(value="userGroupId", required=false) String userGroupId,
			Model model) {
		
		String userRole = SecurityUtil.getGroupRole();
		if ("ADMIN".equals(userRole) && (userGroupId == "" || userGroupId.isEmpty())) {
			userGroupId = null;
		}

		List<BusBeanInRouteDto> busList = routeService.getBusBeansInRoute(selRouteId, selRouteVer, userGroupId);
		
		Map<String, Object> response = new HashMap<>();
		response.put("data", busList);
		
		System.out.println("## busList.size() :"+busList.size());
		return response;
	}
	
	@GetMapping("/updateAllBusList")
	@ResponseBody
	public Map<String, Object> updateAllBusList(@RequestParam(value="userGroupId", required=false) String userGroupId) {
		
		String userRole = SecurityUtil.getGroupRole();
		if ("ADMIN".equals(userRole) && (userGroupId == "" || userGroupId.isEmpty())) {
			userGroupId = null;
		}
		
		List<BusBeanDto> busList= routeService.getAllBusBeans(userGroupId);
		
		Map<String, Object> response = new HashMap<>();
		response.put("data", busList);
		
		return response;
	}
}
