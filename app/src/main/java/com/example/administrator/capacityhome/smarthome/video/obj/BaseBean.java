package com.example.administrator.capacityhome.smarthome.video.obj;

/**
 * Created by Administrator on 2017/5/5.
 */

public class BaseBean {

    /**
     * result : 1
     * reason : 正确

     * data : null
     */

    private int result;
    private String reason;
    private Object data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
