package com.example.administrator.capacityhome.smarthome.video.appteam;

import android.text.TextUtils;
import android.util.Log;

import com.tutk.IOTC.Packet;

import java.io.UnsupportedEncodingException;

public class XM_AVIOCTRLDEFs {

    // CALL
    public static final int IOTYPE_XM_CALL_REQ = 0x700;
    public static final int IOTYPE_XM_CALL_RESP = 0x701;
    public static final int IOTYPE_XM_CALL_IND = 0x702;

    // Door
    public static final int IOTYPE_XM_GETDOORLIST_REQ = 0x710;
    public static final int IOTYPE_XM_GETDOORLIST_RESP = 0x711;
    public static final int IOTYPE_XM_SETDOORINFO_REQ = 0x712;
    public static final int IOTYPE_XM_SETDOORINFO_RESP = 0x713;

    public static final int IOTYPE_XM_FINGERPRINTDEL_REQ = 0x630;
    public static final int IOTYPE_XM_FINGERPRINTDEL_RESP = 0x631;

    public static final int IOTYPE_XM_GETUNLOCKPASSWDSWITCH_REQ = 0x714;
    public static final int IOTYPE_XM_GETUNLOCKPASSWDSWITCH_RESP = 0x715;
    public static final int IOTYPE_XM_SETUNLOCKPASSWDSWITCH_REQ = 0x716;

    public static final int IOTYPE_XM_SETUNLOCKPASSWDSWITCH_RESP = 0x717;
    public static final int IOTYPE_XM_SETUNLOCKPASSWD_REQ = 0x718;
    public static final int IOTYPE_XM_SETUNLOCKPASSWD_RESP = 0x719;

    public static final int IOTYPE_XM_UNLOCKPASSWD_IND = 0x71A;

    public static final int IOTYPE_USER_IPCAM_FILELIST_REQ = 0x031C;//列出想要查看的文件
    public static final int IOTYPE_USER_IPCAM_FILELIST_RESP = 0x031D;

    public static final int IOTYPE_XM_UNLOCK_REQ = 0x71B;
    public static final int IOTYPE_XM_UNLOCK_RESP = 0x71C;
    public static final int IOTYPE_XM_OTAUPDATE_REQ = 0x703;
    public static final int IOTYPE_XM_OTAUPDATE_RESP = 0x704;
    public static final int IOTYPE_XM_READY_WRITEDATA_REQ = 0x705;

    public static final int IOTYPE_XM_READY_WRITEDATA_RESP = 0x706;
    public static final int IOTYPE_XM_INQUIRE_READYSTATE_REQ = 0x707;

    public static final int IOTYPE_XM_INQUIRE_READYSTATE_RESP = 0x708;
    public static final int IOTYPE_XM_UPGRADE_COMPLETE_REQ = 0x709;

    public static final int IOTYPE_XM_UPGRADE_COMPLETE_RESP = 0x70A;
    public static final int IOTYPE_XM_UPGRADE_FW = 0x70B;

    public static final int IOTYPE_XM_USER_INSERT_I_FRAME = 0X2710;

    public static final int IOTYPE_USER_SET_TIME_CONFIG_REQ = 0xFF00051E;
    public static final int IOTYPE_USER_SET_TIME_CONFIG_RESP = 0xFF00051F;

    public static final int IOTYPE_USER_IPCAM_LISTWIFIAPALL_REQ = 0x808;
    public static final int IOTYPE_USER_IPCAM_LISTWIFIAPALL_RESP = 0x809;


    public static final int IOTYPE_USER_NOVATEK_CUSTOM_REQ = 0x7F000008;
    public static final int IOTYPE_USER_NOVATEK_CUSTOM_RESP = 0x7F000009;
    // battery
    public final static int IOTYPE_XM_QUERYBATTERY_REQ = 0x636;
    public final static int IOTYPE_XM_QUERYBATTERY_RESP = 0x637;

    public static class SMsgAVIoctrlListWifiApAllReq {

