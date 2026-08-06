package egovframework.smartbusmng.model.ad;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileAttachmentDto {
    private String uniqueId;
    private String attachmentCode;
    private int seq;
    private String originalFileName;
    private long size;
    private String fileType;
    private boolean defaultAt;
    private LocalDateTime createdDate;
}
