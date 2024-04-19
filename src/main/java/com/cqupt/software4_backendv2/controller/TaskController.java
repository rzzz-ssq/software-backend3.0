package com.cqupt.software4_backendv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.common.TaskRequest;
import com.cqupt.software4_backendv2.entity.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cqupt.software4_backendv2.dao.TaskMapper;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.cqupt.software4_backendv2.service.TaskService;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/Task")
public class TaskController {
    @Resource
    private  TaskService taskService;

    @Resource
    private  TaskMapper taskMapper;

    @GetMapping("/all")
    public Result getTaskList() {
        return Result.success(taskService.getTaskList());
    }

    @GetMapping("/result/{id}")
    public Result result(@PathVariable int id) throws JsonProcessingException {
        Task task = taskService.getlistbyId(id);
        TaskRequest request = new TaskRequest();
        System.out.println(task);
        ObjectMapper objectMapper = new ObjectMapper();
        String res = task.getResult();
        String[][] retrievedArray = objectMapper.readValue(res, String[][].class);

        String fea1 = task.getFeature();
        String[] fea = fea1.split(",");

        String tar1 = task.getTargetcolumn();
        String[] tar = tar1.split(",");

        String para1 = task.getParameters();
        String[] para = new String[0];
        if (para1 != null)
        {
            para = para1.split(",");
        }



        String paraV1 = task.getParametersvalues();
        String[] paraV = new String[0];
        if (paraV1!=null){
            paraV = paraV1.split(",");
        }



        request.setCi(task.getCi());
        request.setRatio(String.valueOf(task.getRatio()));
        request.setRes(retrievedArray);
        request.setTime(task.getUsetime());
        request.setDisease(task.getDisease());
        request.setDisease(task.getDisease());
        request.setFeature(fea);
        request.setLeader(task.getLeader());
        request.setModel(task.getModel());
        request.setPara(para);
        request.setParaValue(paraV);
        request.setParticipant(task.getParticipant());
        request.setTargetcolumn(tar);
        request.setTaskName(task.getTaskname());
        request.setDataset(task.getDataset());
        request.setUid(task.getUserid());
        request.setTasktype(task.getTasktype());
        return Result.success(request);

    }

    @GetMapping("/delete/{id}")
    public Result deleteById(@PathVariable int id){
        taskService.deleteTask(id);
        return Result.success(taskService.getTaskList());
    }


    @PostMapping("/add")
    public List<Task> addTask(@RequestBody TaskRequest taskrequest) throws JsonProcessingException {

        Task task = new Task();
        String[] fea;
        String fea1;
        fea=taskrequest.getFeature();
        fea1 = String.join(",",fea);

        String[] tar;
        String tar1;
        tar=taskrequest.getTargetcolumn();
        tar1 = String.join(",",tar);

        String[] para;
        String para1;
        para = taskrequest.getPara();
        para1 = String.join(",",para);

        String[] paraV;
        String paraV1;
        paraV = taskrequest.getParaValue();
//        for (String para3 : paraV){
//            System.out.println(para3);
//        }
        paraV1 = String.join(",",paraV);
//        System.out.println(paraV1);

        ObjectMapper objectMapper = new ObjectMapper();
        String[][] res;
        res=taskrequest.getRes();
        if (res != null) {
            String arrayString = objectMapper.writeValueAsString(res);
            task.setResult(arrayString);
            // Proceed with the rest of your code
        } else {
            String arrayString = null;
            task.setResult(arrayString);
        }


        Date date=new Date();//此时date为当前的时间

        task.setTaskname(taskrequest.getTaskName());
        task.setLeader(taskrequest.getLeader());
        task.setParticipant(taskrequest.getParticipant());
        task.setDisease(taskrequest.getDisease());
        task.setModel(taskrequest.getModel());
        task.setRemark(taskrequest.getRemark());
        task.setFeature(fea1);
        task.setTargetcolumn(tar1);
        task.setCreatetime(date);
        task.setParameters(para1);
        task.setParametersvalues(paraV1);
        task.setUsetime(taskrequest.getTime());
        task.setCi(taskrequest.getCi());
        task.setUserid(taskrequest.getUid());
        task.setTips(taskrequest.getTips());
        task.setTasktype(taskrequest.getTasktype());
        Double doubleObj;
        if (taskrequest.getRatio() != null) {
            doubleObj = Double.valueOf(taskrequest.getRatio());
        } else {
            doubleObj = 0.0;  // Or any other default value you want
        }

        double num = doubleObj.doubleValue();


        task.setRatio(num);

        task.setDataset(taskrequest.getDataset());
        System.out.println(task);
        taskService.addTask(task);

        return taskService.getTaskList();
    }

//    @RequestMapping(value = "/task_pagehelper",method = RequestMethod.GET)
//    //分页
//    public PageInfo<Task> findByPage(@PathVariable(value = "pageCode") int pageCode, @PathVariable(value = "pageSize") int pageSize,@RequestParam(name="leader",required = false) String leader, @RequestParam(name="dataset",required = false) String dataset,@RequestParam(name="disease",required = false) String disease){
//        System.out.println("leader:"+leader+" dataset:"+dataset+" disease:"+disease);
//        PageInfo<Task> pageInfo = taskService.findByPageService(pageCode, pageSize,leader,dataset,disease);
//        return pageInfo;
//    }

    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String disease,
                               @RequestParam String leader,
                               @RequestParam String dataset,
                               @RequestParam String tasktype,
                               @RequestParam String searchTask){
        QueryWrapper<Task> queryWrapper = new QueryWrapper<Task>().orderByDesc("createtime");
        queryWrapper.like(StringUtils.isNotBlank(disease),"disease",disease);
        queryWrapper.like(StringUtils.isNotBlank(leader),"leader",leader);
        queryWrapper.like(StringUtils.isNotBlank(dataset),"dataset",dataset);
        queryWrapper.like(StringUtils.isNotBlank(tasktype),"tasktype",tasktype);
        queryWrapper.like(StringUtils.isNotBlank(searchTask), "taskname", "%" + searchTask + "%");
        PageInfo<Task> pageInfo = taskService.findByPageService(pageNum, pageSize,queryWrapper);
        return Result.success(pageInfo);
    }



}