        public static byte[] parseContent() {

            return new byte[4];
        }
    }

    public static final int IOTYPE_XM_PREPAREFILE_REQ = 0x644;
    public static final int IOTYPE_XM_PREPAREFILE_RESP = 0x645;
    public static final int IOTYPE_XM_GETFILE_REQ = 0x646;
    public static final int IOTYPE_XM_GETFILE_RESP = 0x647;

    //IOTYPE_XM_PREPAREFILE_REQ = 0x644;
    public static class SmsgAVIoctrlPrepareFileReq {
        //预先取出将要下载文件的信息开门记录在sd卡中路径: list.txt里面就是开门记录
        //filepath最大252个字节不足的补0
        public static byte[] parseConent(byte[] filepath) {
            byte[] data = new byte[256];

            byte[] cmd = Packet.intToByteArray_Little(IOTYPE_XM_PREPAREFILE_REQ);
            System.arraycopy(cmd, 0, data, 0, 4);

            System.arraycopy(filepath, 0, data, 4, filepath.length);

            return data;
        }
    }

    //IOTYPE_XM_PREPAREFILE_RESP = 0x645;
    public static class SmsgAVIoctrlPrepareFileResp {
        int file_size;//文件大小
        int result; //0: 成功 其他:失败

        public SmsgAVIoctrlPrepareFileResp(byte[] data) {
            result = Packet.byteArrayToInt_Little(data, 4);
            file_size = Packet.byteArrayToInt_Little(data, 8);
        }

        public int getFile_size() {
            return file_size;
        }

        public int getResult() {
            return result;
        }
    }

    //IOTYPE_XM_GETFILE_REQ = 0x646;
    public static class SmsgAVIoctrlGetFileReq {
        //预先取出将要下载文件的信息
        //read_pos 从哪里开始读文件, 读取文件最大大小size　最大只能是1008个字节
        public static byte[] parseConent(int read_pos, int size) {
            byte[] data = new byte[1020];

            byte[] cmd = Packet.intToByteArray_Little(IOTYPE_XM_GETFILE_REQ);
            System.arraycopy(cmd, 0, data, 0, 4);

            byte[] pos = Packet.intToByteArray_Little(read_pos);
            System.arraycopy(pos, 0, data, 4, 4);

            byte[] max = Packet.intToByteArray_Little(size);
            System.arraycopy(max, 0, data, 8, 4);


            return data;
        }
    }

    //IOTYPE_XM_GETFILE_RESP = 0x647;
    public static class SmsgAVIoctrlGetBigFileResp {
        int read_pos;//文件读到的位置
        int read_size; //当前读到文件的大小
        byte[] file_data = new byte[1008];

        //        byte[] data;
        public SmsgAVIoctrlGetBigFileResp(byte[] data) {
//            this.data = data;
            read_pos = Packet.byteArrayToInt_Little(data, 4);
            read_size = Packet.byteArrayToInt_Little(data, 8);
            if (read_size > 0)
                System.arraycopy(data, 12, file_data, 0, read_size);
        }

//        public byte[] getData() {
//            return data;
//        }

        public int getRead_pos() {
            return read_pos;
        }

        public int getRead_size() {
            return read_size;
        }

        public byte[] getFile_data() {
            return file_data;
        }

    }

    //IOTYPE_XM_GETFILE_RESP = 0x647;
    public static class SmsgAVIoctrlGetFileResp {
        int read_pos;//文件读到的位置
        int read_size; //当前读到文件的大小
        byte[] file_data = new byte[1008];

        //        byte[] data;
        public SmsgAVIoctrlGetFileResp(byte[] data) {
//            this.data = data;
            read_pos = Packet.byteArrayToInt_Little(data, 4);
            read_size = Packet.byteArrayToInt_Little(data, 8);
            Log.i("fileData", read_pos + ":" + read_size);
            if (read_size > 0)
                System.arraycopy(data, 12, file_data, 0, read_size);
        }

//        public byte[] getData() {
//            return data;
//        }

