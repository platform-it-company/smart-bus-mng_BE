package egovframework.smartbusmng.service;

import java.util.List;

import egovframework.smartbusmng.model.ad.Ad;
import egovframework.smartbusmng.model.facilityAd.FacilityAd;

public interface FacilityAdService {

	List<FacilityAd> getAdsByerId(String facilityId, String userGroupId);

	void saveFacilityAds(List<FacilityAd> facilityAds);

	List<Ad> getCommonAds(List<String> facilityIds, String userGroupId, int facilityCnt);

}