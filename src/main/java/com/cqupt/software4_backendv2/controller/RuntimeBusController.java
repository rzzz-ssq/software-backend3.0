package com.cqupt.software4_backendv2.controller;

import com.cqupt.software4_backendv2.common.*;
import com.cqupt.software4_backendv2.service.RuntimeBusService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.annotation.Resource;
import java.util.*;
import java.lang.reflect.Type;
@Log4j2
@RestController
@RequestMapping("/runtime_bus")
@CrossOrigin

public class RuntimeBusController {


    @Autowired
    private RuntimeBusService runtimeBusService;
    @PostMapping("/sf_drmb")
    public Result sfDrmb(@RequestBody RuntimeBusCreateRequest request) throws Exception {
            String target = request.getTargetcolumn()[0];
            RuntimeBusServiceResponse result = runtimeBusService.SF_DRMB(request);

            List<String> resList = result.getRes(); // Assuming 'getRes()' returns the list of strings.
            List<List<String>> resDoubleList = new ArrayList<>();
            resDoubleList.add(resList);

            Map<Object,Object> treeRes = new HashMap<>();
            if(resList.size()!=0)
            {
                treeRes.put(target,resList);
            }
            // Get the knowledge array from the request
            String[] knowledge = request.getKnowledge();

            // Step 1: Calculate the count of elements in 'result' that are present in 'knowledge'
            int count = 0;
            int a=0;
            for (String element : resList) {
                a++;
                if (Arrays.asList(knowledge).contains(element)) {
                    count++;
                }
            }

            // Step 2: Calculate the ratio
            double ratio = (double) count /a ;

            // Step 3: Print the ratio (optional)

            RuntimeBusServiceResponseFinal res = new RuntimeBusServiceResponseFinal();
            res.setRes(resDoubleList);
            res.setRatio(ratio);
            res.setTreeRes(treeRes);
            return Result.success(res);

        }



    @PostMapping("/iamb")
    public Result submit_Task2(@RequestBody RuntimeBusCreateRequest request) throws Exception {
            List<String>  targetList = Arrays.asList(request.getTargetcolumn());
            Map<Object,Object> treeRes = new HashMap<>();
            RuntimeBusServiceResponse result = runtimeBusService.IAMB(request);

            List<String> resList = result.getRes(); // Assuming 'getRes()' returns the list of strings.

            ObjectMapper mapper = new ObjectMapper();
            String resStr = mapper.writeValueAsString(resList);
            // 解析 JSON 字符串为 JsonNode
            JsonNode data = mapper.readTree(resStr);


            String jsonString1 = data.get(0).asText();
            JsonNode jsonData1 = mapper.readTree(jsonString1);

    // 现在可以访问"ResMB_names"字段，并将其值放入二维列表
            JsonNode resMbNamesNode = jsonData1.get("ResMB_names");
//        System.out.println(resMbNamesNode);
            List<List<String>> resMbNamesArray = new ArrayList<>();
            for (JsonNode item : resMbNamesNode) {
                List<String> innerList = new ArrayList<>();
                for (JsonNode subItem : item) {
                    innerList.add(subItem.asText());
                }
                resMbNamesArray.add(innerList);
            }




            JsonNode ciNumObject = data.get(1);
            JsonNode ciNumObject1 = mapper.readTree(ciNumObject.asText());
            JsonNode timeNumObject = data.get(2);
            JsonNode timeNumObject1 = mapper.readTree(timeNumObject.asText()); // 解析为新的 JsonNode 对象
            double useTime = timeNumObject1.get("use_time").asDouble();
            // 获取 "ci_num" 字段的值并存为整数
            int ciNum = ciNumObject1.get("ci_num").asInt();


            System.out.println(resMbNamesArray);
            List<String> resList1 = new ArrayList<>();
            String[] knowledge = request.getKnowledge();
            int a=0;
            for (List<String> subArray : resMbNamesArray) {
                // 遍历子数组的元素并添加到一维数组中
                for (String element : subArray) {
                    resList1.add(element);
                    a++;
                }
            }
            System.out.println(resList1);
            // Step 1: Calculate the count of elements in 'result' that are present in 'knowledge'
            int count = 0;
            for (String element : resList1) {
                if (Arrays.asList(knowledge).contains(element)) {
                    count++;
                }
            }
        RuntimeBusServiceResponseFinal res = new RuntimeBusServiceResponseFinal();
        // Step 2: Calculate the ratio
        if (a != 0) {
            double ratio = (double) count /a;
            res.setRatio(ratio);
        }
        for (int i =0;i<targetList.size();i++)
        {
            if(resMbNamesArray.get(i).size() != 0)
            {
                treeRes.put(targetList.get(i), resMbNamesArray.get(i));
            }
        }
            res.setRes(resMbNamesArray);
            res.setCi(ciNum);
            res.setTime(useTime);
            res.setTreeRes(treeRes);
            return Result.success(res);


    }


