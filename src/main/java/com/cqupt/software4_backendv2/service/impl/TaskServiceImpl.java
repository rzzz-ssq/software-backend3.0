package com.cqupt.software4_backendv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.dao.TaskMapper;
import com.cqupt.software4_backendv2.entity.AlgorithmUsageDailyStats;
import com.cqupt.software4_backendv2.entity.Task;
import com.cqupt.software4_backendv2.service.TaskService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.List;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
        implements TaskService {
    @Resource
    TaskMapper taskMapper;


    @Override
    public List<AlgorithmUsageDailyStats> getAlgorithmUsageDailyStatsLast7Days() {
        return taskMapper.getAlgorithmUsageDailyStatsLast7Days();
    }

    @Override
    public List<String> getAlgorithmName() {
        return taskMapper.getAlgorithmName();
    }



    @Override
    public List<Task> getTaskList() {
        return taskMapper.getTaskList();
    }

    @Override
    public Task getlistbyId(Integer id) {
        return taskMapper.getlistbyId(id);
    }

    @Override
    public void deleteTask(int id) {
        taskMapper.deleteTask(id);
    }

    @Override
    public void addTask(Task task) {
        taskMapper.addTask(task);
    }

    @Override
    public PageInfo<Task> findByPageService(int pageNum, int pageSize, QueryWrapper<Task> queryWrapper) {
        PageHelper.startPage(pageNum,pageSize);
        List<Task> taskInfos = taskMapper.selectList(queryWrapper);
        return new PageInfo<>(taskInfos);
    }

}
