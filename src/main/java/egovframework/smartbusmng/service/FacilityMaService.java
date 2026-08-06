package egovframework.smartbusmng.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import egovframework.smartbusmng.model.facilityMa.FacilityMaDto;
import egovframework.smartbusmng.model.facilityMa.FacilityMaForm;

public interface FacilityMaService {

	List<FacilityMaDto> getListByFacilityId(String facilityId, int offset, int size);

	int countMaByFacilityId(String facilityId);

	void saveFacilityMa(FacilityMaForm form, MultipartFile upfile, String attachId);

}
