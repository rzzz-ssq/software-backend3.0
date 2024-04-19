package com.cqupt.software4_backendv2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.dao.StasticOneMapper;
import com.cqupt.software4_backendv2.entity.StasticOne;
import com.cqupt.software4_backendv2.service.StasticOneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StasticOneServiceImpl extends ServiceImpl<StasticOneMapper, StasticOne>
        implements StasticOneService {

    @Autowired StasticOneMapper stasticOneMapper;
    public StasticOne getStasticOne(){
        StasticOne stasticOne = new StasticOne();
        //获得任务总量
        Integer numberOfDiease = stasticOneMapper.getDieaseCount();
        stasticOne.setSpecialDiseaseNumber(numberOfDiease);


        //获得样本总量
        List<String> tableNames =  stasticOneMapper.getTableNames();
        Integer totalItemNumber = 0;
        for (String tableName: tableNames) {
            Integer numberOfSample = stasticOneMapper.getSampleCount(tableName);
            totalItemNumber+=numberOfSample;
        }
        stasticOne.setItemNumber(totalItemNumber);

        //获得起始时间
        List<Date> earlyDates = new ArrayList<>();
        List<Date> lateDates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (String tableName: tableNames) {
            Date earlyDate = stasticOneMapper.getEarlyDate(tableName);
            Date lastDate = stasticOneMapper.getLastDate(tableName);
            earlyDates.add(earlyDate);
            lateDates.add(lastDate);
        }
        if (earlyDates != null && !earlyDates.isEmpty()) {
            Date earliestDate = earlyDates.get(0);
            for (Date date : earlyDates) {
                if (date.before(earliestDate)) {
                    earliestDate = date;
                }
            }
            String earlyFormattedDate = sdf.format(earliestDate);
            stasticOne.setStartTime(earlyFormattedDate);
        }
        if (lateDates != null && !lateDates.isEmpty()) {
            Date latestDate  = lateDates.get(0);
            for (Date date : lateDates) {
                if (date.before(latestDate)) {
                    latestDate  = date;
                }
            }
            String lateFormattedDate = sdf.format(latestDate);
            stasticOne.setEndTime(lateFormattedDate);
        }

        //获得任务总数
        stasticOne.setTaskNumber(stasticOneMapper.getTaskCount());
        return stasticOne;
    }

    @Override
    public Map<String,Integer> getDiseases() {
        Map<String,Integer>  diseases = new HashMap<>();
        List<Map<String, Object>> tables =  stasticOneMapper.getTableChineseNames();
        System.out.println(tables);
        for (Map<String, Object> table: tables){
            Integer numberOfSample = stasticOneMapper.getSampleCount((String) table.get("tablename"));
            diseases.put((String) table.get("chinesename"),numberOfSample);
        }
        System.out.println(diseases);
        return diseases;
    }

    @Override
    public List<String> getAllTableNames() {
        List<String> allTableNames = stasticOneMapper.getTableNames();
        return allTableNames;
    }

    @Override
    public List<String> getAllUserBuiltTableNames() {
        return stasticOneMapper.getAllUserBuiltTableNames();
    }



    @Override
    public String getType(String tablename) {
        return stasticOneMapper.getType(tablename);
    }

    @Override
    public Integer getPosNumber(String tablename, String type) {
        return stasticOneMapper.getPosNumber(tablename,type);
    }

    @Override
    public Integer getNegNumber(String tablename, String type) {
        return stasticOneMapper.getNegNumber(tablename,type);
    }

    @Override
    public List<String> getAllFilteredTableNames() {
        return stasticOneMapper.getAllFilteredTableNames();
    }
}
