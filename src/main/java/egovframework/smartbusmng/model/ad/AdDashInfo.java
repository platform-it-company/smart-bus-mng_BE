package egovframework.smartbusmng.model.ad;

public record AdDashInfo(
	int adCount,
	int adOnelineCnt,
	int adImageCnt,
	int adVodCnt,
	int adAnyCnt,
	int adExpiredOneCnt,
	int adExpiredImgCnt,
	int adExpiredVodCnt,
	int adExpiredAnyCnt,
	int adWaitOneCnt,
	int adWaitImgCnt,
	int adWaitVodCnt,
	int adWaitAnyCnt,
	int adCurTotal,
	int adExpiredTotal,
	int adWaitTotal
) {}