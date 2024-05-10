package com.cqupt.software4_backendv2.service.impl;

import com.cqupt.software4_backendv2.common.RuntimeTaskResponse;
import com.cqupt.software4_backendv2.common.*;
import com.cqupt.software4_backendv2.dao.CommonEntityMapper;
import com.cqupt.software4_backendv2.service.RuntimeBusService;
import com.cqupt.software4_backendv2.service.RuntimeTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class RuntimeBusServiceImpl implements RuntimeBusService {
    @Value("${algorithm.py.path1}")
    private String algorithmPyPath1;
    @Value("${algorithm.py.path2}")
    private String algorithmPyPath2;
    @Value("${algorithm.py.path3}")
    private String algorithmPyPath3;

    @Value("src/main/resources/Scripts/traditional_statistic.py")
    private String tradition_statstic_scriptPyPath;

    @Value("src/main/resources/Scripts/PC_mutual_info_regression.py")
    private String pc_algorithm_scriptPyPath;
    @Resource
    private RuntimeTaskService runtimeTaskService;

    @Autowired
    private CommonEntityMapper commonEntityMapper;
    @Override
    public RuntimeBusServiceResponse SF_DRMB(RuntimeBusCreateRequest request) throws Exception {
        RuntimeBusServiceResponse response=new RuntimeBusServiceResponse();
        String[] targets = request.getTargetcolumn();
        String[] CalculatedColumn = request.getFea();
        List<String> indexe_tar = new ArrayList<>();
        List<String>  indexe_fea = new ArrayList<>();
        for (String target : targets){

            Integer index = commonEntityMapper.findTargetColumnIndex(request.getTablename(),target);
            if (index != null){
                indexe_tar.add(String.valueOf(index));
            }
        }
        for (String fea : CalculatedColumn){
            Integer index = commonEntityMapper.findTargetColumnIndex(request.getTablename(),fea);
            if (index != null){
                indexe_fea.add(String.valueOf(index));
            }
        }
        List<String> args=new LinkedList<>();
        String targetcolumn = "--targetcolumn=" + String.join(" ", indexe_tar);
        String feacolumn = "--calculatedColumns=" + String.join(" ", indexe_fea);
        args.add(targetcolumn);
        args.add(feacolumn);
        args.add("--tableName="+request.getTablename());
        args.add("--K_OR="+request.getK_OR());
        args.add("--K_and_pc="+request.getK_and_pc());
        args.add("--K_and_sp="+request.getK_and_sp());


        RuntimeTaskRequest runtimeTaskRequest=new RuntimeTaskRequest();
        runtimeTaskRequest.setPyPath(algorithmPyPath3);
        runtimeTaskRequest.setArgs(args);
        RuntimeTaskResponse taskResponse=runtimeTaskService.submitTask(runtimeTaskRequest);
        response.setRes(taskResponse.getRes());
        return response;
    }

    @Override
    public RuntimeBusServiceResponse IAMB(RuntimeBusCreateRequest request) throws Exception {
        RuntimeBusServiceResponse response=new RuntimeBusServiceResponse();
        String[] targets = request.getTargetcolumn();
        String[] CalculatedColumn = request.getFea();
        List<String> indexe_tar = new ArrayList<>();

        List<String>  indexe_fea = new ArrayList<>();
        for (String target : targets){
            Integer index = commonEntityMapper.findTargetColumnIndex(request.getTablename(),target);
            if (index != null){
                indexe_tar.add(String.valueOf(index));
            }
        }
        for (String fea : CalculatedColumn){
            Integer index = commonEntityMapper.findTargetColumnIndex(request.getTablename(),fea);
            if (index != null){
                indexe_fea.add(String.valueOf(index));
            }
        }
        List<String> args=new LinkedList<>();
        String targetcolumn = "--targetcolumn=" + String.join(" ", indexe_tar);
        String feacolumn = "--calculatedColumns=" + String.join(" ", indexe_fea);
        args.add(targetcolumn);
        args.add(feacolumn);
        args.add("--tableName="+request.getTablename());


        RuntimeTaskRequest runtimeTaskRequest=new RuntimeTaskRequest();
        runtimeTaskRequest.setPyPath(algorithmPyPath1);
        runtimeTaskRequest.setArgs(args);
        RuntimeTaskResponse taskResponse=runtimeTaskService.submitTask(runtimeTaskRequest);
        response.setRes(taskResponse.getRes());
        return response;
    }

    @Override
    public RuntimeBusServiceResponse traditional_statistic(RuntimeBusCreateRequest request) throws Exception {
        RuntimeBusServiceResponse response=new RuntimeBusServiceResponse();
        String[] targets = request.getTargetcolumn();
        String[] CalculatedColumn = request.getFea();
        List<String> args=new LinkedList<>();
        // String []是java的一个对象，需要解析
        String targetcolumn = "--targetcolumn=" +  String.join(" ", targets);
        String feacolumn = "--calculatedColumns=" +  String.join(" ", CalculatedColumn);
        args.add(targetcolumn);
        args.add(feacolumn);
        args.add("--tableName="+request.getTablename());
        System.out.println(args);
        RuntimeTaskRequest runtimeTaskRequest=new RuntimeTaskRequest();
        runtimeTaskRequest.setPyPath(tradition_statstic_scriptPyPath);
        runtimeTaskRequest.setArgs(args);
        RuntimeTaskResponse taskResponse=runtimeTaskService.submitTraditonalStastic(runtimeTaskRequest);
        response.setRes(taskResponse.getRes());
        return response;
    }

    @Override
    public RuntimeBusServiceResponse pc_algorithm(RuntimeBusCreateRequest request) throws Exception {
        RuntimeBusServiceResponse response=new RuntimeBusServiceResponse();
        String[] targets = request.getTargetcolumn();
        String[] CalculatedColumn = request.getFea();
        List<String> args=new LinkedList<>();
        // String []是java的一个对象，需要解析
        String targetcolumn = "--targetcolumn=" +  String.join(" ", targets);
        String feacolumn = "--calculatedColumns=" +  String.join(" ", CalculatedColumn);
        args.add(targetcolumn);
        args.add(feacolumn);
        args.add("--tableName="+request.getTablename());

        RuntimeTaskRequest runtimeTaskRequest=new RuntimeTaskRequest();
        runtimeTaskRequest.setPyPath(pc_algorithm_scriptPyPath);
        runtimeTaskRequest.setArgs(args);
        RuntimeTaskResponse taskResponse=runtimeTaskService.submitPcAlogrithm(runtimeTaskRequest);
        response.setRes(taskResponse.getRes());
        return response;
    }
}
