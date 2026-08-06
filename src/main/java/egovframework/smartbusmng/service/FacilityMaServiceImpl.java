package egovframework.smartbusmng.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import egovframework.smartbusmng.mapper.FacilityMaMapper;
import egovframework.smartbusmng.model.facilityMa.FacilityMaDto;
import egovframework.smartbusmng.model.facilityMa.FacilityMaForm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacilityMaServiceImpl implements FacilityMaService {
	
    private static final Logger log = LoggerFactory.getLogger(FacilityMaService.class);
    
    @Value("${file.service.base.url}")
    private String fileServiceUrl;          // ex) http://10.0.7.100:30101/api/v1/attachments

    @Value("${project.name}")
    private String projectName;             // eroutemng 같은 프로젝트명

    @Value("${ma.entity.id}")        // 유지관리용 entityId (yml에 추가)
    private String faMaEntityId;        // ex) "MA001"

    @Autowired
    private RestTemplate restTemplate;
    
	@Autowired
	private FacilityMaMapper facilityMaMapper;
	
    private SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Transactional(readOnly = true)
	public List<FacilityMaDto> getListByFacilityId(String facilityId, int offset, int size) {
		return facilityMaMapper.getListByFacilityId(facilityId, offset, size);
	}

	@Transactional
	public int countMaByFacilityId(String facilityId) {
		return facilityMaMapper.countMaByFacilityId(facilityId);
	}

	@Transactional
	public void saveFacilityMa(FacilityMaForm form, MultipartFile upfile, String attachId) {
        try {
            String user = "admin"; // 필요하면 SecurityUtil 등으로 교체

            FacilityMaDto faMaDto = new FacilityMaDto();
            faMaDto.setFacilityId(form.getFacilityId());

            // 1) 날짜 파싱
            String dtStr = form.getMaintainDt();
            Date parsedDate;
            if (dtStr == null || dtStr.isEmpty()) {
                parsedDate = new Date();
            } else {
                parsedDate = dtFormat.parse(dtStr);
            }
            faMaDto.setMaintainDt(parsedDate);

            faMaDto.setFaultTp(form.getFaultTp());
            faMaDto.setMaintainTp(form.getMaintainTp());
            faMaDto.setMaintainMemo(form.getMaintainMemo());
            faMaDto.setMaintainPerson(form.getMaintainPerson());

            // 2) 기존 데이터 조회
            FacilityMaDto existFaMa = facilityMaMapper.findMaByPk(faMaDto.getFacilityId(), faMaDto.getMaintainDt(), faMaDto.getFaultTp(), faMaDto.getMaintainTp());

            // 3) 파일 처리
            if (upfile != null && !upfile.isEmpty()) {
                String entityName = projectName;
                String entityId = faMaEntityId;
                String uploadRequestDtoJson = String.format(
                        "{\"entityName\":\"%s\", \"entityId\":\"%s\", \"userName\":\"%s\"}",
                        entityName, entityId, user
                );

                if (attachId == null || attachId.isEmpty()) {
                    // 신규 업로드
                    String uploadFileCode = uploadFileToAttchSvc(upfile, uploadRequestDtoJson);
                    faMaDto.setMaintainAttach(uploadFileCode);
                } else {
                    // 기존 파일과 비교 후 필요 시 업데이트
                    String curAttachCode = form.getMaintainAttach(); // 기존 코드
                    String url = fileServiceUrl + "/" + curAttachCode;

                    ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        Map<String, Object> existFileInfo =
                                (Map<String, Object>) ((List<Object>) response.getBody().get("data")).get(0);

                        String existingFileName = (String) existFileInfo.get("originalFileName");
                        long existingFileSize = ((Number) existFileInfo.get("size")).longValue();
                        String existingFileType = (String) existFileInfo.get("fileType");

                        String newFileName = upfile.getOriginalFilename();
                        long newFileSize = upfile.getSize();
                        String newFileType = upfile.getContentType();

                        boolean changed =
                                !existingFileName.equals(newFileName) ||
                                existingFileSize != newFileSize ||
                                (existingFileType != null && !existingFileType.equals(newFileType));

                        if (changed) {
                            updateFileToAttchSvc(
                                    upfile,
                                    uploadRequestDtoJson,
                                    curAttachCode,
                                    attachId,
                                    entityName,
                                    entityId
                            );
                            // 코드(attachCode)는 그대로 사용
                            faMaDto.setMaintainAttach(curAttachCode);
                        } else {
                            // 변경 없음 → 기존 코드 그대로
                            faMaDto.setMaintainAttach(curAttachCode);
                        }
                    }
                }
            }

            // 4) INSERT / UPDATE
            if (existFaMa != null) {
                faMaDto.setLastUserId(user);
                faMaDto.setLastRegistProgram(projectName);
                facilityMaMapper.updateFacilityMa(faMaDto);
            } else {
                faMaDto.setFrstUserId(user);
                faMaDto.setFrstRegistProgram(projectName);
                facilityMaMapper.insertFacilityMa(faMaDto);
            }

        } catch (IOException e) {
            throw new RuntimeException("유지관리 파일 처리 중 오류", e);
        } catch (Exception e) {
            throw new RuntimeException("유지관리 저장 중 오류", e);
        }
    }

    // ---------------------------
    // 파일 서비스 연동 부분
    // ---------------------------

    @Transactional
    private String uploadFileToAttchSvc(MultipartFile upfile, String uploadRequestDtoJson) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // ByteArrayResource 사용 (AdServiceImpl 과 동일)
        ByteArrayResource fileResource = new ByteArrayResource(upfile.getBytes()) {
            @Override
            public String getFilename() {
                return upfile.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", fileResource);
        body.add("info", uploadRequestDtoJson);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = fileServiceUrl + "/upload";
        log.info("📤 uploadFileToAttchSvc POST {}", url);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IOException("Failed to upload file Attachment Util. status=" + response.getStatusCode());
        }

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.get("data") != null) {
            return responseBody.get("data").toString();   // 서버에서 준 attachmentCode
        } else {
            throw new IOException("Invalid response from Attachment Util");
        }
    }

    @Transactional
    private void updateFileToAttchSvc(
            MultipartFile upfile,
            String uploadRequestDtoJson,
            String attachCode,
            String attachId,
            String entityName,
            String entityId
    ) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource fileResource = new ByteArrayResource(upfile.getBytes()) {
            @Override
            public String getFilename() {
                return upfile.getOriginalFilename();
            }
        };

        // delete 리스트 JSON (AdServiceImpl 과 동일 규약)
        String listJson = String.format("[{\"uniqueId\":\"%s\", \"delete\":true}]", attachId);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", fileResource);
        body.add("info", uploadRequestDtoJson);
        body.add("list", listJson);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = fileServiceUrl + "/upload/" + attachCode;
        log.info("📤 updateFileToAttchSvc PUT {}", url);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IOException("Failed to update file Attachment Util. status=" + response.getStatusCode());
        }

    }
}