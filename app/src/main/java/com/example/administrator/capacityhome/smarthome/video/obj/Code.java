package com.example.administrator.capacityhome.smarthome.video.obj;

/**
 * Created by Administrator on 2017/3/16.
 */

public class Code {
    /**
     * result : 1
     * reason : 正确

     * data : {"code":"7737"}
     */

    private int result;
    private String reason;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * code : 7737
         */

        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
