package egovframework.smartbusmng.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.mapper.AdMapper;
import egovframework.smartbusmng.model.ad.Ad;

@Service
public class AdServiceImpl implements AdService {

	private static final Logger logger = LoggerFactory.getLogger(AdService.class);

	@Value("${file.service.base.url}")
	private String fileServiceUrl;
	
	@Value("${project.name}")
	private String pName;
	
	@Value("${ad.entity.id}")
	private String adEntityId;
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AdMapper adMapper;

	@Transactional
	public int getAdCount(String timeZoneType, String cntType, String userGroupId) {
		String groupId = SecurityUtil.getGroupId();

		if ((userGroupId == null || userGroupId.isEmpty()) && groupId != null && !"100000000".equals(groupId)) {
			userGroupId = groupId;
		}

		String curDt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

		return adMapper.getAdCount(timeZoneType, cntType, curDt, userGroupId);
	}

	@Transactional
	public List<Ad> searchAds(int page, int size, String ad_tp, String ad_period, String ad_used, String userGroupId) {

		String groupId = SecurityUtil.getGroupId();

		int offset = (page - 1) * size;
		int limit = size;

		String curDT = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
//    	String curDT = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));    	
		Map<String, Object> ad_params = new HashMap<>();
		ad_params.put("offset", offset);
		ad_params.put("limit", limit);
		ad_params.put("ad_tp", ad_tp);
		ad_params.put("ad_period", ad_period);
		ad_params.put("ad_used", ad_used);
		ad_params.put("curDT", curDT);
		ad_params.put("groupId", groupId);
		ad_params.put("userGroupId", userGroupId);
		return adMapper.searchAds(ad_params);
	}

