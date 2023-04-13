package com.catalpa.common.core.message;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;

public class ResultMessage<T> implements Serializable {
    private int code;
    @JSONField(
            serialzeFeatures = {SerializerFeature.WriteMapNullValue}
    )
    private String message = "";
    private T data;

    public ResultMessage() {
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    protected ResultMessage(int code) {
        this.code = code;
    }

    protected ResultMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    protected ResultMessage(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isOK() {
        return this.getCode() == 1;
    }

    public static ResultMessage buildOK() {
        return new ResultMessage(1);
    }

    public static <T> ResultMessage buildOK(T data) {
        return new ResultMessage(1, "", data);
    }

    public static ResultMessage buildError(String msg) {
        return new ResultMessage(0, msg);
    }

    public static ResultMessage buildError(int code, String msg) {
        return new ResultMessage(code, msg);
    }

    public static <T> ResultMessage buildError(int code, String msg, T data) {
        return new ResultMessage(code, msg, data);
    }
}

