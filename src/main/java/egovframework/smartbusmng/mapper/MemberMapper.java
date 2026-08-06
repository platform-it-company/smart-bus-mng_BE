package egovframework.smartbusmng.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import egovframework.smartbusmng.model.member.ConfirmUserRequest;
import egovframework.smartbusmng.model.member.GroupUserInfo;
import egovframework.smartbusmng.model.member.MemberVO;
import egovframework.smartbusmng.model.member.MenuDto;
import egovframework.smartbusmng.model.member.OperatorDto;

@Mapper
public interface MemberMapper {

	String getNextOperatorGroupId();
	
	List<MenuDto> getMenusByRole(@Param("role") String role);

	List<GroupUserInfo> getGroupList();

	int countByEmail(String email);

	int checkGroupKey(@Param("groupKey") String groupKey, @Param("groupId") String groupId);

	String getGroupRole(String groupId);

	int insertMember(MemberVO member);

	int updateConfirmedUser(@Param("users") List<ConfirmUserRequest> users);

	List<OperatorDto> selectOperators();

	int insertOperator(OperatorDto operatorDto);

	int updateOperator(OperatorDto operatorDto);

	List<MemberVO> getApprovedMembersPaged(@Param("offset") int offset, @Param("size") int size);

	List<MemberVO> getApprovedMembersAll();

	List<MemberVO> getPendingMembersPaged(@Param("offset") int offset, @Param("size") int size);

	List<MemberVO> getPendingMembersAll();

	int updateMember(MemberVO memberVO);

	MemberVO getMemberInfo(@Param("userId") Long userId, @Param("groupId") String groupId, 
			@Param("userName") String userName);

    String getPasswordByKey(@Param("userId") Long userId, @Param("groupId") String groupId,
            @Param("userName") String userName);

	int updatePassword(@Param("userId") Long userId, @Param("groupId") String groupId,
	    @Param("userName") String userName, @Param("encodedPassword") String encodedPassword);

	MemberVO selectOperatorUserForAuth(@Param("groupId") String groupId, @Param("userEmail") String userEmail);

	int withdrawOperatorUser(@Param("groupId") String groupId, @Param("userEmail") String userEmail);

}
