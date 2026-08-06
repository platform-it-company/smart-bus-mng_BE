package egovframework.smartbusmng.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import egovframework.smartbusmng.model.notice.Notice;
import egovframework.smartbusmng.model.notice.NoticeEditReq;
import egovframework.smartbusmng.model.notice.NoticeList;
import egovframework.smartbusmng.model.notice.NoticeSaveReq;

@Mapper
public interface NoticeMapper {

    List<NoticeList> selectNoticeList(
            @Param("groupId") String groupId,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int selectNoticeCount(
            @Param("groupId") String groupId,
            @Param("keyword") String keyword
    );

    Notice selectNoticeById(
            @Param("notiId") Integer notiId,
            @Param("groupId") String groupId
    );

    int insertNotice(NoticeSaveReq req);

    int updateNotice(
            @Param("notiId") Integer notiId,
            @Param("groupId") String groupId,
            @Param("req") NoticeEditReq req
    );

    int deleteNotice(
            @Param("notiId") Integer notiId,
            @Param("groupId") String groupId
    );
}