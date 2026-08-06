package egovframework.smartbusmng.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.mapper.FacilityMapper;
import egovframework.smartbusmng.model.facility.FacilityDto;
import egovframework.smartbusmng.model.facility.FacilityVO;
import egovframework.smartbusmng.model.facility.FacilityVehInfoDto;
import egovframework.smartbusmng.model.facility.ProductInfoDto;
import egovframework.smartbusmng.model.facility.record.BusesInRoute;
import egovframework.smartbusmng.model.facility.record.ModelDto;
import egovframework.smartbusmng.model.facility.record.ModelSpecDto;

@Service
public class FacilityServiceImpl implements FacilityService {

	@Value("${project.name}")
	private String pName;
	
	@Autowired
	private FacilityMapper facilityMapper;
	
	@Override
	@Transactional
	public void insertFacility(FacilityVO facilityVO) {
		facilityMapper.insertFacility(facilityVO);
	}

	@Override
	@Transactional
	public void updateFacility(FacilityVO facilityVO) {
		facilityMapper.updateFacility(facilityVO);
	}
	
	@Override
	public List<FacilityVehInfoDto> searchFaByVehIds(String strRouteId, int selRouteVer, int selVehId, int selState, int viewType, String useTp, String userGroupId) {

		String groupId = SecurityUtil.getGroupId();

		if (groupId == null) {
			throw new RuntimeException("인증 정보 없음");
		}
		
		int selRouteId = 0;
		
	    if (strRouteId != null && strRouteId.length() > 3) {
	        try {
	            // 앞 3글자 제거 후 숫자로 변환
	            String numericPart = strRouteId.substring(3);  // ex) "SJB293000169" -> "293000169"
	            selRouteId = Integer.parseInt(numericPart);
	        } catch (NumberFormatException e) {
	            // 형식이 이상할 때는 적당히 예외 처리
	            throw new IllegalArgumentException("잘못된 노선ID 형식입니다: " + strRouteId, e);
	        }
	    }
	    
		return facilityMapper.searchFaByVehIds(selRouteId, selRouteVer, selVehId, selState, viewType, useTp, userGroupId);
	}
	
	@Override
	public FacilityDto getFaById(String facilityId, String userGroupId) {
		String groupId = SecurityUtil.getGroupId();

		if (groupId == null) {
			throw new RuntimeException("인증 정보 없음");
		}
		
		return facilityMapper.getFaById(facilityId, userGroupId);
	}
	
	@Override
	public int getFaCount(String cntType, String userGroupId) {
		String groupId = SecurityUtil.getGroupId();

		if (groupId == null) {
			throw new RuntimeException("인증 정보 없음");
		}
			
		System.out.println("### getFacount : "+cntType+":"+groupId);
		return facilityMapper.getFaCount(cntType, userGroupId);
//		return facilityMapper.getFaCount(cntType, groupId, userGroupId);

	}
	
	@Override
	public List<String> getManufactureList(String userGroupId) {
		return facilityMapper.getManufacureList(userGroupId);
	}

	@Override
	public List<ModelDto> getModelsByManufacturer(String groupId, String manufacturer) {
		return facilityMapper.getModelsByManufacturer(groupId, manufacturer);
	}
	
	@Override
	public ModelSpecDto getModelDetail(String groupId, String modelId) {
		return facilityMapper.getModelDetail(groupId, modelId);
	}
	
	@Override
	@Transactional
	public void saveModel(ProductInfoDto dto) {
		String groupId = SecurityUtil.getGroupId();
		String userId = SecurityUtil.getUserName();
		
		dto.setGroupId(groupId);

		// model edit
		if (dto.getModelId() != null && !dto.getModelId().isBlank()) {
			
			System.out.println("======modelId(edit):"+dto.getModelId());
			dto.setLastUserId(userId);
			dto.setLastRegistTool(pName);
			
	        int count = facilityMapper.updateModel(dto);
	        
	        if (count != 1) {
	        	throw new RuntimeException("모델 수정 실패");
	        }
	    // model add
		} else {
			String nextModelId = facilityMapper.nextModelId(groupId);
			dto.setModelId(nextModelId);
			
			System.out.println("======modelId:"+nextModelId);
			dto.setFrstUserId(userId);
			dto.setFrstRegistTool(pName);
			
	        int count = facilityMapper.insertModel(dto);
	        
	        if (count != 1) {
	        	throw new RuntimeException("모델 저장 실패");
	        }				
		}
	}
	