	@Transactional
	public int getSearchAdsCount(String ad_tp, String ad_period, String ad_used, String userGroupId) {
		String curDT = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

		String groupId = SecurityUtil.getGroupId();

		Map<String, Object> ad_params = new HashMap<>();
		ad_params.put("ad_tp", ad_tp);
		ad_params.put("ad_period", ad_period);
		ad_params.put("ad_used", ad_used);
		ad_params.put("curDT", curDT);
		ad_params.put("groupId", groupId);
		ad_params.put("userId", userGroupId);

		return adMapper.getSearchAdsCount(ad_params);
	}

//getAdById
	@Transactional
	public Ad getAdById(String adId, String userGroupId) {
		return adMapper.getAdById(adId, userGroupId);
	}

//saveAd
	@Transactional
	public void saveAd(Ad ad, MultipartFile upfile, String attachId, String fPath) {
		try {
			String groupId = SecurityUtil.getGroupId();
			String user = SecurityUtil.getUserName();
			if (upfile != null && !upfile.isEmpty()) {
				String entityName = pName;
				String entityId = adEntityId;
				String userName = user;
			
		        String uploadRequestDtoJson = String.format(
		                "{\"entityName\":\"%s\", \"entityId\":\"%s\", \"userName\":\"%s\"}",
		                entityName, entityId, userName
		        );
		        
				if (attachId == null) {
					String uploadFileCode = uploadFileToAttchSvc(upfile, uploadRequestDtoJson);
					System.out.println("uploadFilecode:"+uploadFileCode);
					ad.setAttchment(uploadFileCode);
				} else if (attachId != null) {
					// 기존 파일과 비교하여 변경 여부 확인
					String url = fileServiceUrl + "/" + ad.getAttchment();

					ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
					if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
						Map<String, Object> existFileInfo = (Map<String, Object>)
								((List<Object>) response.getBody().get("data")).get(0);
						String existingFileName = (String) existFileInfo.get("originalFileName");
						long existingFileSize = ((Number) existFileInfo.get("size")).longValue();
						String existingFileType = (String) existFileInfo.get("fileType");

						String newFileName = upfile.getOriginalFilename();
						long newFileSize = upfile.getSize();
						String newFileType = upfile.getContentType();

						// 파일 변경 여부 확인
						if (!existingFileName.equals(newFileName) || existingFileSize != newFileSize
								|| !existingFileType.equals(newFileType)) {
							// 파일 업데이트
							updateFileToAttchSvc(upfile, uploadRequestDtoJson, ad.getAttchment(), attachId, entityName,
									entityId);
						}
					}
				}
			} else if (upfile == null || upfile.isEmpty()) {
				System.out.println("==============fPath : " + fPath);
				if (attachId != null) {
					if (fPath.length() != 0) {
						System.out.println("fpath not null : " + fPath);
						ad.setAttchment(ad.getAttchment());
					} else if (fPath.length() == 0) {
						System.out.println("fpath null");
						ad.setAttchment(null);
					}
				}
			}

			if (ad.getEventSd() != null) {
				String temp_eventSd = ad.getEventSd().replace("-", "");
				ad.setEventSd(temp_eventSd);
			}

			if (ad.getEventEd() != null) {
				String temp_eventEd = ad.getEventEd().replace("-", "");
				ad.setEventEd(temp_eventEd);
			}

			if (ad.getEventSt() != null) {
				String temp_eventSt = ad.getEventSt().replace(":", "");
				ad.setEventSt(temp_eventSt);
			}

			if (ad.getEventEt() != null) {
				String temp_eventEt = ad.getEventEt().replace(":", "");
				ad.setEventEt(temp_eventEt);
			}

//            DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//            String curDate = LocalDate.now().format(dateformatter);

			if (ad.getAdId() == null || ad.getAdId().isEmpty()) {
				ad.setAdId(generateId("AD"));
				ad.setFrstUserId(groupId);
				ad.setFrstRegistProgrm("smartbusmng");
				ad.setGroupId(groupId);

				try {
					adMapper.insertAd(ad);
				} catch (Exception e) {
					throw new RuntimeException("데이터베이스 저장 중 오류가 발생했습니다.", e);
				}
			} else {
				ad.setLastUserId(groupId);
				ad.setLastRegistProgrm("eroutemng");

				adMapper.updateAd(ad);

			}
		} catch (IOException e) {
			throw new RuntimeException("데이터베이스 저장 중 오류가 발생했습니다.", e);
		}

	}

	@Transactional
	private void updateFileToAttchSvc(MultipartFile upfile, String uploadRequestDtoJson, String attachCode,
			String attachId, String entityName, String entityId) throws IOException {
		// HTTP 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	    
		// 파일을 ByteArrayResource로 단순하게
	    ByteArrayResource fileResource = new ByteArrayResource(upfile.getBytes()) {
	        @Override
	        public String getFilename() {
	            return upfile.getOriginalFilename();
	        }
	    };
		// JSON 데이터 담기 위한 HttpEntity
//		HttpHeaders jsonHeaders = new HttpHeaders();
//		jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> jsonEntity = new HttpEntity<>(uploadRequestDtoJson, jsonHeaders);

		String listJson = String.format("[{\"uniqueId\":\"%s\", \"delete\":true}]", attachId);

		// 요청 본문 설정
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//		body.add("files", new MultipartInputStreamFileResource(upfile.getInputStream(), upfile.getOriginalFilename()));
//		body.add("info", jsonEntity);
		body.add("files", fileResource);
		body.add("info", uploadRequestDtoJson);
		body.add("list", listJson);

		// 기존 파일 삭제 정보 설정
//		String listJson = String.format("[{\"uniqueId\":\"%s\", \"delete\":true}]", attachId);
//		HttpEntity<String> listEntity = new HttpEntity<>(listJson, jsonHeaders);
//		body.add("list", listEntity);

		// HTTP 요청 생성
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		// 외부 endpoint 호출 (PUT 방식)
		String url = fileServiceUrl + "/upload/" + attachCode;

	    logger.info("📤 updateFileToAttchSvc PUT {}", url);
	    logger.info("   - fileName: {}, size: {}", upfile.getOriginalFilename(), upfile.getSize());
	    logger.info("   - deleteList: {}", listJson);
	    
		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Map.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new IOException("첨부파일을 업데이트하는데 실패했습니다.");
		} else if (response.getStatusCode() == HttpStatus.OK) {
			adMapper.deletePrefile(attachCode, entityId, entityName, attachId);
		}

		logger.info("File updated for attachment ID: {}", attachId);
	}

	@Transactional
	private String uploadFileToAttchSvc(MultipartFile upfile, String uploadRequestDtoJson) throws IOException {
		// HTTP 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

	    ByteArrayResource fileResource = new ByteArrayResource(upfile.getBytes()) {
	        @Override
	        public String getFilename() {
	            return upfile.getOriginalFilename();
	        }
	    };
	    
		// Json 데이터 담기 위한 HttpEntity
//		HttpHeaders jsonHeaders = new HttpHeaders();
//		jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> jsonEntity = new HttpEntity<>(uploadRequestDtoJson, jsonHeaders);

		// 요청 본문 설정
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//		body.add("files", new MultipartInputStreamFileResource(upfile.getInputStream(), upfile.getOriginalFilename()));
		body.add("files", fileResource);
//		body.add("info", jsonEntity);
		body.add("info", uploadRequestDtoJson);

		// HTTP 요청 생성
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		// 외부 endpoint 호출
		String url = fileServiceUrl + "/upload";
	    logger.info("📤 uploadFileToAttchSvc POST {}", url);
	    logger.info("   - fileName: {}, size: {}", upfile.getOriginalFilename(), upfile.getSize());
	    
//		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
//
//		if (response.getStatusCode() != HttpStatus.CREATED && response.getStatusCode() != HttpStatus.OK) {
//			throw new IOException("파일을 첨부하는데 실패하였습니다.");
//		}
	    ResponseEntity<Map> response =
	            restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

	    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
	        throw new IOException("파일을 첨부하는데 실패하였습니다. status=" + response.getStatusCode());
	    }
	    
		Map<String, Object> responseBody = response.getBody();
				
		if (responseBody != null && responseBody.get("data") != null) {
			return responseBody.get("data").toString();
		} else {
			throw new IOException("첨부기능에 대한 응답에 오류가 있습니다.");
		}

	}