        public int getRead_pos() {
            return read_pos;
        }

        public int getRead_size() {
            return read_size;
        }

        public byte[] getFile_data() {
            return file_data;
        }

    }

    public static final int IOTYPE_XM_PREPAREFPLIST_REQ = 0x632;
    public static final int IOTYPE_XM_PREPAREFPLIST_RSP = 0x633;

    public static final int IOTYPE_XM_GETFPLIST_REQ = 0x634;
    public static final int IOTYPE_XM_GETFPLIST_RSP = 0x635;

    //IOTYPE_XM_PREPAREFPLIST_RSP = 0x633;
    public static class SmsgAVIoctrlPrepareFpListResp {
        int total;//指纹总共要取多少次,一次取３０个指纹

        public SmsgAVIoctrlPrepareFpListResp(byte[] data) {
            total = Packet.byteArrayToInt_Little(data, 4);
        }

        public int getTotal() {
            return total;
        }
    }

    //IOTYPE_XM_GETFPLIST_REQ = 0x634;
    public static class SmsgAVIoctrlGetFpListReq {
        //cnt表示要取第几次指纹
        public static byte[] parseConent(int cnt) {
            byte[] data = new byte[1020];

            byte[] cmd = Packet.intToByteArray_Little(IOTYPE_XM_GETFPLIST_REQ);
            System.arraycopy(cmd, 0, data, 0, 4);

            byte[] cn = Packet.intToByteArray_Little(cnt);
            System.arraycopy(cn, 0, data, 4, 4);

            return data;
        }
    }

    //IOTYPE_XM_GETFPLIST_RSP = 0x635;
//指纹列表一次传输30个
    public static class SmsgAVIoctrlGetFpListResp {
        int times;//指纹总共要取多少次
        int result; //0: 成功 其他: 失败
        /*
         *  times : 第幾次存取
         *	result : 存取結果
         *	 0 : 成功
         *	data:  指紋列表, 以ASCII編碼存放
         *		一筆指紋資料 (  指紋ID , 指紋名字 )共32bytes
         *      指紋格式為 "DDD NNN…" , DDD為十進制的數字, NNN… 為名字
         *		3. 一次可傳30 筆指紋資料.
         *		total =  ( 指紋列表總筆數 / 30 ) , 有餘數時 total + 1
         */
//		byte [] fp = new byte[1008];
        fingerprintInfo[] info;

        public SmsgAVIoctrlGetFpListResp(byte[] data) {
            times = Packet.byteArrayToInt_Little(data, 4);
            result = Packet.byteArrayToInt_Little(data, 8);
//			System.arraycopy(data, 8, fp, 0, 1008);
            info = new fingerprintInfo[result];
            for (int i = 0; i < result; i++) {
                byte[] tmp = new byte[32];
                System.arraycopy(data, 12 + i * 32, tmp, 0, 32);
                info[i] = new fingerprintInfo(tmp);
            }

        }

        public void setResult(int result) {
            this.result = result;
        }

        public void setInfo(fingerprintInfo[] info) {
            this.info = info;
        }

        public fingerprintInfo[] getInfo() {
            return info;
        }

//		public byte[] getFp(){
//			return fp;
//		}

        public int getResult() {
            return result;
        }
    }

    public static class fingerprintInfo {
        int id;
        byte[] name = new byte[28];  //name遇到0时就分割出来

        public int getId() {
            return id;
        }

        public byte[] getName() {
            return name;
        }

        public void setName(byte[] name) {
            this.name = name;
        }

