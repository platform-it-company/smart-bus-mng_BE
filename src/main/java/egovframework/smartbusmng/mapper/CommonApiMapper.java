package egovframework.smartbusmng.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import egovframework.smartbusmng.model.common.record.InitRouteListItem;
import egovframework.smartbusmng.model.common.record.InitStopListItem;
import egovframework.smartbusmng.model.common.record.InitVehListItem;
import egovframework.smartbusmng.model.common.record.MakerModelRow;

public interface CommonApiMapper {
	
	List<InitRouteListItem> getInitRouteList(@Param("userGroupId") String userGroupId);
	
	List<InitVehListItem> getInitVehList(@Param("userGroupId") String userGroupId);
	
	List<InitStopListItem> getInitStopList();

	List<MakerModelRow> selectMakerModelRows(String userGroupId);
}
