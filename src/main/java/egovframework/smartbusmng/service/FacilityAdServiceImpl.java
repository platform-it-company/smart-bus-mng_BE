package egovframework.smartbusmng.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.mapper.FacilityAdMapper;
import egovframework.smartbusmng.model.ad.Ad;
import egovframework.smartbusmng.model.facilityAd.FacilityAd;

@Service
public class FacilityAdServiceImpl implements FacilityAdService {

    private static final Logger logger = LoggerFactory.getLogger(FacilityAdServiceImpl.class);

    @Autowired
    private FacilityAdMapper facilityAdMapper;
    
    @Transactional(readOnly=true)
	public List<FacilityAd> getAdsByerId(String facilityId, String userGroupId) {
    	return facilityAdMapper.getAdsByerId(facilityId, userGroupId);
	}
	
    /**
     * 시설물-홍보물 매핑 전체 저장 (upsert + 삭제)
     *
     * - 같은 facilityId 끼리 묶어서 처리
     * - adId 가 "no"인 row 가 들어오면, 해당 facilityId 에 대한 매핑 전체 삭제
     * - 그 외에는 (facilityId + adId) 기준으로 upsert 후, 나머지는 삭제
     */
    @Transactional
 	public void saveFacilityAds(List<FacilityAd> facilityAds) {
         if (facilityAds == null || facilityAds.isEmpty()) {
             logger.debug("saveFacilityAds 호출됨: 넘어온 FacilityAds 가 비어있음");
             return;
         }
         
         String user = SecurityUtil.getUserName();
         // facilityId 기준으로 그룹핑
         Map<String, List<FacilityAd>> grouped = facilityAds.stream()
                 .collect(Collectors.groupingBy(FacilityAd::getFacilityId));
         
// 	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
// 	    String curDate = LocalDate.now().format(dateFormatter);
 	
 	    for (Map.Entry<String, List<FacilityAd>> entry : grouped.entrySet()) {
 	        String facilityId = entry.getKey();
 	        List<FacilityAd> newAds = entry.getValue();
 	
 	        // groupId는 넘어오는 FacilityAd 안에 있다고 가정
 	        String userGroupId = newAds.stream()
 	                .map(FacilityAd::getGroupId) 
 	                .filter(g -> g != null && !g.isBlank())
 	                .findFirst()
 	                .orElse(null);
 	
 	        logger.debug("Processing facilityId: {}, userGroupId: {}", facilityId, userGroupId);
 	        logger.debug("New FacilityAds: {}", newAds);
 	
 	        // 기존 매핑 읽기
 	        List<FacilityAd> existingAds = facilityAdMapper.getAdsByerId(facilityId, userGroupId);
 	        logger.debug("Existing FacilityAds: {}", existingAds);
 	
 	        // 기존 매핑을 key=adId 로 Map으로 변환
 	        Map<String, FacilityAd> existingMap = existingAds.stream()
 	                .collect(Collectors.toMap(FacilityAd::getAdId, fa -> fa));
 	
 	        // 새로 들어온 key 세트
 	        Set<String> newKeys = new HashSet<>();
 	
 	        // 1) adId가 "no"인 경우: 해당 facilityId에 대한 전체 매핑 삭제
 	        boolean deleteAllForFacility = newAds.stream()
 	                .anyMatch(ad -> "no".equals(ad.getAdId()));
 	
 	        if (deleteAllForFacility) {
 	            facilityAdMapper.deleteAdsByFacilityId(facilityId, userGroupId);
 	            logger.debug("Deleted all FacilityAds for facilityId: {}, userGroupId: {}", facilityId, userGroupId);
 	            continue; // 이 facilityId는 더 이상 처리 안 함
 	        }
 	
 	        // 2) upsert 처리
 	        for (FacilityAd newAd : newAds) {
 	            // adId가 비거나 "no"인 데이터는 무시
                 String adId = newAd.getAdId();
                 if (adId == null || adId.isBlank() || "no".equals(adId)) {
                     continue;
                 }

                 String key = adId;
 	            newKeys.add(key);
 	
 	            if (existingMap.containsKey(key)) {
 	                // UPDATE
// 	                newAd.setLastUpdtDt(curDate);
 	                newAd.setLastUserId(user);
 	                newAd.setLastRegistProgrm("smartbusmng");
 	
 	                facilityAdMapper.updateFacilityAd(newAd);
 	                existingMap.remove(key);
 	                logger.debug("Updated FacilityAd: {}", newAd);
 	            } else {
 	                // INSERT
// 	                newAd.setFrstRegistDt(curDate);
 	                newAd.setFrstUserId(user);
 	                newAd.setFrstRegistProgrm("smartbusmng");
 	
 	                facilityAdMapper.insertFacilityAd(newAd);
 	                logger.debug("Inserted FacilityAd: {}", newAd);
 	            }
 	        }
 	
 	        // 3) existingMap에 남은 것들 중 newKeys에 없는 것 → 삭제
 	        for (FacilityAd remaining : existingMap.values()) {
 	            String key = remaining.getAdId();
 	            if (!newKeys.contains(key)) {
 	                facilityAdMapper.deleteFacilityAd(
 	                        remaining.getFacilityId(),
 	                        remaining.getAdId(),
 	                        remaining.getGroupId()
 	                );
 					logger.debug("Deleted FacilityAd: {}", remaining);

 	            }
 	        }
 	    }
 	
 	    logger.debug("Completed saveFacilityAds()");
 	}
    
	public List<Ad> getCommonAds(List<String> facilityIds, String userGroupId, int facilityCnt) {
		return facilityAdMapper.getCommonAds(facilityIds, userGroupId, facilityCnt);
	}

}