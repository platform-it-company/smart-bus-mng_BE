package egovframework.smartbusmng.service;

import java.util.Map;

import egovframework.smartbusmng.model.notice.Notice;
import egovframework.smartbusmng.model.notice.NoticeEditReq;
import egovframework.smartbusmng.model.notice.NoticeSaveReq;

public interface NoticeService {
    Map<String, Object> getNoticeList(String groupId, String keyword, int page, int size);

    Notice getNotice(Integer notiId, String groupId);

    void createNotice(NoticeSaveReq req);

    void updateNotice(Integer notiId, String groupId, NoticeEditReq req);

    void deleteNotice(Integer notiId, String groupId);
}
