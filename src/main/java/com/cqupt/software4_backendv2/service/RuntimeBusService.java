package com.cqupt.software4_backendv2.service;

import com.cqupt.software4_backendv2.common.RuntimeBusCreateRequest;
import com.cqupt.software4_backendv2.common.RuntimeBusResponse;
import com.cqupt.software4_backendv2.common.RuntimeBusServiceResponse;

public interface RuntimeBusService {
    RuntimeBusServiceResponse SF_DRMB(RuntimeBusCreateRequest request) throws Exception;

    RuntimeBusServiceResponse IAMB(RuntimeBusCreateRequest request) throws Exception;

    RuntimeBusServiceResponse traditional_statistic(RuntimeBusCreateRequest request) throws Exception;

    RuntimeBusServiceResponse pc_algorithm(RuntimeBusCreateRequest request) throws Exception;
}
