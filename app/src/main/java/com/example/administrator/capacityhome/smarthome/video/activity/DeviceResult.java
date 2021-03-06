package com.example.administrator.capacityhome.smarthome.video.activity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/17.
 */

public class DeviceResult {
    /**
     * result : 1
     * reason : 正确
     * <p>
     * data : [{"pid":"4028aa825a93baf6015ada47b6470021","description":null,"userPid":"4028aa825a93baf6015ad5b9fd50001d","name":"shdh","devicePid":"dhdhuf","imageName":null,"online":null,"role":"0","userName":null,"deviceType":null,"phone":null,"account":null},{"pid":"4028aa825a93baf6015ada4880cb0022","description":null,"userPid":"4028aa825a93baf6015ad5b9fd50001d","name":"shdh","devicePid":"dhdhuffhrh","imageName":null,"online":null,"role":"0","userName":null,"deviceType":null,"phone":null,"account":null}]
     */

    private int result;
    private String reason;
    private ArrayList<DataBean> data;

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

    public ArrayList<DataBean> getData() {
        return data;
    }

    public void setData(ArrayList<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * pid : 4028aa825a93baf6015ada47b6470021
         * description : null
         * userPid : 4028aa825a93baf6015ad5b9fd50001d
         * name : shdh
         * devicePid : dhdhuf
         * imageName : null
         * online : null
         * role : 0
         * userName : null
         * deviceType : null
         * phone : null
         * account : null
         */

        private String pid;
        private Object description;
        private String userPid;
        private String name;
        private String devicePid;
        private Object imageName;
        private boolean online;
        private int onlineNum;
        private int battery;
        private String role;
        private Object userName;
        private Object deviceType;
        private Object phone;
        private Object account;

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

        public boolean isOnline() {
            return online;
        }

        public int getOnlineNum() {
            return onlineNum;
        }

        public void setOnlineNum(int onlineNum) {
            this.onlineNum = onlineNum;
        }

        public int getBattery() {
            return battery;
        }

        public void setBattery(int battery) {
            this.battery = battery;
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

        public boolean getOnline() {
            return online;
        }

        public void setOnline(boolean online) {
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

        public Object getAccount() {
            return account;
        }

        public void setAccount(Object account) {
            this.account = account;
        }
    }
}
