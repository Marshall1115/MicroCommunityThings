package com.java110.entity.response;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * @ClassName ResultDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 22:41
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public class ResultDto implements Serializable {


    //成功
    public static final int SUCCESS = 0;

    //失败
    public static final int ERROR = -1;

    public static final int NO_AUTHORITY_ERROR = 401; // 认证失败
    public static final int NO_PAY = -2; // 未支付


    public static final int NO_PUSH_STREAM = -3;

    public static final String SUCCESS_MSG = "成功";

    public static final String ERROR_MSG = "处理失败";

    //未知异常
    public static final String NO_RESULT = "未知异常";

    private int code;

    private String msg;

    private long total;

    private long totalPage;

    private Object data;

    public ResultDto(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultDto(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultDto(int code, String msg, long total, long totalPage, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.total = total;
        this.totalPage = totalPage;
    }

    /**
     * 创建ResponseEntity对象
     *
     * @param data 数据对象
     * @return
     */
    public static ResponseEntity<String> createResponseEntity(Object data) {
        ResultDto resultVo = new ResultDto(SUCCESS, SUCCESS_MSG, data);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);
        return responseEntity;
    }

    /**
     * 创建ResponseEntity对象
     *
     * @param resultVo 数据对象
     * @return
     */
    public static ResponseEntity<String> createResponseEntity(ResultDto resultVo) {
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);
        return responseEntity;
    }

    /**
     * 成功通用回复
     * @return
     */
    public static ResponseEntity<String> success() {
        ResultDto resultVo = new ResultDto(SUCCESS, SUCCESS_MSG);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);
        return responseEntity;
    }

    /**
     * 成功通用回复
     * @return
     */
    public static ResponseEntity<String> success(Object data) {
        ResultDto resultVo = new ResultDto(SUCCESS, SUCCESS_MSG,data);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);
        return responseEntity;
    }

    /**
     * 失败通用回复
     * @return
     */
    public static ResponseEntity<String> error(String msg) {
        ResultDto resultVo = new ResultDto(ERROR, msg);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);
        return responseEntity;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }
}
