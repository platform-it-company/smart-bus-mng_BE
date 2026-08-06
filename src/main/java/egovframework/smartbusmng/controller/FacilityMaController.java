package egovframework.smartbusmng.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import egovframework.smartbusmng.model.facilityMa.FacilityMaDto;
import egovframework.smartbusmng.model.facilityMa.FacilityMaForm;
import egovframework.smartbusmng.service.FacilityMaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/facilityMa")
@RequiredArgsConstructor
public class FacilityMaController {

	@Autowired
	private FacilityMaService faMaService;
	
	@GetMapping("/history")
	public List<FacilityMaDto> history(@RequestParam("facilityId") String facilityId,
		@RequestParam("offset") int offset, @RequestParam("size") int size) {
		
		return faMaService.getListByFacilityId(facilityId, offset, size);
	}
	
	// 앞서 history를 가져갈 때 데이터length보면 될 듯
	@GetMapping("/count")
	public int count(@RequestParam("facilityId") String facilityId) {
		return faMaService.countMaByFacilityId(facilityId);
	}
	
    @PostMapping("/save")
    public ResponseEntity<Map<String,Object>> save(
            @ModelAttribute FacilityMaForm form,
            @RequestParam(value = "upfile", required = false) MultipartFile upfile,
            @RequestParam(value = "attachId", required = false) String attachId
    ) {
        Map<String,Object> body = new HashMap<>();
        try {
            faMaService.saveFacilityMa(form, upfile, attachId);
            body.put("status", "success");
            body.put("message", "유지관리 정보를 저장했습니다.");
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            e.printStackTrace();
            body.put("status", "error");
            body.put("message", "저장 중 오류가 발생했습니다.");
            body.put("error", e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
}
