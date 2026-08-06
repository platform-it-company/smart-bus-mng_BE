package egovframework.smartbusmng.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.model.ApiResponse;
import egovframework.smartbusmng.model.ad.Ad;
import egovframework.smartbusmng.model.ad.FileAttachmentDto;
import egovframework.smartbusmng.model.ad.SearchResponse;
import egovframework.smartbusmng.service.AdService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/adMng")
public class AdController {

//	private static final Logger log = LoggerFactory.getLogger(AdController.class);
//	private final ObjectMapper om = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	
    private static final String DEFAULT_PAGE_SIZE = "15";

    @Value("${file.service.base.url}")
    private String fileServiceUrl;
    
	@Autowired
	private AdService adService;

	@Autowired
	private RestTemplate restTemplate;
    
	@GetMapping("")
	public ResponseEntity<Map<String, Object>> getAds(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
			@RequestParam(value = "userGroupId", required = false) String userGroupId) {

		// 1) 원래 컨트롤러에서 하던 통계
		int adCount = adService.getAdCount("all", "totalAd", userGroupId);
		int adOnelineCnt = adService.getAdCount("cur", "oneline", userGroupId);
		int adImageCnt = adService.getAdCount("cur", "Image", userGroupId);
		int adVodCnt = adService.getAdCount("cur", "vod", userGroupId);
		int adAnyCnt = adService.getAdCount("cur", "anyType", userGroupId);

		int adExpiredOneCnt = adService.getAdCount("expired", "oneline", userGroupId);
		int adExpiredImgCnt = adService.getAdCount("expired", "Image", userGroupId);
		int adExpiredVodCnt = adService.getAdCount("expired", "vod", userGroupId);
		int adExpiredAnyCnt = adService.getAdCount("expired", "anyType", userGroupId);

		int adWaitOneCnt = adService.getAdCount("wait", "oneline", userGroupId);
		int adWaitImgCnt = adService.getAdCount("wait", "Image", userGroupId);
		int adWaitVodCnt = adService.getAdCount("wait", "vod", userGroupId);
		int adWaitAnyCnt = adService.getAdCount("wait", "anyType", userGroupId);

		int adCurTotal = adOnelineCnt + adImageCnt + adVodCnt + adAnyCnt;
		int adExpiredTotal = adExpiredOneCnt + adExpiredImgCnt + adExpiredVodCnt + adExpiredAnyCnt;
		int adWaitTotal = adWaitOneCnt + adWaitVodCnt + adWaitImgCnt + adWaitAnyCnt;

		System.out.printf(
			    "[AD-DASH] total=%d | cur(one=%d,img=%d,vod=%d,any=%d,sum=%d) | " +
			    "expired(one=%d,img=%d,vod=%d,any=%d,sum=%d) | " +
			    "wait(one=%d,img=%d,vod=%d,any=%d,sum=%d) | userGroupId=%s%n",
			    adCount,
			    adOnelineCnt, adImageCnt, adVodCnt, adAnyCnt, adCurTotal,
			    adExpiredOneCnt, adExpiredImgCnt, adExpiredVodCnt, adExpiredAnyCnt, adExpiredTotal,
			    adWaitOneCnt, adWaitImgCnt, adWaitVodCnt, adWaitAnyCnt, adWaitTotal,
			    userGroupId
			);
		String groupId = SecurityUtil.getGroupId();

		// 3) JSON 으로 내려보낼 바디 구성
		Map<String, Object> body = new HashMap<>();
		// 통계
		body.put("adCount", adCount);
		body.put("adOnelineCnt", adOnelineCnt);
		body.put("adImageCnt", adImageCnt);
		body.put("adVodCnt", adVodCnt);
		body.put("adAnyCnt", adAnyCnt);
		body.put("adCurTotal", adCurTotal);

		body.put("adExpiredOneCnt", adExpiredOneCnt);
		body.put("adExpiredImgCnt", adExpiredImgCnt);
		body.put("adExpiredVodCnt", adExpiredVodCnt);
		body.put("adExpiredAnyCnt", adExpiredAnyCnt);
		body.put("adExpiredTotal", adExpiredTotal);

		body.put("adWaitOneCnt", adWaitOneCnt);
		body.put("adWaitImgCnt", adWaitImgCnt);
		body.put("adWaitVodCnt", adWaitVodCnt);
		body.put("adWaitAnyCnt", adWaitAnyCnt);
		body.put("adWaitTotal", adWaitTotal);

		// 사용자 정보
		body.put("userGroupId", userGroupId);
		body.put("groupId", groupId);

		return ResponseEntity.ok(body);
	}