	@Override
	@Transactional
	public void saveFaciltiy(FacilityDto dto) {
	    String groupId = SecurityUtil.getGroupId();
	    String userId  = SecurityUtil.getUserName();
	
	    FacilityVO faVo = new FacilityVO();
	    
	    faVo.setGroupId(groupId);
	    faVo.setModelId(dto.getModelId());
	    faVo.setUseTp(dto.getUseTp());
	    faVo.setStatCd(dto.getStatCd());
	    faVo.setVehId(dto.getVehId());
	    
	    if (dto.getInstallDe() != null) {
	        faVo.setInstallDe(dto.getInstallDe().replace("-", ""));
	    }

	    if (dto.getFacilityId() == null || dto.getFacilityId().isBlank()) {
			String newId = generateId("ER");
			dto.setFacilityId(newId);
			
			faVo.setFacilityId(dto.getFacilityId());
			faVo.setFrstUserId(userId);
			faVo.setFrstRegistProgram(pName);

			int count = facilityMapper.insertFacility(faVo);
			System.out.println("==========insertFacility count: "+ count);
	        if (count != 1) {
	        	throw new RuntimeException("모델 저장 실패");
	        }
		} else {
			faVo.setFacilityId(dto.getFacilityId());
			faVo.setLastUserId(userId);
			faVo.setLastRegistProgram(pName);
						
			System.out.println("facilityId = " + faVo.getFacilityId());
			System.out.println("modelId    = " + faVo.getModelId());
			System.out.println("installDe  = " + faVo.getInstallDe());
			System.out.println("useTp      = " + faVo.getUseTp());
			System.out.println("statCd     = " + faVo.getStatCd());
			System.out.println("vehId      = " + faVo.getVehId());
			System.out.println("groupId    = " + faVo.getGroupId());
			System.out.println("lastUserId = " + faVo.getLastUserId());
			System.out.println("lastRegistProgram = " + faVo.getLastRegistProgram());
			
			int count = facilityMapper.updateFacility(faVo);
			
	        if (count != 1) {
	        	throw new RuntimeException("모델 저장 실패");
	        }			
		}
	}
	
	public String generateId(String preId) {
		String id_timestamp = new SimpleDateFormat("yyMMddmmss").format(new Date());
		String temp_id = preId + id_timestamp;
		return temp_id;
	}
	
	@Override
	public List<BusesInRoute> getBusesInRoute(String strRouteId, int selRouteVer, String userGroupId) {
		
		int selRouteId = 0;
		
	    if (strRouteId != null && strRouteId.length() > 3) {
	        try {
	            // 앞 3글자 제거 후 숫자로 변환
	            String numericPart = strRouteId.substring(3);  // ex) "SJB293000169" -> "293000169"
	            selRouteId = Integer.parseInt(numericPart);
	        } catch (NumberFormatException e) {
	            // 형식이 이상할 때는 적당히 예외 처리
	            throw new IllegalArgumentException("잘못된 노선ID 형식입니다: " + strRouteId, e);
	        }
	    }
		return facilityMapper.getBusesInRoute(selRouteId, selRouteVer, userGroupId);
	}
	
	@Override
	@Transactional
	public void deleteModel(String groupId, String modelId) {
		int used = facilityMapper.countFacilityUsingModel(groupId, modelId);
		
		if (used > 0) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "해당 모델은 시설물에 사용 중이라 삭제할 수 없습니다.(사용중 수: " + used+")");
		}

		int deleted = facilityMapper.deleteModel(groupId, modelId);
		if (deleted == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 모델이 없습니다.");
		}
	}
	
	@Override
	@Transactional
	public void deleteFacility(String groupId, String facilityId) {
		
		int updated = facilityMapper.deleteFacility(groupId, facilityId);
		
		if (updated == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 시설물이 없거나 이미 삭제되었습니다.");
		}
	}
}
