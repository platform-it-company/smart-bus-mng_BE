package egovframework.smartbusmng.model.notice;

import lombok.Data;

@Data
public class NoticeList {
    private Integer notiId;
    private String groupId;
    private String notiTitle;
    private String notiWriter;
    private String notiCreatedAt;
    private String notiUpdatedAt;
}
