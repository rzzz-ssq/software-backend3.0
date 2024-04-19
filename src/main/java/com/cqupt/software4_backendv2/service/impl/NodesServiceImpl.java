package com.cqupt.software4_backendv2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.dao.NodesMapper;

import com.cqupt.software4_backendv2.entity.Nodes;
import com.cqupt.software4_backendv2.entity.Relationships;
import com.cqupt.software4_backendv2.service.NodesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodesServiceImpl extends ServiceImpl<NodesMapper, Nodes>
        implements NodesService {
    @Autowired
    NodesMapper nodesMapper;


    @Override
    public List<Nodes> getAllNodes() {
        return nodesMapper.getAllNodes();
    }

    @Override
    public List<Relationships> getRelationships() {
        return nodesMapper.getRelationships();
    }
}
