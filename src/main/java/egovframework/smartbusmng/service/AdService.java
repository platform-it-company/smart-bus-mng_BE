package egovframework.smartbusmng.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import egovframework.smartbusmng.model.ad.Ad;

public interface AdService {

    int getAdCount(String timeZoneType, String cntType, String userGroupId);

    List<Ad> searchAds(int page, int size, String ad_tp, String ad_period, String ad_used, String userGroupId);

    int getSearchAdsCount(String ad_tp, String ad_period, String ad_used, String userGroupId);

    Ad getAdById(String adId, String userGroupId);

    void saveAd(Ad ad, MultipartFile upfile, String attachId, String fPath);

    void deleteAdByIdAttch(String selAdId, String attachmentCode);

    int getAdCnt();

//    List<Ad> getAllAds(String userGroupId);
}
