package com.cqupt.software4_backendv2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software4_backendv2.entity.tTable;

public interface tTableService extends IService<tTable> {
    void insertTable(tTable tTable);
}
