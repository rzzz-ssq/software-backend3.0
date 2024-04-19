package com.cqupt.software4_backendv2.service;



import com.cqupt.software4_backendv2.common.RuntimeTaskRequest;
import com.cqupt.software4_backendv2.common.RuntimeTaskResponse;

import java.util.List;

public interface RuntimeTaskService {

    RuntimeTaskResponse submitTask(RuntimeTaskRequest request) throws Exception;

    RuntimeTaskResponse submitStastic(RuntimeTaskRequest request) throws Exception;



    
}