        public fingerprintInfo(byte[] data) {
//            id = Packet.byteArrayToInt_Little(data, 0);
//            Log.i("fingerDatas", "id:" + id + Arrays.toString(data));
//            System.arraycopy(data, 4, name, 0, 21);
//            byte[] result1 = {2, 55, 53, 51};
//            byte[] result2 = {2, 49};
//            byte[] result3 = {2, 50};
//            byte[] result4 = {2, 53};
//            byte[] result5 = {2, 54};
//            Log.i("fingerDatas", new String(result1) + ":" + new String(result2) + ":" + new String(result3) + ":" + new String(result4) + ":" + new String(result5));
            byte[] resultBytes = new byte[3];
            System.arraycopy(data, 0, resultBytes, 0, 3);
            try {
                String resule = new String(resultBytes, "ascii").trim();
                if (!TextUtils.isEmpty(resule)) {
                    id = Integer.parseInt(resule);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.arraycopy(data, 4, name, 0, 28);
        }
    }


    public static class SMsgAVIoctrlOTAUpdateRESP {

        int nAnswered;//0:������  1������
        byte[] reserved = new byte[4];

        public static byte[] paraseContent(int nAnswered) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(nAnswered);
            System.arraycopy(ch, 0, result, 0, 4);
            return result;
        }
    }

    public static class SQVSETTimeREQ {
        int channel;
        long time;
        byte tZone; // +-12
        byte[] reserved = new byte[7];

        public static byte[] paraseContent(int channel, long time, int tZone) {
            byte[] result = new byte[20];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            ch = Packet.intToByteArray_Little((int) time);
            System.arraycopy(ch, 0, result, 4, 4);
            ch = Packet.intToByteArray_Little(tZone);
            result[8] = ch[0];
            /* ch = Packet.intToByteArray_Little(value) */
            return result;

        }

    }


    // IOTYPE_YLK_CALL_RESP (0x604) APP --->Device
    public static class SMsgAVIoctrlCallResp {
        byte index;        //0  1
        int nAnswered; // 0 �ҵ� 1 ����
        byte[] reserved = new byte[3];

        public static byte[] paraseContent(byte index, int nAnswered) {

            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(nAnswered);
            result[0] = index;
            System.arraycopy(ch, 0, result, 1, 4);
            return result;

        }
    }

    public static class SmsgAVIoctrlGetDoorListReq {

        public static byte[] parseContent() {
            byte[] result = new byte[4];
            return result;
        }
    }

    // IOTYPE_YLK_SETDOORINFO_REQ(0x613)
    public static class SMsgAVIoctrlSetDoorInfoReq {
        byte index;
        byte nOpen;
        int time;
        byte[] reserved = new byte[2];

        public static byte[] paraseContent(byte index, byte nOpen, int time) {

            byte[] result = new byte[12];
            result[0] = index;
            result[1] = nOpen;
            byte[] ch = new byte[4];
            ch = Packet.intToByteArray_Little(time);
            System.arraycopy(ch, 0, result, 4, 4);
            return result;
        }

    }

    // IOTYPE_YLK_GETUNLOCKPASSWDSWITCH_REQ 0x615
    public static class SMsgAVIoctrlGetUnlockSwitchReq {
        public static byte[] parseContent() {

            byte[] result = new byte[8];
            return result;

        }
    }

    // IOTYPE_YLK_GETUNLOCKPASSWDSWITCH_RESP(0x616)

    public static class SMsgAVIoctrlGetUnlockSwitchResp {
        int nEnable;
        byte[] reserved = new byte[4];

    }

    // IOTYPE_YLK_SETUNLOCKPASSWDSWITCH_REQ 0x617
    public static class SMsgAVIoctrlSetUnlockSwitchReq {
        int nEnable; //
        byte[] pwd = new byte[32];
        byte[] reserved = new byte[4];

        public static byte[] parseContent(int nEnable, String pwd) {

            byte[] result = new byte[40];
            byte[] ch = Packet.intToByteArray_Little(nEnable);
            System.arraycopy(ch, 0, result, 0, 4);
            if (pwd != null)
                System.arraycopy(pwd.getBytes(), 0, result, 4,
                        pwd.getBytes().length);
            return result;

        }

    }

    // IOTYPE_YLK_SETUNLOCKPASSWDSWITCH_RESP(0x618)
    public static class SMsgAVIoctrlSetUnlockSwitchResp {
        int result;
        byte[] reserved = new byte[4];
    }

