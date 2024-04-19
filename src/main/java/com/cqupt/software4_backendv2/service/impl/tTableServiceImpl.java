package com.cqupt.software4_backendv2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.dao.tTableMapper;
import com.cqupt.software4_backendv2.entity.tTable;
import com.cqupt.software4_backendv2.service.tTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class tTableServiceImpl extends ServiceImpl<tTableMapper, tTable>
        implements tTableService {
    @Autowired
    private tTableMapper tTableMapper;
    @Override
    public void insertTable(tTable tTable) {
        tTableMapper.insertTable(tTable);
    }
}
