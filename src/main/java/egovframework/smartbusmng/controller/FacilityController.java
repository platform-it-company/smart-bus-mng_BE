package egovframework.smartbusmng.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.model.common.record.InitRouteListItem;
import egovframework.smartbusmng.model.common.record.InitVehListItem;
import egovframework.smartbusmng.model.facility.DeleteFacility;
import egovframework.smartbusmng.model.facility.FacilityDto;
import egovframework.smartbusmng.model.facility.FacilityVehInfoDto;
import egovframework.smartbusmng.model.facility.ProductInfoDto;
import egovframework.smartbusmng.model.facility.record.BusesInRoute;
import egovframework.smartbusmng.model.facility.record.FacilityDashInfo;
import egovframework.smartbusmng.model.facility.record.FacilityInfoRec;
import egovframework.smartbusmng.model.facility.record.FacilityMngInit;
import egovframework.smartbusmng.model.facility.record.ModelDto;
import egovframework.smartbusmng.model.facility.record.ModelSpecDto;
import egovframework.smartbusmng.model.facility.record.PreAddFacilityRec;
import egovframework.smartbusmng.model.route.BusBeanDto;
import egovframework.smartbusmng.service.CommonApiService;
import egovframework.smartbusmng.service.FacilityService;
import egovframework.smartbusmng.service.RouteService;

@RestController
@RequestMapping("/api/facilityMng")
public class FacilityController {

	@Autowired
	private RouteService routeService;
	
	@Autowired
	private FacilityService facilityService;
	
	@Autowired
	private CommonApiService commonApiService;
	
	@GetMapping("")
	public ResponseEntity<FacilityMngInit> facilityMng(
							@RequestParam(value = "userGroupId", required = false) String userGroupId) {
		
		final String userRole = SecurityUtil.getGroupRole();
		
		if ("ADMIN".equals(userRole) && (userGroupId == null || userGroupId.isBlank())) {
			userGroupId = null;
		}

        FacilityDashInfo info = buildDashInfo(userGroupId);

        List<InitRouteListItem> initRoutes = commonApiService.getInitRouteList(userGroupId);
        List<InitVehListItem> initVehicles = commonApiService.getInitVehList(userGroupId);
        
        FacilityMngInit facilityMngInit = new FacilityMngInit(info, initRoutes, initVehicles);
        
        return ResponseEntity.ok(facilityMngInit);
	}

	@GetMapping("/searchEr")
	@ResponseBody
	public ResponseEntity<List<FacilityVehInfoDto>> searchFacilityList(
			@RequestParam(name="selRouteId", required = false, defaultValue="0") String selRouteId,
			@RequestParam(name="selRouteVer", required = false, defaultValue="0") int selRouteVer,
			@RequestParam(name="selVehId", required = false, defaultValue="0") int selVehId,
			@RequestParam(name="selState", required = false, defaultValue="99999") int selState,
			@RequestParam(name="viewType", required = false, defaultValue="0") int viewType,
			@RequestParam(name="useTp", required = false, defaultValue="Y") String useTp,
			@RequestParam(name="userGroupId", required = false) String userGroupId
	) {
		String userRole = SecurityUtil.getGroupRole();

		if ("ADMIN".equals(userRole) && (userGroupId == null || userGroupId.isBlank())) {
			userGroupId = null;
		}
		
		List<FacilityVehInfoDto> facilityList = facilityService.searchFaByVehIds(selRouteId, selRouteVer, selVehId, selState, viewType, useTp, userGroupId);
				
		System.out.println("FacilityVehInfoDto.size() : "+facilityList.size());
		return ResponseEntity.ok(facilityList);

	}

