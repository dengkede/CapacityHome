package com.example.administrator.capacityhome.smarthome.video.obj;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */

public class LoginResult {

    /**
     * result : 1
     * reason : 正确

     * data : {"userInfo":[],"expired":1489648160373,"pid":"4028aa825a93baf6015ad5b9fd50001d","token":"1489644560373"}
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
         * userInfo : []
         * expired : 1489648160373
         * pid : 4028aa825a93baf6015ad5b9fd50001d
         * token : 1489644560373
         */

        private long expired;
        private String pid;
        private String token;
        private List<?> userInfo;

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

        public List<?> getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(List<?> userInfo) {
            this.userInfo = userInfo;
        }
    }
}
