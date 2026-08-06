package egovframework.smartbusmng.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.model.ad.Ad;
import egovframework.smartbusmng.model.facilityAd.FacilityAd;
import egovframework.smartbusmng.service.AdService;
import egovframework.smartbusmng.service.FacilityAdService;

@RestController
@RequestMapping("/api/facilityAd")
public class FacilityAdController {

//    private static final String DEFAULT_PAGE_SIZE_STR = "6";
//    private static final int DEFAULT_PAGE_SIZE = 6;
    
    @Autowired
    private AdService adService;

    @Autowired
    private FacilityAdService facilityAdService;

    // api/adMng/searchAds 이용
//    @GetMapping("/getAdList")
//    public Map<String, Object> getAdList(
//            @RequestParam(value = "page", defaultValue = "1") int page,
//            @RequestParam(value = "size", defaultValue = "6") int size,
//            @RequestParam(value = "ad_tp", required = false) String ad_tp,
//            @RequestParam(value = "ad_period", required = false) String ad_period,
//            @RequestParam(value = "ad_used", required = false) String ad_used,
//            @RequestParam(value = "userGroupId", required = false) String userGroupId
//        ) {
//
//    	String groupId = SecurityUtil.getGroupId();
//    	if (userGroupId == null || userGroupId.isBlank()) {
//    		userGroupId = groupId;
//    	}
//    	
//        List<Ad> ads = adService.searchAds(page, size, ad_tp, ad_period, ad_used, userGroupId);
//        int totalCount = adService.getSearchAdsCount(ad_tp, ad_period, ad_used, userGroupId);
//        int totalPg = (int) Math.ceil((double) totalCount / size);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("ads", ads);
//        response.put("totalPg", totalPg);
//        response.put("totalCount", totalCount);
//
//        return response;
//    }

    @GetMapping("/getFacilityAdList")
    public List<Ad> getFacilityAdList(
            @RequestParam("facilityId") String facilityId,
            @RequestParam(value ="userGroupId", required = false) String userGroupId,
            @RequestParam("page") int page) {

    	//front에서 반드시 usergroupid를 체크할 것.
    	if (userGroupId == null || userGroupId.isBlank()) {
    		userGroupId = SecurityUtil.getGroupId();
    	}
    	
        List<FacilityAd> facilityAds = facilityAdService.getAdsByerId(facilityId, userGroupId);

        List<Ad> ads = new ArrayList<>();
        for (FacilityAd facilityAd : facilityAds) {
            Ad ad = adService.getAdById(facilityAd.getAdId(), userGroupId);
            if (ad != null) {
                ads.add(ad);
            }
        }

        return ads;
    }

    @PostMapping("/saveFacilityAds")
    public ResponseEntity<String> saveFacilityAds(@RequestBody List<FacilityAd> facilityAds) {
        try {
            if (facilityAds == null || facilityAds.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("저장할 홍보물 매핑 데이터가 없습니다.");
            }
            
            String curGroupId = SecurityUtil.getGroupId();
            
            String requestGroupId = facilityAds.stream()
                    .map(FacilityAd::getGroupId)
                    .filter(g -> g != null && !g.isBlank())
                    .findFirst()
                    .orElse(null);
            
            if (requestGroupId == null) {
                return ResponseEntity
                        .badRequest()
                        .body("요청 데이터에 groupId 정보가 없습니다. 다시 시도해주세요.");
            }
            
            if (!requestGroupId.equals(curGroupId)) {
            	return ResponseEntity
            			.badRequest()
            			.body("홍보물관리 설정할 권한이 없습니다. 확인해주세요.");
            }
            
            facilityAdService.saveFacilityAds(facilityAds);
            
            return ResponseEntity.ok("시설물에 대한 홍보물 정보를 저장하였습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("저장 중 오류가 발생했습니다. ");
        }
    }

    
    @PostMapping("/getCommonAds")
    public ResponseEntity<List<Ad>> getCommonAds(@RequestParam("facilityIds") List<String> facilityIds,
	    @RequestParam(value = "userGroupId", required = false) String userGroupId
    ){
    	int facilityCnt = facilityIds.size();
    	List<Ad> ads = facilityAdService.getCommonAds(facilityIds, userGroupId, facilityCnt); 
    	
    	if (ads == null) {
    		ads = Collections.emptyList();
    	}


    	   System.out.println("=== ads size   = " + ads.size());
    	   System.out.println("=== adIds      = " +
    	       ads.stream().map(Ad::getAdId).filter(Objects::nonNull).toList()
    	   );
    	return ResponseEntity.ok(ads);
    }
}