	@PostMapping("/save")
	public ResponseEntity<?> saveFacility(
//		@ModelAttribute FacilityDto dto,
		@RequestBody FacilityDto dto,
		@RequestParam(name="userGroupId", required = false) String userGroupId
	) {
		
		System.out.println("facilityId = " + dto.getFacilityId());
		System.out.println("modelId    = " + dto.getModelId());
		System.out.println("modelNm    = " + dto.getModelNm());
		System.out.println("manufacturer = " + dto.getManufacturer());
		System.out.println("unitPrice  = " + dto.getUnitPrice());
		System.out.println("installDe  = " + dto.getInstallDe());
		System.out.println("displayRes = " + dto.getDisplayRes());
		System.out.println("routeMapRes = " + dto.getRouteMapRes());
		System.out.println("adAreaRes   = " + dto.getAdAreaRes());
		System.out.println("useTp      = " + dto.getUseTp());
		System.out.println("statCd     = " + dto.getStatCd());
		System.out.println("vehId      = " + dto.getVehId());
		
		Map<String, Object> body = new HashMap<>();
		
		try {
			String groupId = (userGroupId != null && !userGroupId.isBlank()) ? userGroupId : SecurityUtil.getGroupId();
			System.out.println("groupId:"+groupId+userGroupId);
			dto.setGroupId(groupId);
			
			if (dto.getUseTp() == null) {
				dto.setUseTp("Y");
			}
			if (dto.getStatCd() == null) {
				dto.setStatCd("0");
			}
			
			System.out.println("before ad");
			facilityService.saveFaciltiy(dto);
			
			body.put("success", true);
			body.put("facilityId", dto.getFacilityId());
			
			return ResponseEntity.ok(body);
		} catch (Exception e) {
			body.put("success",  false);
			body.put("message",  e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
		}
	}
	
	@PostMapping("/facilityInfo")
	public ResponseEntity<FacilityInfoRec> viewFacilityInfo(@RequestParam("facilityId") String facilityId,
							@RequestParam("userGroupId") String userGroupId) {
		
		FacilityDto facilityInfo = facilityService.getFaById(facilityId, userGroupId);
		
		if (facilityInfo == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "시설 정보를 찾을 수 없습니다.");
		}
		
		String modelId = facilityInfo.getModelId();
		System.out.println("===========modelId: "+modelId);
		String groupId = userGroupId;
		
		if (modelId != null && !modelId.isBlank()) {
			ModelSpecDto spec = facilityService.getModelDetail(groupId, modelId);
			if (spec != null) {
				facilityInfo.setModelNm(spec.modelNm());
				facilityInfo.setModelId(modelId);
				facilityInfo.setGroupId(groupId);
				facilityInfo.setManufacturer(spec.manufacturer());
				facilityInfo.setUnitPrice(spec.unitPrice());
				facilityInfo.setDisplayRes(spec.displayRes());
				facilityInfo.setRouteMapRes(spec.routeMapRes());
				facilityInfo.setAdAreaRes(spec.adAreaRes());
			}
		}
		List<BusBeanDto> busList = routeService.getAllBusBeans(userGroupId);
		
		FacilityInfoRec response = new FacilityInfoRec(facilityInfo, busList);
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("/addFacility")
	public ResponseEntity<PreAddFacilityRec> addFaForm() {
		String userGroupId = SecurityUtil.getGroupId();
		String userRole = SecurityUtil.getGroupRole();
		
        if (userGroupId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        
		if ("ADMIN".equals(userRole) && (userGroupId == null || userGroupId.isBlank())) {
			userGroupId = null;
		}

        FacilityDashInfo faDashInfo = buildDashInfo(userGroupId);		
		List<String> manufacturers = facilityService.getManufactureList(userGroupId);
    	
		return ResponseEntity.ok(new PreAddFacilityRec(faDashInfo, manufacturers));
	}
	
	@PostMapping("/deleteFacility")
	public ResponseEntity<Map<String, Object>> deleteFacility(
			@RequestBody DeleteFacility deleteItem
//			@RequestParam("userGroupId") String userGroupId,
//			@RequestParam("facilityId") String facilityId
	) {
    	Map<String, Object> body = new HashMap<>();
    	System.out.println("==========groupId:"+deleteItem.getUserGroupId()+", facilityId:"+deleteItem.getFacilityId());
    	final String groupId = SecurityUtil.getGroupId();

        if (!groupId.equals(deleteItem.getUserGroupId())) {
            body.put("success", false);
            body.put("message", "삭제할 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        }
        
        try {
            facilityService.deleteFacility(groupId, deleteItem.getFacilityId());
            body.put("success", true);
            return ResponseEntity.ok(body);

        } catch (ResponseStatusException e) {
            body.put("success", false);
            body.put("message", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(body);

        } catch (Exception e) {
            body.put("success", false);
            body.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
		
//	@PostMatting("/saveFacility")
	@PostMapping("/saveModel")
	public ResponseEntity<?> saveModel(@RequestBody ProductInfoDto dto) {
        Map<String, Object> body = new HashMap<>();

		System.out.println("modelId    = " + dto.getModelId());
		System.out.println("modelNm    = " + dto.getModelNm());
		System.out.println("manufacturer = " + dto.getManufacturer());
		System.out.println("unitPrice  = " + dto.getUnitPrice());
		System.out.println("displayRes = " + dto.getDisplayRes());
		System.out.println("routeMapRes = " + dto.getRouteMapRes());
		System.out.println("adAreaRes   = " + dto.getAdAreaRes());
		
        try {
			facilityService.saveModel(dto);
	        body.put("success", true);
	        body.put("modelId", dto.getModelId());
	        body.put("modelNm", dto.getModelNm());
	        
	        return ResponseEntity.ok(body);
		} catch (Exception e) {
	        body.put("success", false);
	        body.put("message", e.getMessage());
	        
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
		}
	}
	
    @GetMapping("/products/models")
    public List<ModelDto> modelsByManufacturer(
        @RequestParam("manufacturer") String manufacturer,
        @RequestParam(name="groupId", required = false) String groupId
    ) {
        String gId = (groupId != null && !groupId.isBlank()) ? groupId : SecurityUtil.getGroupId();
        return facilityService.getModelsByManufacturer(gId, manufacturer);
    }
    
    @PostMapping("/products/getModelDetail")
    public ModelSpecDto getModelDetail(@RequestParam(name="modelId") String modelId, 
    		@RequestParam(name="groupId", required = false) String groupId) {
    	String gId = (groupId != null && !groupId.isBlank()) ? groupId : SecurityUtil.getGroupId();
    	return facilityService.getModelDetail(gId, modelId);
    }

    private FacilityDashInfo buildDashInfo(String userGroupId) {
    	int totalErCnt = facilityService.getFaCount("totalEr", userGroupId);
    	int normalErCnt = facilityService.getFaCount("normal", userGroupId);
    	int rebootErCnt = facilityService.getFaCount("reboot", userGroupId);
    	int scrOffErCnt = facilityService.getFaCount("scrOff", userGroupId);
    	int defaultErCnt = facilityService.getFaCount("default", userGroupId);
    	int faultErCnt = facilityService.getFaCount("fault", userGroupId);
    	int disconErCnt = facilityService.getFaCount("discon", userGroupId);
    	int testErCnt = facilityService.getFaCount("test", userGroupId);
		int emergencyErCnt = facilityService.getFaCount("emergency", userGroupId);

		System.out.println("============groupId:"+userGroupId);

        int errTotal = faultErCnt + disconErCnt;
        int controlTotal = rebootErCnt + scrOffErCnt + defaultErCnt + testErCnt + emergencyErCnt;

    	List<BusBeanDto> busList = routeService.getAllBusBeans(userGroupId);
        System.out.println("[facilityContyroller] busList.size:"+busList.size());

        FacilityDashInfo info = new FacilityDashInfo(
            totalErCnt, normalErCnt, rebootErCnt, scrOffErCnt, defaultErCnt,
            faultErCnt, disconErCnt, testErCnt, emergencyErCnt,
            errTotal, controlTotal, busList
        );

        return info;
    }
    
    @DeleteMapping("/deleteModel")
    public ResponseEntity<Map<String, Object>> deleteModel(
    	@RequestParam("modelId") String modelId, @RequestParam("userGroupId") String userGroupId
	) {
    	Map<String, Object> body = new HashMap<>();
    	final String groupId = SecurityUtil.getGroupId();
    	
        if (!groupId.equals(userGroupId)) {
            body.put("success", false);
            body.put("message", "삭제할 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        }
        
        try {
            facilityService.deleteModel(groupId, modelId);
            body.put("success", true);
            return ResponseEntity.ok(body);

        } catch (ResponseStatusException e) {
            body.put("success", false);
            body.put("message", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(body);

        } catch (Exception e) {
            body.put("success", false);
            body.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
    
    @GetMapping("/updateCmbBus")
    public List<BusesInRoute> updateCmbBus(@RequestParam("selRouteId") String selRouteId,
    	@RequestParam("selRouteVer") int selRouteVer,
    	@RequestParam("userGroupId") String userGroupId
    ) {
    	return facilityService.getBusesInRoute(selRouteId, selRouteVer, userGroupId);
    }
    
    @GetMapping("/udpateCmbAllBuses")
    public List<InitVehListItem> updateCmbAllBuses(@RequestParam(value = "userGroupId") String userGroupId) {
    	return commonApiService.getInitVehList(userGroupId);
    }
}