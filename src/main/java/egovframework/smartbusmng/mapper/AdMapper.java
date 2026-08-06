package egovframework.smartbusmng.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import egovframework.smartbusmng.model.ad.Ad;


@Mapper
public interface AdMapper {

    int getAdCount(@Param("timeZoneType") String timeZoneType, @Param("cntType") String cntType, 
			@Param("curDT") String curDT, @Param("userGroupId") String userGroupId);


	Ad getAdById(@Param("adId") String adId, @Param("userGroupId") String userGroupId);
    void insertAd(Ad ad);
    void updateAd(Ad ad);
          
// condition count
    List<Ad> searchAds(Map<String, Object> ad_params);
    
    int getSearchAdsCount(Map<String, Object> ad_params);
    
    int getAdCnt();
    
//	List<Ad> getAllAds(@Param("userGroupId") String userGroupId);
	
	// file delete part
	void deletePrefile(@Param("attachCode") String attachCode, @Param("entityId") String entityId, @Param("entityName") String entityName, @Param("attachId") String attachId);
	void deleteAdsById(@Param("selAdId") String selAdId);	
	void deleteAdsErPrById(@Param("selAdId") String selAdId);
	void deleteAttachment(@Param("attachmentCode")String attachmentCode, @Param("entityId") String entityId, 
		@Param("entityName") String entityName);
	String getAttchId(String attachmentCode, String entityId, String entityName);

	List<Ad> getCommonAds(@Param("facilityIds") List<String> facilityIds, 
			@Param("userGroupId") String userGroupId, @Param("faciltyCnt") int facilityCnt); 
	
}