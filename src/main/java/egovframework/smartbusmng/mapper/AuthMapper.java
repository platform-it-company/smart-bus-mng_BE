package egovframework.smartbusmng.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import egovframework.smartbusmng.model.member.UserDto;

@Mapper
public interface AuthMapper {
	UserDto findUserByEmail(@Param("userEmail") String userEmail);
}
