package com.cqupt.software4_backendv2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software4_backendv2.entity.Model;
import java.util.List;
public interface ModelService extends IService<Model> {
    List<String> getModelNames();
}
