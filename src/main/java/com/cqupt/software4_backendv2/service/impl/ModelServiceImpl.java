package com.cqupt.software4_backendv2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.dao.ModelMapper;
import com.cqupt.software4_backendv2.entity.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cqupt.software4_backendv2.service.ModelService;

import java.util.List;

@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model>
        implements ModelService {
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public List<String> getModelNames() {
        return modelMapper.getModelNames();
    }
}