    // IOTYPE_YLK_SETUNLOCKPASSWD_REQ = 0x619;
    public static class SMsgAVIoctrlSetUnlockPasswdReq {
        byte[] OldPasswd = new byte[32];
        byte[] NewPasswd = new byte[32];

        public static byte[] parseContent(String OldPasswd, String NewPasswd) {
            byte[] result = new byte[64];
            System.arraycopy(OldPasswd.getBytes(), 0, result, 0,
                    OldPasswd.length());
            System.arraycopy(NewPasswd.getBytes(), 0, result, 32,
                    NewPasswd.length());
            return result;
        }
    }

    // public static final int IOTYPE_YLK_UNLOCK_REQ = 0x624;
    public static final int IOTYPE_XM_USERUNLOCK_REQ = 0x640;
    public static final int IOTYPE_XM_USERUNLOCK_RESP = 0x641;

    public static class SMsgAVIoctrlUnlockReq {
        int channel; // Camera Index
        byte index;
        byte[] passwd = new byte[32];
        byte[] reserved = new byte[3];

        public static byte[] paraseContent(int channel, byte index, String pwd) {
            byte[] result = new byte[64];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            result[4] = index;
            if (pwd == null) {
                result[5] = '8';
                result[6] = '8';
                result[7] = '8';
                result[8] = '8';
                result[9] = '8';
                result[10] = '8';
            } else {
                System.arraycopy(pwd.getBytes(), 0, result, 5, pwd.length());

            }

            return result;
        }

        public static byte[] parseConent(int isOn) {
            byte[] data = new byte[12];

            byte[] cmd = Packet.intToByteArray_Little(IOTYPE_XM_USERUNLOCK_REQ);
            System.arraycopy(cmd, 0, data, 0, 4);

            byte[] tmp = Packet.intToByteArray_Little(isOn);
            System.arraycopy(tmp, 0, data, 4, 4);

            return data;
        }
    }

    public static class SmsgAVIoctrlUnlockResp {
        int result;//0：成功  other:失败
        private int restlt;

        public SmsgAVIoctrlUnlockResp(byte[] data) {
            result = Packet.byteArrayToInt_Little(data, 4);
        }

        public int getRestlt() {
            return restlt;
        }
    }

    // IOTYPE_YLK_VERIFYUNLOCKPASSWD_REQ = 0x622;
    public static class SMsgAVIoctrlVerifyUnlockPasswdReq {
        byte[] passwd = new byte[32];

        public static byte[] paraseContent(String strpasswd) {
            byte[] result = new byte[32];
            System.arraycopy(strpasswd.getBytes(), 0, result, 0,
                    strpasswd.getBytes().length);

            return result;
        }
    }

    // IOTYPE_YLK_GETDEFENCESTATUS_REQ(0x626)
    public static class SMsgAVIoctrlGetDefenceStatusReq {
        int channel;
        byte DefenceType;
        byte[] reserved = new byte[3];

        public static byte[] paraseContent(int channel, byte DefenceType) {
            byte[] result = new byte[12];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            result[4] = DefenceType;
            return result;

        }
    }

    // IOTYPE_YLK_SETDEFENCESTATUS_REQ(0x628)

    public static class SMsgAVIoctrlSetDefenceStatusReq {
        int channel;
        byte DefenceType;
        byte[] reserved = new byte[3];

        public static byte[] paraseContent(int channel, byte DefenceType) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            result[4] = DefenceType;
            return result;

        }

    }


    //IOTYPE_XM_PREPAREFPLIST_REQ = 0x632;
    public static class SmsgAVIoctrlPrepareFpListReq {
        public static byte[] parseConent() {
            byte[] data = new byte[12];

            byte[] cmd = Packet.intToByteArray_Little(IOTYPE_XM_PREPAREFPLIST_REQ);
            System.arraycopy(cmd, 0, data, 0, 4);

            return data;
        }
    }

