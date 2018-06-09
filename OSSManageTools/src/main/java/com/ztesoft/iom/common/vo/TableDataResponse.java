package com.ztesoft.iom.common.vo;

/**
 * @Description: 前后台交互表格数据响应类
 * @author: huang.jing
 * @Date: 2018/1/1 0001 - 20:13
 */
public class TableDataResponse {

    private int code;
    private String msg;
    private long count;
    private Object data;

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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
