package com.cqupt.software4_backendv2.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software4_backendv2.entity.AlgorithmUsageDailyStats;
import com.cqupt.software4_backendv2.entity.Task;
import com.github.pagehelper.PageInfo;


import java.util.List;

public interface TaskService extends IService<Task> {
    List<AlgorithmUsageDailyStats> getAlgorithmUsageDailyStatsLast7Days();

    List<String> getAlgorithmName();

    List<Task> getTaskList();
    Task getlistbyId(Integer id);

    void deleteTask(int id);

    void addTask(Task task);

    PageInfo<Task> findByPageService(int pageNum, int pageSize, QueryWrapper<Task> queryWrapper);
}