//커스텀 id 생성 메소드
	public String generateId(String preId) {
		String id_timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(new Date());
		String temp_id = preId + id_timestamp;
		return temp_id;
	}

	@Transactional
	public int getAdCnt() {
		return adMapper.getAdCnt();
	}

//	@Transactional
//	public List<Ad> getAllAds(String userGroupId) {
//		return adMapper.getAllAds(userGroupId);
//	}

	@Transactional
	public void deleteAdByIdAttch(String selAdId, String attachmentCode) {
		adMapper.deleteAdsById(selAdId); // promotion 테이블에서 삭제
		adMapper.deleteAdsErPrById(selAdId); // eroute-pr 테이블에서 삭제

		String userName = SecurityUtil.getUserName();
		
		if (attachmentCode != null) {
			String entityName = pName;
			String entityId = adEntityId;
			String attchId = adMapper.getAttchId(attachmentCode, entityId, entityName);

			System.out.println("=======username:"+userName);
			if (attchId != null) {
				String deleteUrl = fileServiceUrl + "/id/" + attchId + "?userName=" + userName;
				System.out.println("========== delete url : "+deleteUrl);
				try {
					ResponseEntity<Void> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, null,
							Void.class);

					// 성공적으로 삭제된 경우 상태코드 204 또는 200을 확인
					if (response.getStatusCode() != HttpStatus.NO_CONTENT
							&& response.getStatusCode() != HttpStatus.OK) {
						throw new RuntimeException("홍보 파일 삭제 중............ 오류 발생하였습니다.: " + response.getStatusCode());
					}

					logger.info("홍보 파일 삭제 성공: {}", attachmentCode);
				} catch (Exception e) {
					logger.error("홍보 파일 삭제 실패: {}", attachmentCode, e);
					throw new RuntimeException("파일 삭제 중 오류가.. 발생했습니다.", e);
				}
			}
			adMapper.deleteAttachment(attachmentCode, entityId, entityName);
		}
	}
}
