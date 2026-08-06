package egovframework.smartbusmng.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseVO<T> {
	
	private int status;
	private String code;
	private String message;
	private T data;
	private List<Object> errors;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
	private LocalDateTime timestamp;
	private String timeStampStr;
	
	public static <T> ResponseVO<T> of(ResponseCode code, T data) {
		LocalDateTime now = LocalDateTime.now();
		return ResponseVO.<T>builder()
				.status(code.getStatus())
				.code(code.getCode())
				.message(code.getMessage())
				.data(data)
				.errors(null)
				.timestamp(now)
				.timeStampStr(now.toString())
				.build();
	}
	
	public static <T> ResponseVO<T> error(ResponseCode code, List<Object> errors) {
		LocalDateTime now = LocalDateTime.now();
		return ResponseVO.<T>builder()
				.status(code.getStatus())
				.code(code.getCode())
				.message(code.getMessage())
				.data(null)
				.errors(errors)
				.timestamp(now)
				.timeStampStr(now.toString())
				.build();
	}
}