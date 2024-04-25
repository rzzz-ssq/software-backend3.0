package com.cqupt.software4_backendv2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.entity.Notification;
import com.cqupt.software4_backendv2.dao.NoticeMapper;
import com.cqupt.software4_backendv2.service.NoticeService;
import com.cqupt.software4_backendv2.vo.InsertNoticeVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notification>
        implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;


    @Override
    public PageInfo<Notification> allNotices(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        // 返回查询结果列表
        List<Notification> notifications =  noticeMapper.selectAllNotices();
        // 使用 PageInfo 包装查询结果，并返回
        return new PageInfo<>(notifications);
    }

    @Override
    public void saveNotification(InsertNoticeVo notification) {
        noticeMapper.saveNotification(notification);
    }

    @Override
    public List<Notification> queryNotices() {

        List<Notification> notifications = noticeMapper.selectList(null);
        return notifications;
    }

    @Override
    public void deleteById(Integer infoId) {
        noticeMapper.deleteInfoById(infoId);
    }
}
