package egovframework.smartbusmng.model.facility.record;

public record ModelSpecDto(
	    String modelId,
	    String modelNm,
	    String manufacturer,
	    String displayRes,
	    String routeMapRes,
	    String adAreaRes,
	    int unitPrice
) {}
