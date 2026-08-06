package egovframework.smartbusmng.model.notice;

import lombok.Data;

@Data
public class Notice {
    private Integer notiId;
    private String groupId;
    private String notiTitle;
    private String notiContent;
    private String notiWriter;
    private String notiCreatedAt;
    private String notiUpdatedAt;
}
