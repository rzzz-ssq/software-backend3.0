package com.cqupt.software4_backendv2.service.impl;

import com.alibaba.fastjson.JSON;

import com.cqupt.software4_backendv2.dao.CategoryMapper;
import com.cqupt.software4_backendv2.dao.TableDataMapper;
import com.cqupt.software4_backendv2.dao.TableDescribeMapper;
import com.cqupt.software4_backendv2.dao.UserMapper;
import com.cqupt.software4_backendv2.entity.CategoryEntity;
import com.cqupt.software4_backendv2.entity.FieldManagementEntity;
import com.cqupt.software4_backendv2.entity.TableDescribeEntity;
import com.cqupt.software4_backendv2.entity.User;
import com.cqupt.software4_backendv2.service.FieldManagementService;
import com.cqupt.software4_backendv2.service.TableDataService;
import com.cqupt.software4_backendv2.vo.CreateTableFeatureVo;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

// TODO 公共模块

@Service
public class TableDataServiceImpl implements TableDataService {

    @Autowired
    TableDataMapper tableDataMapper;

    @Autowired
    TableDescribeMapper tableDescribeMapper;
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    UserMapper  userMapper;

    @Autowired
    FieldManagementService fieldManagementService;
    @Override
    public List<LinkedHashMap<String, Object>> getTableData(String TableId, String tableName) {
        List<LinkedHashMap<String, Object>> tableData = tableDataMapper.getTableData(tableName);
        return tableData;
    }

