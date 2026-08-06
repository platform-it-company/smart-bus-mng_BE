package egovframework.smartbusmng.service;

import java.util.List;

import egovframework.smartbusmng.model.common.record.InitRouteListItem;
import egovframework.smartbusmng.model.common.record.InitStopListItem;
import egovframework.smartbusmng.model.common.record.InitVehListItem;
import egovframework.smartbusmng.model.common.record.MakerDto;

public interface CommonApiService {

	List<InitRouteListItem> getInitRouteList(String userGroupId);

	List<InitVehListItem> getInitVehList(String userGroupId);
	
	List<InitStopListItem> getInitStopList();

	List<MakerDto> getMakerModels(String userGroupId);
}
