package com.example.administrator.capacityhome.smarthome.video.obj;

/**
 * Created by Administrator on 2017/3/16.
 */

public class RegistResult {
    /**
     * result : 1
     * reason : 正确

     * data : {"userInfo":null,"expired":1489652478691,"pid":"4028aa825a93baf6015ad5fe0461001e","token":"1489648878691"}
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
         * userInfo : null
         * expired : 1489652478691
         * pid : 4028aa825a93baf6015ad5fe0461001e
         * token : 1489648878691
         */

        private Object userInfo;
        private long expired;
        private String pid;
        private String token;

        public Object getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(Object userInfo) {
            this.userInfo = userInfo;
        }

        public long getExpired() {
            return expired;
        }

        public void setExpired(long expired) {
            this.expired = expired;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
