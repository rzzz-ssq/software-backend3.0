package com.cqupt.software4_backendv2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software4_backendv2.entity.CategoryEntity;
import com.cqupt.software4_backendv2.vo.AddDiseaseVo;
import com.cqupt.software4_backendv2.vo.UpdateDiseaseVo;
import org.apache.ibatis.annotations.Param;
import com.cqupt.software4_backendv2.common.Result;

import java.util.List;
// TODO 公共模块新增类
public interface CategoryService extends IService<CategoryEntity> {
    List<CategoryEntity> getCategory(String uid);
    void removeNode(String id, String label);
    void removeNode(String id);

    void addParentDisease(String diseaseName);

    void changeStatus(CategoryEntity categoryEntity);

    List<CategoryEntity> getTaskCategory();
    List<CategoryEntity> getSpDisease();
    List<CategoryEntity> getComDisease();
    String getLabelByPid(@Param("pid") String pid);

    //    查看各等级病种
    List<CategoryEntity> getLevel2Label();
    List<CategoryEntity> getLabelsByPid(@Param("pid") String pid);

    //    新增疾病管理模块
    List<CategoryEntity> getAllDisease();
    int addCategory(AddDiseaseVo addDiseaseVo);
    Result updateCategory(UpdateDiseaseVo updateDiseaseVo);
    void removeCategorys(List<String> deleteIds);
}
