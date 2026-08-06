package egovframework.smartbusmng.service;

import java.util.List;

import egovframework.smartbusmng.model.route.BusBeanDto;
import egovframework.smartbusmng.model.route.BusBeanInRouteDto;
import egovframework.smartbusmng.model.route.RouteDto;

public interface RouteService {
	List<RouteDto> getAllRoutes();
	List<BusBeanDto> getAllBusBeans(String tempUserGroupId);
	List<BusBeanInRouteDto> getBusBeansInRoute(int selRouteId, int routeVer, String userGroupId);
//	List<BusBeanInRouteVO> searchRoadBus(String selRouteId, int selRouteVer, int selVehId);
}
