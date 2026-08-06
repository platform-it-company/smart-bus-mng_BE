package egovframework.smartbusmng.service;

import java.util.List;

import egovframework.smartbusmng.model.facility.FacilityDto;
import egovframework.smartbusmng.model.facility.FacilityVO;
import egovframework.smartbusmng.model.facility.FacilityVehInfoDto;
import egovframework.smartbusmng.model.facility.ProductInfoDto;
import egovframework.smartbusmng.model.facility.record.BusesInRoute;
import egovframework.smartbusmng.model.facility.record.ModelDto;
import egovframework.smartbusmng.model.facility.record.ModelSpecDto;

public interface FacilityService {
	
	void insertFacility(FacilityVO facilityVO);
	
	void updateFacility(FacilityVO facilityVO);
	
	List<FacilityVehInfoDto> searchFaByVehIds(String selRouteId, int selRouteVer, int selVehId, int selState, int viewType, String useTp, String userGroupId);
		
	int getFaCount(String cntType, String userGroupId);
	
	FacilityDto getFaById(String facilityId, String userGroupId);

	List<String> getManufactureList(String userGroupId);

	List<ModelDto> getModelsByManufacturer(String groupId, String manufacturer);

	ModelSpecDto getModelDetail(String groupId, String modelId);

	void saveModel(ProductInfoDto dto);

	void saveFaciltiy(FacilityDto dto);

	List<BusesInRoute> getBusesInRoute(String selRouteId, int selRouteVer, String userGroupId);

	void deleteModel(String groupId, String modelId);

	void deleteFacility(String groupId, String facilityId);
}