    @PostMapping("/traditional_statistic")
    public Result traditional_statistic(@RequestBody RuntimeBusCreateRequest request) throws Exception {
        List<String>  targetList = Arrays.asList(request.getTargetcolumn());
        RuntimeBusServiceResponse result = runtimeBusService.traditional_statistic(request);
        return Result.success(result);
    }

    @PostMapping("/pc_algorithm")
    public Result pc_algorithm(@RequestBody RuntimeBusCreateRequest request) throws Exception {
        List<String>  targetList = Arrays.asList(request.getTargetcolumn());
        RuntimeBusServiceResponse result = runtimeBusService.pc_algorithm(request);
        String JsonRes= result.getRes().get(0).toString();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> data = gson.fromJson(JsonRes, listType);

        List<List<String>> resDoubleList = new ArrayList<>();
        List<List<Double>> weightDoubleList = new ArrayList<>();
        for (String tar: targetList){
            for (Map<String, Object> map: data) {
                if(map.get("targetcol").equals(tar)){
                    List<String> tempResList = new ArrayList<>();
                    List<Double> temWeightList = new ArrayList<>();
                    List<Map<String,Double>> res = (List<Map<String, Double>>) map.get("res");
                    if(res.size()>0){
                        for (Map<String, Double> tempres:res){
                            Set<String> keys = tempres.keySet();
                            for (String key : keys) {
                                tempResList.add(key);
                            }
                            Collection<Double> values = tempres.values();  // 获取所有值的集合
                            for (Double value : values) {
                                temWeightList.add(value);
                            }
                        }
                        resDoubleList.add(tempResList);
                        weightDoubleList.add(temWeightList);
                    }
                    else{
                        resDoubleList.add(tempResList);
                        weightDoubleList.add(temWeightList);
                    }

                }
            }
        }
        RuntimeBusServiceResponseFinal res = new RuntimeBusServiceResponseFinal();
        res.setResWeights(weightDoubleList);
        res.setRes(resDoubleList);
        return Result.success(res);
    }


    @PostMapping("/mifs_algorithm")
    public Result mifs_algorithm(@RequestBody RuntimeBusCreateRequest request) throws Exception {
        List<String>  targetList = Arrays.asList(request.getTargetcolumn());
        RuntimeBusServiceResponse result = runtimeBusService.mifs_algorithm(request);
        String JsonRes= result.getRes().get(0).toString();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> data = gson.fromJson(JsonRes, listType);

        List<List<String>> resDoubleList = new ArrayList<>();
        List<List<Double>> weightDoubleList = new ArrayList<>();
        for (String tar: targetList){
            for (Map<String, Object> map: data) {
                if(map.get("targetcol").equals(tar)){
                    List<String> tempResList = new ArrayList<>();
                    List<Double> temWeightList = new ArrayList<>();
                    List<Map<String,Double>> res = (List<Map<String, Double>>) map.get("res");
                    if(res.size()>0){
                        for (Map<String, Double> tempres:res){
                            Set<String> keys = tempres.keySet();
                            for (String key : keys) {
                                tempResList.add(key);
                            }
                            Collection<Double> values = tempres.values();  // 获取所有值的集合
                            for (Double value : values) {
                                temWeightList.add(value);
                            }
                        }
                        resDoubleList.add(tempResList);
                        weightDoubleList.add(temWeightList);
                    }
                    else{
                        resDoubleList.add(tempResList);
                        weightDoubleList.add(temWeightList);
                    }

                }
            }
        }
        RuntimeBusServiceResponseFinal res = new RuntimeBusServiceResponseFinal();
        res.setResWeights(weightDoubleList);
        res.setRes(resDoubleList);
        return Result.success(res);
    }

}
