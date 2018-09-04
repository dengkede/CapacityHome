package com.example.administrator.capacityhome.smarthome.video.obj;

import java.util.List;

/**
 * Created by Administrator on 2017/4/27.
 */

public class DeviceUsers {

    /**
     * result : 1
     * reason : 正确

     * data : [{"pid":"4028aa825a93baf6015adb596dd50024","description":null,"userPid":"4028aa825a93baf6015ad5b9fd50001d","name":"测试","devicePid":"WBHZ8V3SP9KY7D9B111A","imageName":null,"online":null,"role":"0","userName":null,"deviceType":null,"phone":null,"account":"18221199447"},{"pid":"4028aa825a93baf6015b7b5a49610060","description":null,"userPid":"4028aa825a93baf6015ad5fe0461001e","name":"wbh","devicePid":"WBHZ8V3SP9KY7D9B111A","imageName":null,"online":null,"role":"2","userName":null,"deviceType":null,"phone":null,"account":"13243759807"},{"pid":"4028aa825a93baf6015ba93021070067","description":null,"userPid":"4028aa825a637d6f015a63c2e7b60000","name":"wbh","devicePid":"WBHZ8V3SP9KY7D9B111A","imageName":null,"online":null,"role":"2","userName":"18666427973","deviceType":null,"phone":"15012345678","account":"18666427973"}]
     */

    private int result;
    private String reason;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * pid : 4028aa825a93baf6015adb596dd50024
         * description : null
         * userPid : 4028aa825a93baf6015ad5b9fd50001d
         * name : 测试
         * devicePid : WBHZ8V3SP9KY7D9B111A
         * imageName : null
         * online : null
         * role : 0
         * userName : null
         * deviceType : null
         * phone : null
         * account : 18221199447
         */

        private String pid;
        private Object description;
        private String userPid;
        private String name;
        private String devicePid;
        private Object imageName;
        private Object online;
        private String role;
        private Object userName;
        private Object deviceType;
        private Object phone;
        private String account;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public Object getDescription() {
            return description;
        }

        public void setDescription(Object description) {
            this.description = description;
        }

        public String getUserPid() {
            return userPid;
        }

        public void setUserPid(String userPid) {
            this.userPid = userPid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDevicePid() {
            return devicePid;
        }

        public void setDevicePid(String devicePid) {
            this.devicePid = devicePid;
        }

        public Object getImageName() {
            return imageName;
        }

        public void setImageName(Object imageName) {
            this.imageName = imageName;
        }

        public Object getOnline() {
            return online;
        }

        public void setOnline(Object online) {
            this.online = online;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Object getUserName() {
            return userName;
        }

        public void setUserName(Object userName) {
            this.userName = userName;
        }

        public Object getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(Object deviceType) {
            this.deviceType = deviceType;
        }

        public Object getPhone() {
            return phone;
        }

        public void setPhone(Object phone) {
            this.phone = phone;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }
    }
}
