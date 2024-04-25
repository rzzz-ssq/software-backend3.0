package com.cqupt.software4_backendv2.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software4_backendv2.entity.Notification;
import com.cqupt.software4_backendv2.vo.InsertNoticeVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper extends BaseMapper<Notification> {


    List<Notification> selectAllNotices();

    void saveNotification(InsertNoticeVo notification);

    void deleteInfoById(Integer infoId);
}
