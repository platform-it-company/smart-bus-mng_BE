package egovframework.smartbusmng.model.facilityCntr;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CmdReqDetailResponse {
    private CmdReqRow request;
    private List<CmdTargetRow> targets;
}
