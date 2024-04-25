package com.cqupt.software4_backendv2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software4_backendv2.entity.Notification;
import com.cqupt.software4_backendv2.vo.InsertNoticeVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface NoticeService extends IService<Notification> {
    PageInfo<Notification> allNotices(Integer pageNum, Integer pageSize);

    void saveNotification(InsertNoticeVo notification);

    List<Notification> queryNotices();

    void deleteById(Integer infoId);
}
