package egovframework.smartbusmng.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.mapper.RouteMapper;
import egovframework.smartbusmng.model.route.BusBeanDto;
import egovframework.smartbusmng.model.route.BusBeanInRouteDto;
import egovframework.smartbusmng.model.route.RouteDto;

@Service
public class RouteServiceImpl implements RouteService {
	
	@Autowired
	private RouteMapper routeMapper;
	
	@Override
	@Transactional
	public List<RouteDto> getAllRoutes() {
		String groupId = SecurityUtil.getGroupId();
		if (groupId == null) {
			throw new RuntimeException("인증 정보 없음");
		}
		
		return routeMapper.getAllRoutes();
	}
	
	@Override
	@Transactional
	public List<BusBeanDto> getAllBusBeans(String userGroupId) {
		String groupId = SecurityUtil.getGroupId();
		if (groupId == null) {
			throw new RuntimeException("인증정보 없음");
		}                                             
		
		return routeMapper.getAllBusBeans(userGroupId);
	}
	
	@Override
	public List<BusBeanInRouteDto> getBusBeansInRoute(int selRouteId, int selRouteVer, String userGroupId){
		String groupId = SecurityUtil.getGroupId();
		
		if (groupId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
		System.out.println("routeId:"+selRouteId);
//		return routeMapper.getBusBeansInRoute(selRouteId, selRouteVer, groupId, userGroupId);
		return routeMapper.getBusBeansInRoute(selRouteId, selRouteVer, userGroupId);
	}
	
//	@Override
//	List<BusBeanInRouteVO> searchRoadBus(String selRouteId, int selRouteVer, int selVehId) {
		
//	}
}