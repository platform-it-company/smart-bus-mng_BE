package egovframework.smartbusmng.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import egovframework.smartbusmng.mapper.NoticeMapper;
import egovframework.smartbusmng.model.notice.Notice;
import egovframework.smartbusmng.model.notice.NoticeEditReq;
import egovframework.smartbusmng.model.notice.NoticeList;
import egovframework.smartbusmng.model.notice.NoticeSaveReq;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getNoticeList(String groupId, String keyword, int page, int size) {
        int offset = (page - 1) * size;

        List<NoticeList> list = noticeMapper.selectNoticeList(groupId, keyword, offset, size);
        int totalCount = noticeMapper.selectNoticeCount(groupId, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("page", page);
        result.put("size", size);
        result.put("groupId", groupId);
        result.put("keyword", keyword);
        result.put("totalCount", totalCount);
        result.put("totalPages", (int) Math.ceil((double) totalCount / size));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Notice getNotice(Integer notiId, String groupId) {
        return noticeMapper.selectNoticeById(notiId, groupId);
    }

    @Override
    @Transactional
    public void createNotice(NoticeSaveReq req) {
        noticeMapper.insertNotice(req);
    }

    @Override
    @Transactional
    public void updateNotice(Integer notiId, String groupId, NoticeEditReq req) {
        int updated = noticeMapper.updateNotice(notiId, groupId, req);
        if (updated == 0) {
            throw new IllegalArgumentException("존재하지 않는 공지사항이거나 groupId가 일치하지 않습니다. notiId=" + notiId);
        }
    }

    @Override
    @Transactional
    public void deleteNotice(Integer notiId, String groupId) {
        int deleted = noticeMapper.deleteNotice(notiId, groupId);
        if (deleted == 0) {
            throw new IllegalArgumentException("존재하지 않는 공지사항이거나 groupId가 일치하지 않습니다. notiId=" + notiId);
        }
    }
}