    // IOTYPE_YLK_VERIFYDISARMPASSWD_REQ(0x630)
    public static class SMsgAVIoctrlVerifyDisarmPasswdReq {
        byte[] passwd = new byte[32];

        public static byte[] paraseContent(String strpasswd) {
            byte[] result = new byte[32];
            System.arraycopy(strpasswd.getBytes(), 0, result, 0,
                    strpasswd.getBytes().length);

            return result;
        }
    }

    // IOTYPE_YLK_GETDEFENCELIST_REQ(0x632)
    public static class SMsgAVIoctrlGetDefenceListReq {
        public static byte[] parseContent() {

            byte[] result = new byte[8];
            return result;

        }
    }

    // IOTYPE_YLK_GETALARMSTATUS_REQ(0x634)
    public static class SMsgAVIoctrlGetAlarmStatusReq {
        int channel;
        byte alarmType; // refer to ENUM_ALARMTYPE
        byte[] reserved = new byte[3];

        public static byte[] paraseContent(int channel, byte alarmType) {
            byte[] result = new byte[12];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            result[4] = alarmType;
            return result;

        }
    }

    // IOTYPE_YLK_TIGGERALARM_REQ(0x636)
    public static class SMsgAVIoctrlTriggerAlarmReq {
        int channel;
        byte alarmType; // refer to ENUM_ALARMTYPE
        byte[] reserved = new byte[3];

