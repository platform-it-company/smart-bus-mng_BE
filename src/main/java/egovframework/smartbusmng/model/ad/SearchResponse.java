package egovframework.smartbusmng.model.ad;

import java.util.List;

public record SearchResponse(
	List<Ad> ads,
	int totalAds,
	int curPg,
	int size,
	int totalPg,
	int startPg,
	int endPg,
	String ad_tp,
	String ad_period,
	String ad_used
) {}