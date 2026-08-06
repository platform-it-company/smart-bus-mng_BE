package egovframework.smartbusmng.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import egovframework.smartbusmng.model.facilityMa.FacilityMaDto;

@Mapper
public interface FacilityMaMapper {

	List<FacilityMaDto> getListByFacilityId(@Param("facilityId") String facilityId,
            @Param("offset") int offset, @Param("size") int size);

	int countMaByFacilityId(@Param("facilityId") String facilityId);

    FacilityMaDto findMaByPk(@Param("facilityId") String facilityId, @Param("maintainDt") Date maintainDt,
            @Param("faultTp") String faultTp, @Param("maintainTp") String maintainTp);

    void insertFacilityMa(FacilityMaDto dto);

	void updateFacilityMa(FacilityMaDto dto);
}