        public static byte[] paraseContent(int channel, byte alarmType) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            result[4] = alarmType;
            return result;

        }
    }

    // //IOTYPE_YLK_VERIFYDISARMPASSWD_RESP
    public static class SMsgAVIoctrlVerifyPasswdResp {
        int result; // 0 OK or falied
        byte[] reserved = new byte[4];
    }

    // IOTYPE_YLK_GETALARMLIST_REQ 0x639
    public static class SMsgAVIoctrlGetAlarmListReq {
        public static byte[] parseContent() {

            byte[] result = new byte[8];
            return result;

        }
    }


    //IOTYPE_XM_READY_WRITEDATA_ RESP (0x609)
    public static class SMsgAVIoctrlReadyWriteResp {
        public static byte[] parseContent() {
            byte[] result = new byte[4];
            return result;
        }
    }

    //IOTYPE_XM_INQUIRE_READYSTATE _REQ (0x60A)
    public static class SMsgAVIoctrlInquireReadyReq {
        public static byte[] parseContent() {
            byte[] result = new byte[4];
            return result;
        }
    }

    //IOTYPE_XM_ UPGRADE _COMPLETE_RESP (0x60D)
    public static class SMsgAVIoctrlCompleteUpgradeResp {
        public static byte[] parseContent() {
            byte[] result = new byte[4];
            return result;
        }
    }

    public static class SMsgAVIoctrlGetSupportStreamReq {
        byte[] reserved = new byte[4];

        public static byte[] parseContent(int ntype) {

            byte[] result = new byte[4];
            byte[] ch = Packet.intToByteArray_Little(ntype);
            System.arraycopy(ch, 0, result, 0, 4);
            return result;
        }
    }

    //IOTYPE_XM_QUERYBATTERY_REQ = 0x636
    public static class SmsgAVIoctrlQuerBatteryReq {
        byte[] reserved = new byte[4];

        //        public static byte[] parseConent() {
//            byte[] data = new byte[4];
//            return data;
//        }
        public static byte[] parseConent(int isOn) {

            byte[] data = new byte[12];
            //增加４个字节的子cmd
            byte[] cmd = Packet.intToByteArray_Little(IOTYPE_XM_QUERYBATTERY_REQ);
            System.arraycopy(cmd, 0, data, 0, 4);

            byte[] tmp = Packet.intToByteArray_Little(isOn);
            System.arraycopy(tmp, 0, data, 4, 4);

            return data;
        }
    }

    //IOTYPE_XM_QUERYBATTERY_RESP = 0x637,
    public static class SmsgAVIoctrlQuerBatteryResp {
        int result; //电池电量本百分比，如:99 代表99%
        byte[] reserved = new byte[4];

        public int getResult() {
            return result;
        }

        public SmsgAVIoctrlQuerBatteryResp(byte[] data) {
            result = Packet.byteArrayToInt_Little(data, 0);
        }
    }

    //IOTYPE_XM_FINGERPRINTDEL_RESP = 0x631,
    public static class SmsgAVIoctrlFingerPrintDelResp {
        int result;//0：成功  other:失败
        byte[] reserved = new byte[4];

        public SmsgAVIoctrlFingerPrintDelResp(byte[] data) {
            result = Packet.byteArrayToInt_Little(data, 4);
        }

        public int getResult() {
            return result;
        }
    }


    public static final int IOTYPE_XM_FINGERPRINTADD_REQ = 0x62D;
    public static final int IOTYPE_XM_FINGERPRINTADD_RESP = 0x62e;

    //IOTYPE_XM_FINGERPRINTDEL_REQ = 0x630,
    public static class SmsgAVIoctrlFingerPrintDelReq {
        int id; //将要删除的指纹id
        byte[] reserved = new byte[4];

        public static byte[] parseConent(int id) {
            byte[] data = new byte[12];
            byte[] cmd = Packet.intToByteArray_Little(IOTYPE_XM_FINGERPRINTDEL_REQ);
            byte[] tmp = Packet.intToByteArray_Little(id);
            System.arraycopy(tmp, 0, data, 4, 4);
            System.arraycopy(cmd, 0, data, 0, 4);

            return data;
        }
    }

    public static class SmsgAVIoctrlFingerPrintAddReq {
        //name不足２4的后面全补0
        public static byte[] parseConent(int id, String name) throws UnsupportedEncodingException {
            byte[] data = new byte[36];

            //增加４个字节的子cmd
            byte[] cmd = Packet.intToByteArray_Little(IOTYPE_XM_FINGERPRINTADD_REQ);
            System.arraycopy(cmd, 0, data, 0, 4);

            byte[] nameByte = new byte[24];
//            byte[] currentByte = name.getBytes("US-ASCII");
            byte[] currentByte = name.getBytes("utf-16");
            System.arraycopy(currentByte, 0, nameByte, 0, currentByte.length);

            byte[] tmp = Packet.intToByteArray_Little(id);
            System.arraycopy(tmp, 0, data, 4, 4);
            System.arraycopy(nameByte, 0, data, 8, 24);

            return data;
        }
    }

    //IOTYPE_XM_FINGERPRINTADD_RESP = 0x62e,
    public static class SmsgAVIoctrlFingerPrintAddResp {
        int result; //0：成功
        byte[] reserved = new byte[28];

        public SmsgAVIoctrlFingerPrintAddResp(byte[] data) {
            result = Packet.byteArrayToInt_Little(data, 4);
            System.arraycopy(data, 8, reserved, 0, 28);
        }

        public int getResult() {
            return result;
        }

        public byte[] getReserved() {
            return reserved;
        }
    }

    public static final int IOTYPE_XM_FINGERPRINTADD_RESULT_REQ = 0x642;
    public static final int IOTYPE_XM_FINGERPRINTADD__RESULT_RESP = 0x643;

    public static class SmsgAVIoctrlFingerAddResultReq {
        public static byte[] parseContent(int id) {
            byte[] data = new byte[12];

            byte[] cmd = Packet.intToByteArray_Little(IOTYPE_XM_FINGERPRINTADD_RESULT_REQ);
            System.arraycopy(cmd, 0, data, 0, 4);

            return data;
        }
    }

    public static class SmsgAViotrlFingerAddResultResp {
        /**
         * 1 : 指紋添加中
         * 0：成功
         * -2: 指纹已经添加
         * other:失败
         */
        int result;

        public SmsgAViotrlFingerAddResultResp(byte[] data) {
            result = Packet.byteArrayToInt_Little(data, 4);
        }

        public int getResult() {
            return result;
        }

    }
}
