package egovframework.smartbusmng.model.facilityCntr;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCmdRequest {
    private List<String> selFacilityIds;
    private String cmdTp;
    private String cmdMsg = null;
}