    @Transactional(propagation = Propagation.REQUIRED) // 事务控制
    @Override
    public List<String> uploadFile(MultipartFile file, String tableName, String type, String user, String userId, String parentId, String parentType,String status,Double size,String is_upload,String is_filter) throws IOException, ParseException {
        System.out.println(parentId);
        // 封住表描述信息
        CategoryEntity node = new CategoryEntity();
        node.setIsDelete(0);
        node.setParentId(parentId);
        node.setIsLeafs(1);
        node.setStatus(status);
        node.setUid(userId);
        node.setUsername(user);
        CategoryEntity categoryEntity = categoryMapper.selectById(parentId);
        node.setCatLevel(categoryEntity.getCatLevel()+1);
        node.setLabel(tableName);
        node.setIsFilter(is_filter);
        node.setIsUpload(is_upload);
        categoryMapper.insert(node); // 保存目录信息

        // 表描述信息
        TableDescribeEntity tableDescribeEntity = new TableDescribeEntity();
        tableDescribeEntity.setTableName(tableName);
        tableDescribeEntity.setCreateUser(user);
        tableDescribeEntity.setUid(userId);
        tableDescribeEntity.setTableStatus(status);
        tableDescribeEntity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        tableDescribeEntity.setClassPath(parentType+"/"+tableName);
        tableDescribeEntity.setTableId(node.getId());
        tableDescribeEntity.setTableSize(size);
        // 保存表描述信息
        tableDescribeMapper.insert(tableDescribeEntity);
        List<String> featureList = storeTableData(file, tableName);
        userMapper.decUpdateUserColumnById(userId,size);
        return featureList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void createTable(String tableName, List<CreateTableFeatureVo> characterList, String createUser, CategoryEntity nodeData,String uid,String username,String IsFilter,String IsUpload) {
        /**
         *          筛选数据
         *              查询当前目录下的宽表所有数据
         *                  1、查询nodeData的所有子节点，找到是宽表的节点信息（表名）
         *              筛选 其他病种符合条件的所有数据
         *                  2、遍历所有目录信息 找到所有的宽表节点，排除上一步找到的宽表节点，使用剩下的宽表节点筛选数据
         *              合并所有数据
         *          创建表头信息
         *              3、根据宽表的字段管理表创建一个新表，存储筛选后的数据
         *          保存创建表的数据信息信息
         *              4、将1、2步骤的数据插入到3创建的表中
         *          保存目录信息
         *              5、创建目录节点信息，并保存数据库
         *
         */
        List<LinkedHashMap<String, Object>> res = getFilterDataByConditions(characterList, nodeData);
//        CategoryEntity mustContainNode = getBelongType(nodeData, new ArrayList<CategoryEntity>());
//        // 查询考虑疾病的宽表数据
        //List<LinkedHashMap<String,Object>> diseaseData = tableDataMapper.getAllTableData("merge"); // 传递表名参数
        List<LinkedHashMap<String,Object>> diseaseData = res;
//        System.out.println("考虑疾病所有数据");
//        // 合并考虑疾病和非考虑疾病的所有数据
//        for (LinkedHashMap<String, Object> re : res) {
//            diseaseData.add(re);
//        }
        // 创建表头信息 获取宽表字段管理信息
        List<FieldManagementEntity> fields = fieldManagementService.list(null);
        // System.out.println("字段长度为："+fields.size());
        HashMap<String, String> fieldMap = new HashMap<>();
        for (FieldManagementEntity field : fields) {
            fieldMap.put(field.getFeatureName(),field.getUnit());
        }
        // TODO 创建表头信息
        tableDataMapper.createTableByField(tableName,fieldMap);
        // TODO 数据保存 批量插入
        // TODO 保证value值数量与字段个数一致
        for (Map<String, Object> diseaseDatum : diseaseData) {
            for (FieldManagementEntity field : fields) {
                if(diseaseDatum.get(field.getFeatureName())==null)
                {
                    diseaseDatum.put(field.getFeatureName(),"");
                }
            }
//            System.out.println("数据长度为："+diseaseDatum.size());
        }
        System.out.println("========================================");
        System.out.println("数据长度："+diseaseData.size());
        // TODO 分批插入 防止sql参数传入过多导致溢出
        if(diseaseData.size()>200){
            int batch = diseaseData.size()/200;
            for(int i=0; i<batch; i++){
                int start = i*200, end = (i+1)*200;
                System.out.println("插入第"+i+"轮数据");
                tableDataMapper.bachInsertData(diseaseData.subList(start,end),tableName); // diseaseData.subList(start,end) 前闭后开
            }
            tableDataMapper.bachInsertData(diseaseData.subList(batch*200,diseaseData.size()),tableName);
        }else{
            tableDataMapper.bachInsertData(diseaseData,tableName);
        }
        // 目录信息
        CategoryEntity node = new CategoryEntity();
        node.setIsDelete(0);
        node.setParentId(nodeData.getId());
        node.setIsLeafs(1);
        node.setStatus(nodeData.getStatus());
        node.setUid(uid);
        node.setUsername(username);
        node.setCatLevel(nodeData.getCatLevel()+1);
        node.setLabel(tableName);
        node.setIsFilter(IsFilter);
        node.setIsUpload(IsUpload);
        System.out.println(node);
        categoryMapper.insert(node); // 保存目录信息

        // 表描述信息
        TableDescribeEntity tableDescribeEntity = new TableDescribeEntity();
        tableDescribeEntity.setTableName(tableName);
        tableDescribeEntity.setCreateUser(node.getUsername());
        tableDescribeEntity.setUid(uid);
        tableDescribeEntity.setTableStatus(node.getStatus());
        tableDescribeEntity.setCreateUser(username);
        tableDescribeEntity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        tableDescribeEntity.setClassPath(nodeData.getLabel()+"/"+tableName);
        tableDescribeEntity.setTableId(node.getId());
        tableDescribeEntity.setTableSize(0.0);
        // 保存表描述信息
        tableDescribeMapper.insert(tableDescribeEntity);


    }
    //待修改
//    private CategoryEntity getBelongType(CategoryEntity nodeData, ArrayList<CategoryEntity> leafNodes){
//        getLeafNode(nodeData, leafNodes);
//        if(leafNodes!=null && leafNodes.size()>0){
//            for (CategoryEntity leafNode : leafNodes) {
//                if(leafNode.getIsWideTable()!=null && leafNode.getIsWideTable()==1) {
//                    return leafNode;
//                }
//            }
//        }
//        return null;
//    }

    // 根据条件筛选数据
    @Override
    public List<LinkedHashMap<String, Object>> getFilterDataByConditions(List<CreateTableFeatureVo> characterList,CategoryEntity nodeData) {
        List<CategoryEntity> categoryEntities = categoryMapper.selectList(null); // 查询所有的目录信息
        // 找到所有的宽表节点
        List<CategoryEntity> allWideTableNodes = categoryEntities.stream().collect(Collectors.toList());
        System.out.println("所有的宽表节点："+JSON.toJSONString(allWideTableNodes));
        // 遍历当前节点的所有叶子节点，找到这个宽表节点
        ArrayList<CategoryEntity> leafNodes = new ArrayList<>();
        getLeafNode(nodeData, leafNodes);
        /** 找到所有的非考虑疾病的宽表节点 **/
        List<CategoryEntity> otherWideTable = null;
        if(leafNodes!=null && leafNodes.size()>0){
            System.out.println("非空！！！！！！！！！！！！");
        }else{
            otherWideTable = allWideTableNodes;
        }
        if(otherWideTable==null) otherWideTable = allWideTableNodes;
        // 筛选所有非考虑疾病的宽表数据
        /** select * from ${tableName} where ${feature} ${computeOpt} ${value} ${connector} ... **/
        // 前端传过来的 AND OR NOT 是数字形式0,1,2，需要变成字符串拼接sql
        for (CreateTableFeatureVo createTableFeatureVo : characterList) {
            if(createTableFeatureVo.getOpt()==null) createTableFeatureVo.setOptString("");
            else if(createTableFeatureVo.getOpt()==0) createTableFeatureVo.setOptString("AND");
            else if(createTableFeatureVo.getOpt()==1) createTableFeatureVo.setOptString("OR");
            else createTableFeatureVo.setOptString("AND NOT");
        }
        // 处理varchar类型的数据
        for (CreateTableFeatureVo createTableFeatureVo : characterList) {
            System.out.println("当前字段的类型："+createTableFeatureVo.getUnit());
            if(createTableFeatureVo.getType()==null || createTableFeatureVo.getType().equals("character varying")){
                createTableFeatureVo.setValue("'"+createTableFeatureVo.getValue()+"'");
            }
        }
        List<List<LinkedHashMap<String, Object>>> otherWideTableData = new ArrayList<>();
        ArrayList<LinkedHashMap<String, Object>> res = new ArrayList<>();
        otherWideTableData.add(tableDataMapper.getFilterData("merge",characterList));
        ArrayList<LinkedHashMap<String, Object>> res2 = new ArrayList<>();
        for (List<LinkedHashMap<String, Object>> otherWideTableDatum : otherWideTableData) {
            for (LinkedHashMap<String, Object> rowData : otherWideTableDatum) {
                res.add(rowData);
            }
        }
//        // 依次查询每一个表中符合条件的数据
//        List<List<LinkedHashMap<String, Object>>> otherWideTableData = new ArrayList<>();
//        for (CategoryEntity wideTable : otherWideTable) {
//            otherWideTableData.add(tableDataMapper.getFilterData(wideTable.getLabel(),characterList)); // TODO 筛选SQl xml
//        }
//        /** 数据合并List<List<Map<String, Object>>> otherWideTableData(多张表) 合并到 List<Map<String,Object>> diseaseData（一张表） **/
//        ArrayList<LinkedHashMap<String, Object>> res = new ArrayList<>();
//        for (List<LinkedHashMap<String, Object>> otherWideTableDatum : otherWideTableData) {
//            for (LinkedHashMap<String, Object> rowData : otherWideTableDatum) {
//                res.add(rowData);
//            }
//        }

        return res;
    }

    @Override
    public List<Map<String, Object>> getInfoByTableName(String tableName) {
        return tableDataMapper.getInfoByTableName(tableName);
    }

    private void getLeafNode(CategoryEntity nodeData,List<CategoryEntity> leafNodes){
        for (CategoryEntity child : nodeData.getChildren()) {
            if(child.getIsLeafs()==1) leafNodes.add(child);
            else getLeafNode(child,leafNodes);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<String> storeTableData(MultipartFile file, String tableName) throws IOException {
        ArrayList<String> featureList = null;
        if (!file.isEmpty()) {
            // 使用 OpenCSV 解析 CSV 文件
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),"UTF-8"));
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> csvData = csvReader.readAll();
            csvReader.close();
            // 获取表头信息
            String[] headers = csvData.get(0);
            featureList = new ArrayList<String>(Arrays.asList(headers));
            System.out.println("表头信息为："+ JSON.toJSONString(headers));
            // 删除表头行，剩余的即为数据行
            csvData.remove(0);
            // 创建表信息
            tableDataMapper.createTable(headers,tableName);

            // 保存表头信息和表数据到数据库中
            for (String[] row : csvData) { // 以此保存每行信息到数据库中
                tableDataMapper.insertRow(row,tableName);
            }
        }
        return featureList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<String> ParseFileCol(MultipartFile file, String tableName) throws IOException {
        ArrayList<String> featureList = null;
        if (!file.isEmpty()) {
            // 使用 OpenCSV 解析 CSV 文件
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),"UTF-8"));
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> csvData = csvReader.readAll();
            csvReader.close();
            // 获取表头信息
            String[] headers = csvData.get(0);
            featureList = new ArrayList<String>(Arrays.asList(headers));
        }
        return featureList;
    }

    @Override
    public Integer getCountByTableName(String tableName) {
        return tableDataMapper.getCountByTableName(tableName);
    }


}
