package egovframework.smartbusmng.model.facilityCntr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AckRequest {
	private long msgId;
	private int ok;
	private String ackCode;
	private String ackMsg;
}
