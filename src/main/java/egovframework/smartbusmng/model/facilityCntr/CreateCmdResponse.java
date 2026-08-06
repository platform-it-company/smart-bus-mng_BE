package egovframework.smartbusmng.model.facilityCntr;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateCmdResponse {
    private Long reqId;
    private String requestId;
    private int totalTargets;
}