package egovframework.smartbusmng.model;

public enum ResponseCode {

	SUCCESS(200, "PRC-200", "요청이 정상적으로 처리되었습니다."),
	NO_DATA(200, "PRC-204", "데이터가 존재하지 않습니다."),
	BAD_REQUEST(400, "ERR-400", "잘못된 요청입니다."),
	UNAUTHORIZED(401,"ERR-401", "인증이 필요합니다."),
	FORBIDDEN(403,"ERR-403", "접근 권한이 없습니다."),
	SERVER_ERROR(500, "ERR-500", "서버 오류가 발생했습니다.");
	
	private final int status;
	private final String code;
	private final String message;
	
	ResponseCode(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
}

