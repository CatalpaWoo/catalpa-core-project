package com.catalpa.common.core.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;
import java.util.*;

public class ResponseMessage implements Serializable {
    public static final int OK = 1;
    public static final int ERROR = 0;
    public static final int GLOBAL_EXCEPTION = -1;
    private static final int TOTAL_DEFAULT = -1;
    private static final int TOTAL = 0;
    private static final long serialVersionUID = 8992436576262574064L;
    private int code;
    @JSONField(
            serialzeFeatures = {SerializerFeature.WriteMapNullValue}
    )
    private String message;
    @JSONField(
            serialzeFeatures = {SerializerFeature.WriteMapNullValue}
    )
    private String callback;
    @JSONField(
            serialzeFeatures = {SerializerFeature.WriteMapNullValue}
    )
    private Object data;
    private long total;

    protected void setCode(int code) {
        this.code = code;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    protected void setCallback(String callback) {
        this.callback = callback;
    }

    protected void setData(Object data) {
        this.data = data;
    }

    protected void setTotal(long total) {
        this.total = total;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap();
        if (this.data != null) {
            map.put("data", this.getData());
        }

        if (this.message != null) {
            map.put("message", this.getMessage());
        }

        map.put("code", this.getCode());
        map.put("total", this.getTotal());
        return map;
    }

    public ResponseMessage callback(String callback) {
        this.callback = callback;
        return this;
    }

    public String getCallback() {
        return this.callback;
    }

    public ResponseMessage() {
        this.total = -1L;
    }

    protected ResponseMessage(String message) {
        this(0, message, -1L, (Object)null);
    }

    protected ResponseMessage(int code, String message) {
        this(code, message, -1L, "");
    }

    protected ResponseMessage(int code, String message, long total, Object data) {
        this.total = -1L;
        this.code = code;
        this.message = message;
        this.data = data;
        this.total = total;
    }

    protected Set<String> getStringListFormMap(Map<Class<?>, Set<String>> map, Class type) {
        Set<String> list = (Set)map.get(type);
        if (list == null) {
            list = new HashSet();
            map.put(type, list);
        }

        return (Set)list;
    }

    public static int getGlobalException() {
        return -1;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getData() {
        return this.data;
    }

    public long getTotal() {
        return this.total;
    }

    public String toString() {
        return JSON.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]);
    }

    public static ResponseMessage fromJson(String json) {
        JSONObject object = JSONObject.parseObject(json);
        Object obCode = object.get("code");
        Object obData = object.get("data");
        Object obMessage = object.get("message");
        Object obTotal = object.get("total");
        return new ResponseMessage(obCode != null ? Integer.parseInt(obCode.toString()) : 0, obMessage != null ? obMessage.toString() : "", obTotal != null ? (long)Integer.parseInt(obTotal.toString()) : 0L, obData);
    }

    public static ResponseMessage ok() {
        return new ResponseMessage(1, "success");
    }

    public static ResponseMessage okArray(Object data) {
        return data == null ? new ResponseMessage(1, "success", 0L, new ArrayList()) : ok(data);
    }

    public static ResponseMessage okArray(Object data, long total) {
        return data == null ? new ResponseMessage(1, "success", 0L, new ArrayList()) : ok(total, data);
    }

    public static ResponseMessage okObject(Object data) {
        return data == null ? new ResponseMessage(1, "success") : ok(data);
    }

    public static ResponseMessage ok(Object data) {
        if (data == null) {
            return error("查询对象为空");
        } else if (data instanceof List) {
            List _obj = (List)data;
            return _obj.size() <= 0 ? new ResponseMessage(1, "success", 0L, new ArrayList()) : new ResponseMessage(1, "success", (long)((List)data).size(), data);
        } else {
            return new ResponseMessage(1, "success", 1L, data);
        }
    }

    public static ResponseMessage ok(long total, Object data) {
        if (data == null) {
            return error("查询对象为空");
        } else {
            if (data instanceof List) {
                List _obj = (List)data;
                if (_obj.size() <= 0) {
                    return new ResponseMessage(1, "success", 0L, new ArrayList());
                }
            }

            return new ResponseMessage(1, "success", total, data);
        }
    }

    public static ResponseMessage error(String message) {
        return new ResponseMessage(message);
    }

    public static ResponseMessage error() {
        return new ResponseMessage(-1, "系统错误！", 0L, (Object)null);
    }

    public static ResponseMessage error(int code, String message) {
        return new ResponseMessage(code, message);
    }

    public static ResponseMessage error(int code, String message, Object object) {
        return new ResponseMessage(code, message, -1L, object);
    }

    public static int getOK() {
        return 1;
    }

    public static int getERROR() {
        return 0;
    }

    public static int getTotalDefault() {
        return -1;
    }

    public static int getTOTAL() {
        return 0;
    }
}