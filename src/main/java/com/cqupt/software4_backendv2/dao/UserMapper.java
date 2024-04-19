package com.cqupt.software4_backendv2.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software4_backendv2.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("UPDATE software4.software4user SET upload_size = upload_size-#{size} WHERE uid = #{id}")
    int decUpdateUserColumnById(@Param("id") String id, @Param("size") Double size);

    @Update("UPDATE software4.software4user SET upload_size = upload_size+#{size} WHERE uid = #{id}")
    int recoveryUpdateUserColumnById(@Param("id") String id, @Param("size") Double size);

    User queryByUername(String username);
    User selectByUid(String uid);

}
