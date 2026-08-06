package egovframework.smartbusmng.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.smartbusmng.mapper.CommonApiMapper;
import egovframework.smartbusmng.model.common.record.InitRouteListItem;
import egovframework.smartbusmng.model.common.record.InitStopListItem;
import egovframework.smartbusmng.model.common.record.InitVehListItem;
import egovframework.smartbusmng.model.common.record.MakerDto;
import egovframework.smartbusmng.model.common.record.MakerModelRow;
import egovframework.smartbusmng.model.common.record.VehModelDto;

@Service
public class CommonApiServiceImpl implements CommonApiService {
	
	@Autowired
	private CommonApiMapper commonApiMapper;
	
	@Override
	public List<InitRouteListItem> getInitRouteList(String userGroupId) {
		return commonApiMapper.getInitRouteList(userGroupId);
	}
	
	@Override
	public List<InitVehListItem> getInitVehList(String userGroupId) {
		return commonApiMapper.getInitVehList(userGroupId);
	}
	
	@Override
	public List<InitStopListItem> getInitStopList() {
		return commonApiMapper.getInitStopList();
	}
	
	@Override
	public List<MakerDto> getMakerModels(String userGroupId) {

		List<MakerModelRow> rows = commonApiMapper.selectMakerModelRows(userGroupId);
		
		Map<String, MakerDto> makerMap = new LinkedHashMap<>();
		
		for (MakerModelRow row : rows) {
			if (row.getMakerNm() == null || row.getModelNm() == null) continue;
			
			MakerDto maker = makerMap.computeIfAbsent(row.getMakerNm(), k -> {
				MakerDto m = new MakerDto();
				m.setMakerNm(k);
				return m;
			});
			
			VehModelDto model = new VehModelDto();
			model.setModelNm(row.getModelNm());
			maker.getModels().add(model);
		}
		
		return new ArrayList<>(makerMap.values());
	}
}
