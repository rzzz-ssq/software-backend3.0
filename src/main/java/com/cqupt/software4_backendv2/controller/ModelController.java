package com.cqupt.software4_backendv2.controller;


import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Model")
public class ModelController {
    @Autowired
    private ModelService modelService;

    @GetMapping("/all")
    public Result getModelNames(){
        return Result.success(
                200,"获取统计信息成功",modelService.getModelNames());
    }



}
