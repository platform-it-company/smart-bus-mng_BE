package egovframework.smartbusmng.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import egovframework.smartbusmng.model.facility.FacilityDto;
import egovframework.smartbusmng.model.facility.FacilityVO;
import egovframework.smartbusmng.model.facility.FacilityVehInfoDto;
import egovframework.smartbusmng.model.facility.ProductInfoDto;
import egovframework.smartbusmng.model.facility.record.BusesInRoute;
import egovframework.smartbusmng.model.facility.record.ModelDto;
import egovframework.smartbusmng.model.facility.record.ModelSpecDto;

@Mapper
public interface FacilityMapper {

	int insertFacility(FacilityVO facilityVO);
	int updateFacility(FacilityVO facilityVO);

	List<FacilityVehInfoDto> searchFaByVehIds(@Param("selRouteId") int selRouteId, @Param("selRouteVer") int selRouteVer, 
			@Param("selVehId") int selVehId, @Param("selState") int selState, @Param("viewType") int viewType, 
			@Param("useTp") String useTp, @Param("userGroupId") String userGroupId);

	int getFaCount(@Param("cntType") String cntType, @Param("userGroupId") String userGroupId);

	FacilityDto getFaById(@Param("facilityId") String facilityId, @Param("userGroupId") String userGroupId);

	List<String> getManufacureList(@Param("userGroupId") String userGroupId);

	List<ModelDto> getModelsByManufacturer(@Param("groupId") String groupId, @Param("manufacturer") String manufacturer);

	ModelSpecDto getModelDetail(@Param("groupId") String groupId, @Param("modelId") String modelId);
	
	String nextModelId(@Param("groupId") String groupId);
	
	int insertModel(ProductInfoDto dto);

	int updateModel(ProductInfoDto dto);
	
	List<BusesInRoute> getBusesInRoute(@Param("selRouteId") int selRouteId, @Param("selRouteVer") int selRouteVer,
			@Param("userGroupId")String userGroupId);
	
	int countFacilityUsingModel(@Param("groupId") String groupId, @Param("modelId") String modelId);
	
	int deleteModel(@Param("groupId") String groupId, @Param("modelId") String modelId);
	
	int deleteFacility(@Param("groupId") String groupId, @Param("facilityId") String facilityId);
}
