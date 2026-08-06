package egovframework.smartbusmng.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import egovframework.smartbusmng.model.route.BusBeanDto;
import egovframework.smartbusmng.model.route.BusBeanInRouteDto;
import egovframework.smartbusmng.model.route.RouteDto;

@Mapper
public interface RouteMapper {

	List<RouteDto> getAllRoutes();

	List<BusBeanDto> getAllBusBeans(@Param("userGroupId") String userGroupId);

	List<BusBeanInRouteDto> getBusBeansInRoute(@Param("selRouteId") int selRouteId, @Param("selRouteVer") int selRouteVer, 
			@Param("userGroupId") String userGroupId);
}
