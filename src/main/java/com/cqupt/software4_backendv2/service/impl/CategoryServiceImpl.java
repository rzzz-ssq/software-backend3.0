package com.cqupt.software4_backendv2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.dao.CategoryMapper;
import com.cqupt.software4_backendv2.entity.CategoryEntity;
import com.cqupt.software4_backendv2.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cqupt.software4_backendv2.entity.CategoryEntity.copyShareTreeStructure;
import static com.cqupt.software4_backendv2.entity.CategoryEntity.copyPrivareTreeStructure;
import static com.cqupt.software4_backendv2.entity.CategoryEntity.copyCommonTreeStructure;


// TODO 公共模块新增类

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,CategoryEntity>
        implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;
    @Override
    public List<CategoryEntity> getCategory(String uid) {
        List<CategoryEntity> categoryEntities_private = new ArrayList<CategoryEntity>();
        List<CategoryEntity> categoryEntities_share = new ArrayList<CategoryEntity>();
        List<CategoryEntity> categoryEntities_common = new ArrayList<CategoryEntity>();
        // 获取所有目录行程树形结构
        List<CategoryEntity> categoryEntities = categoryMapper.selectList(null);
        // 获取所有级结构
        List<CategoryEntity> treeData = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId().equals("0") && categoryEntity.getIsDelete()==0;
        }).map((level1Cat) -> {
            level1Cat.setChildren(getCatChildren(level1Cat, categoryEntities));
            return level1Cat;
        }).collect(Collectors.toList());


        CategoryEntity copiedTree1 = copyPrivareTreeStructure(treeData.get(0),uid);
        copiedTree1.setLabel("私有数据集");
        CategoryEntity copiedTree2 = copyShareTreeStructure(treeData.get(0));
        copiedTree2.setLabel("共享数据集");
        CategoryEntity copiedTree3 = copyCommonTreeStructure(treeData.get(0));
        copiedTree3.setLabel("公共数据集");
        List<CategoryEntity> res = new ArrayList<CategoryEntity>();
        res.add(copiedTree1);
        res.add(copiedTree2);
        res.add(copiedTree3);
        return res;
    }






    // 获取第二层目录
    private List<CategoryEntity> getSecondLevelChildren(String parentId) {
        // 获取所有第二层目录
        List<CategoryEntity> secondLevelCategories = categoryMapper.selectList(null).stream()
                .filter(categoryEntity -> categoryEntity.getParentId().equals(parentId) && categoryEntity.getIsDelete() == 0)
                .map(level2Cat -> {
                    level2Cat.setChildren(new ArrayList<>()); // 第二层目录没有子目录
                    return level2Cat;
                })
                .collect(Collectors.toList());

        return secondLevelCategories;
    }




    @Override
    public void removeNode(String id, String label) {
        categoryMapper.removeNode(id);
        categoryMapper.removeTable(label);
    }

    @Override
    public void removeNode(String id) {
        categoryMapper.removeNode(id);
    }

    // 获取1级目录下的所有子结构
    private List<CategoryEntity> getCatChildren(CategoryEntity level1Cat, List<CategoryEntity> categoryEntities) {
        List<CategoryEntity> children = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId().equals(level1Cat.getId()) && categoryEntity.getIsDelete()==0; // 获取当前分类的所有子分类
        }).map((child) -> {
            // 递归设置子分类的所有子分类
            child.setChildren(getCatChildren(child, categoryEntities));
            return child;
        }).collect(Collectors.toList());
        return children;
    }


    @Override
    public void addParentDisease(String diseaseName) {
        CategoryEntity categoryEntity = new CategoryEntity(null, 1, diseaseName, "0", 0, 0, null, null,null,null,null,null);
        categoryMapper.insert(categoryEntity);
    }

    @Override
    public void changeStatus(CategoryEntity categoryEntity) {
        System.out.println(categoryEntity.getStatus());
        if (categoryEntity.getStatus().equals("0")){
            categoryMapper.changeStatusToShare(categoryEntity.getId());
        }
        else if(categoryEntity.getStatus().equals("1")){
            categoryMapper.changeStatusToPrivate(categoryEntity.getId());
        }

    }

    @Override
    public List<CategoryEntity> getTaskCategory() {

        // 获取所有目录行程树形结构
        List<CategoryEntity> categoryEntities = categoryMapper.selectList(null);
        // 获取所有级结构
        List<CategoryEntity> treeData = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId().equals("0") && categoryEntity.getIsDelete()==0;
        }).map((level1Cat) -> {
            level1Cat.setChildren(getCatChildren(level1Cat, categoryEntities));
            return level1Cat;
        }).collect(Collectors.toList());

        return treeData;
    }

    @Override
    public List<CategoryEntity> getSpDisease() {
        List<CategoryEntity> res = categoryMapper.getSpDisease();
        return res;
    }

    @Override
    public List<CategoryEntity> getComDisease() {
        List<CategoryEntity> res = categoryMapper.getComDisease();
        return res;
    }

    @Override
    public String getLabelByPid(String pid) {
        return categoryMapper.getLabelByPid(pid);
    }


    @Override
    public List<CategoryEntity> getLevel2Label() {
        return categoryMapper.getLevel2Label();
    }

    @Override
    public List<CategoryEntity> getLabelsByPid(String pid) {
        return categoryMapper.getLabelsByPid(pid);
    }
    public void updateTableNameByTableId(String tableid, String tableName, String tableStatus) {
        System.out.println("status: " + tableStatus);
        categoryMapper.updateTableNameByTableId(tableid, tableName, tableStatus);

    }
}
