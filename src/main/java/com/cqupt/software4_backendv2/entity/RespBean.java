package com.cqupt.software4_backendv2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespBean {
    private long code;      //状态码
    private String msg;    //返回的信息
    private Object obj;    //返回的对象

    /**
     * 成功返回结果
     * @return
     */
    public static RespBean success(String msg) {
        return new RespBean(200, msg, null);
    }

    /**
     * 成功返回结果
     * @param msg
     * @param obj
     * @return
     */
    public static RespBean success(String msg,Object obj) {
        return new RespBean(200, msg, obj);
    }

    /**
     * 失败返回结果
     * @param msg
     * @return
     */
    public static RespBean error(String msg) {
        return new RespBean(500, msg, null);
    }


    /**
     * 失败返回结果
     * @param msg
     * @param obj
     * @return
     */
    public static RespBean error(String msg,Object obj) {
        return new RespBean(500, msg, obj);
    }

}
