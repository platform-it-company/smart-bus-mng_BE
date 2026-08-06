package egovframework.smartbusmng.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import egovframework.smartbusmng.model.ad.Ad;
import egovframework.smartbusmng.model.facilityAd.FacilityAd;

@Mapper
public interface FacilityAdMapper {

	List<FacilityAd> getAdsByerId(@Param("facilityId") String facilityId, 
			@Param("userGroupId") String userGroupId);

	void updateFacilityAd(FacilityAd newAd);

	void insertFacilityAd(FacilityAd newAd);

	void deleteFacilityAd(@Param("facilityId") String facilityId,
            @Param("adId") String adId,@Param("groupId") String groupId);

	void deleteAdsByFacilityId(@Param("facilityId") String facilityId,
            @Param("userGroupId") String userGroupId);

    List<Ad> getCommonAds(@Param("facilityIds") List<String> facilityIds,
            @Param("userGroupId") String userGroupId, @Param("facilityCnt") int facilityCnt);

}