	/**
	 * 단건 조회 GET /api/eroute/ads/{adId}
	 */
	@GetMapping("/ad/{adId}")
	public ResponseEntity<Map<String, Object>> getAd(@PathVariable String adId, 
			@RequestParam(value = "userGroupId", required = false) String userGroupId) {
		System.out.println("==================userGroupId:"+ userGroupId);
		Ad ad = adService.getAdById(adId, userGroupId);
		if (ad == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "not found"));
		}

		Map<String, Object> body = new HashMap<>();
		body.put("ad", ad);

		// 첨부파일 있으면 외부 API 호출해서 원본파일명도 넣어줌 (원래 코드 유지)
		if (ad.getAttchment() != null) {
			String attachmentCode = ad.getAttchment();
			String attachmentUrl = fileServiceUrl +"/"+ attachmentCode;

			try {
				ResponseEntity<ApiResponse<List<FileAttachmentDto>>> response =
				        restTemplate.exchange(
				                attachmentUrl,
				                HttpMethod.GET,
				                null,
				                new ParameterizedTypeReference<ApiResponse<List<FileAttachmentDto>>>() {}
				        );
	
				if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				    ApiResponse<List<FileAttachmentDto>> api = response.getBody();
				    List<FileAttachmentDto> list = api.getData();
				    if (list != null && !list.isEmpty()) {
				        FileAttachmentDto attachmentData = list.get(0);
				        
				        System.out.println("============attachmentData: "+attachmentData.getOriginalFileName()+","+attachmentData.getUniqueId()+","+attachmentData.getFileType());
				        body.put("attachOriginFN", attachmentData.getOriginalFileName());
				        body.put("attachId", attachmentData.getUniqueId());
				        body.put("attachFType", attachmentData.getFileType());
				    }
				}
			} catch (HttpStatusCodeException e) {
		        System.out.println("fileService 호출 실패: status=" + e.getStatusCode()
                + ", body=" + e.getResponseBodyAsString());
			} catch (Exception e) {
		        System.out.println("fileService 호출 중 알 수 없는 예외: " + e.getMessage());
			}
		}

		if (ad.getEventSd() != null) {
			String formEventSd = ad.getEventSd().substring(0, 4) + "-" + ad.getEventSd().substring(4, 6) + "-"
					+ ad.getEventSd().substring(6, 8);
			body.put("formEventSd", formEventSd);
		}
		if (ad.getEventEd() != null) {
			String formEventEd = ad.getEventEd().substring(0, 4) + "-" + ad.getEventEd().substring(4, 6) + "-"
					+ ad.getEventEd().substring(6, 8);
			body.put("formEventEd", formEventEd);
		}
		if (ad.getEventSt() != null) {
			String formEventSt = ad.getEventSt().substring(0, 2) + ":" + ad.getEventSt().substring(2, 4);
			body.put("formEventSt", formEventSt);
		}
		if (ad.getEventEt() != null) {
			String formEventEt = ad.getEventEt().substring(0, 2) + ":" + ad.getEventEt().substring(2, 4);
			body.put("formEventEt", formEventEt);
		}

		return ResponseEntity.ok(body);
	}

	@GetMapping("/ad/new")
	public ResponseEntity<Ad> newAd() {
		Ad ad = new Ad();
		ad.setAdTp("0");
		ad.setUseTp("N");
		ad.setAdDefault("N");
		return ResponseEntity.ok(ad);
	}

	/**
	 * 저장 POST /api/adMng/saveAd form-data 로 파일까지 받을 수 있게 해둔 버전
	 */
	@PostMapping("/saveAd")
	public ResponseEntity<Map<String, Object>> saveAd(
			@Valid @ModelAttribute Ad ad, 
			BindingResult result,
			@RequestParam(value = "upfile", required = false) MultipartFile upfile,
			@RequestParam(value = "fPath", required = false) String fPath,
			@RequestParam(value = "attachId", required = false) String attachId) {
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			response.put("status", "error");
			response.put("message", "유효성 검사 오류가 발생했습니다.");
			response.put("errors", result.getAllErrors().stream().map(error -> {
				Map<String, String> errorDetails = new HashMap<>();
				if (error instanceof org.springframework.validation.FieldError) {
					errorDetails.put("field", ((org.springframework.validation.FieldError) error).getField());
				}
				errorDetails.put("code", error.getCode());
				errorDetails.put("message", error.getDefaultMessage());
				return errorDetails;
			}).collect(Collectors.toList()));
			return ResponseEntity.badRequest().body(response);
		}
		
		try {
			if (attachId != null && attachId.length() < 2) {
				attachId = null;
			}

			adService.saveAd(ad, upfile, attachId, fPath);
			response.put("status", "success");
			response.put("message", "홍보물을 저장하였습니다.");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "error");
			response.put("message", "저장 중 오류가 발생하였습니다.");
			response.put("error", e.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/searchAds")
	public ResponseEntity<SearchResponse> searchAds(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
			@RequestParam(value = "ad_tp", required = false) String ad_tp,
			@RequestParam(value = "ad_period", required = false) String ad_period,
			@RequestParam(value = "ad_used", required = false) String ad_used,
			@RequestParam(value = "userGroupId", required = false) String userGroupId) {
		
		String userRole = SecurityUtil.getGroupRole();

		if ("ADMIN".equals(userRole) && (userGroupId == null || userGroupId.isBlank())) {
		    userGroupId = null;
		}
		
		List<Ad> ads = adService.searchAds(page, size, ad_tp, ad_period, ad_used, userGroupId);
		int totalAds = adService.getSearchAdsCount(ad_tp, ad_period, ad_used, userGroupId);
		int totalPg = (int) Math.ceil((double) totalAds / size);

		int curBlock = 0;
		if (page >= 2) {
			curBlock = (page - 1) / size;
		}

		int startPg = curBlock * size + 1;
		int endPg = Math.min(startPg + 9, totalPg);

		SearchResponse searchBody = new SearchResponse(
			ads, totalAds, page, size, totalPg, startPg, endPg, ad_tp, ad_period, ad_used 
		);
		
		return ResponseEntity.ok(searchBody);
		
	}

	@PostMapping("/deleteAds")
	public ResponseEntity<?> deleteAds(@RequestBody List<Map<String, String>> selectedAds) {
		try {
			for (Map<String, String> ad : selectedAds) {
				String adId = ad.get("adId");
				String attchment = ad.get("attchment");
				adService.deleteAdByIdAttch(adId, attchment);
			}
			return ResponseEntity.ok(Map.of("message", "삭제 성공"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "삭제 실패", "error", e.getMessage()));
		}
	}
	
//    private String normalizeGroupIdForRole(String groupId, String role) {
//        if ("ADMIN".equals(role) && (groupId == null || groupId.isBlank())) {
//            return null; // ADMIN이며 지정 없으면 전체
//        }
//        return groupId;
//    }
}
