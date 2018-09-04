package com.example.administrator.capacityhome.smarthome.video.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.smarthome.video.appteam.DatabaseManager;
import com.example.administrator.capacityhome.smarthome.video.obj.DeviceUsers;
import com.example.administrator.capacityhome.smarthome.video.obj.MyCamera;
import com.example.administrator.capacityhome.smarthome.video.util.ConnectUtil;
import com.example.administrator.capacityhome.smarthome.video.util.InfoUtil;
import com.example.administrator.capacityhome.smarthome.video.util.OnRequestListener;
import com.tutk.IOTC.Camera;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IOTC.st_LanSearchInfo;

public class AddDeviceActivity extends Activity {

    private final int REQUEST_CODE_GETUID_BY_SCAN_BARCODE = 0;
    public static final String TOTAL_NUMBER = "totalnumber_shpreference";

    private EditText edtUID;
    private EditText edtSecurityCode;
    private EditText edtName;
    private Button btnScan;
    private Button btnSearch;
    private Button btnOK;
    private TextView bindUser;
    private Button btnCancel;
    //	private Broadcast resultStateReceiver;
//	private IntentFilter filter;
    private ImageView imTpnsreg;
    private String mClient;

    private DatagramSocket socket = null;
    private InetAddress serverAddress = null;

    // private final static String ZXING_DOWNLOAD_URL = "http://zxing.googlecode.com/files/BarcodeScanner3.72.apk";
    // private final static String ZXING_DOWNLOAD_URL = "http://www.tutk.com/IOTCApp/Android/BarcodeScanner.apk";
    private final static String ZXING_DOWNLOAD_URL = "http://push.iotcplatform.com/release/BarcodeScanner.apk";

    private List<SearchResult> list = new ArrayList<SearchResult>();
    public static List<MyCamera> CameraList = Collections.synchronizedList(new ArrayList<MyCamera>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActionBar actionBar = getActionBar();
//
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setCustomView(R.layout.titlebar);
//        TextView tv = (TextView) this.findViewById(R.id.bar_text);
//        tv.setText(getText(R.string.dialog_AddCamera));
//        imTpnsreg = (ImageView) findViewById(R.id.imTpnsreg);
//        imTpnsreg.setVisibility(View.GONE);
//        ImageButton ibtn_Back = (ImageButton) findViewById(R.id.bar_left_btn);
//        ibtn_Back.setOnClickListener(btnOKOnClickListener);
//        ibtn_Back.setVisibility(View.GONE);
        setContentView(R.layout.add_doorphone);

        edtUID = (EditText) findViewById(R.id.edtUID);
        edtSecurityCode = (EditText) findViewById(R.id.edtSecurityCode);
        edtName = (EditText) findViewById(R.id.edtNickName);
        int nTotalNum = XMMainActivity.CameraList.size() + 1;
        String name = (String) getText(R.string.txt_device_name) + nTotalNum;
        edtName.setText(name);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(btnScanClickListener);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(btnSearchOnClickListener);
        btnOK = (Button) findViewById(R.id.btnOK);
        bindUser = (TextView) findViewById(R.id.bindUser);
        btnOK.setOnClickListener(btnOKOnClickListener);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(btnCancelOnClickListener);
        edtUID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 20) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("type", "DeviceHandler");
                    map.put("action", "getBindUser");
                    map.put("data", charSequence);
                    ConnectUtil.request("/smart/device/getBindUser", map, new OnRequestListener() {
                        @Override
                        public void onRequestSuccess(String result) {
                            DeviceUsers deviceUsers = (DeviceUsers) com.alibaba.fastjson.JSONObject.parseObject(result, DeviceUsers.class);
                            List<DeviceUsers.DataBean> dataBeens = deviceUsers.getData();
                            String bindUser = null;
                            for (int i = 0; i < dataBeens.size(); i++) {
                                DeviceUsers.DataBean dataBean = dataBeens.get(i);
                                if ("0".equals(dataBean.getRole())) {
                                    bindUser = dataBean.getAccount();
                                }
                            }
                            if (TextUtils.isEmpty(bindUser)) {
                                edtUID.setVisibility(View.GONE);
                            } else {
                                edtUID.setVisibility(View.VISIBLE);
                                edtUID.setText("该设备已被账户" + bindUser + "绑定");
                            }
                        }

                        @Override
                        public void onFail() {
                            super.onFail();
                        }
                    });
                } else {
                    edtUID.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_CODE_GETUID_BY_SCAN_BARCODE) {

            if (resultCode == RESULT_OK) {

                String contents = intent.getStringExtra("SCAN_RESULT");
                // String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                if (null == contents) {
                    Bundle bundle = intent.getExtras();
                    if (null != bundle) {
                        /* String */
                        contents = bundle.getString("result");
                    }
                }
                if (null == contents) {
                    return;
                }

                if (contents.length() > 20) {
                    String temp = "";

                    for (int t = 0; t < contents.length(); t++) {
                        if (contents.substring(t, t + 1).matches("[A-Z0-9]{1}"))
                            temp += contents.substring(t, t + 1);
                    }
                    contents = temp;
                }

                edtUID.setText(contents);

                edtSecurityCode.requestFocus();

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    private View.OnClickListener btnScanClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            doScan();
        }
    };

    private void doScan() {

        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(AddDeviceActivity.this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(AddDeviceActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_PERMISSION_CAMERA);
            return;
        }
        Intent mintent = new Intent();
        mintent = mintent.setClass(AddDeviceActivity.this, qr_codeActivity.class);
        AddDeviceActivity.this.startActivityForResult(mintent, REQUEST_CODE_GETUID_BY_SCAN_BARCODE);
    }


    private static final int REQUEST_CODE_PERMISSION_CAMERA = 100;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("LiveView", " onRequestPermissionsResult " + requestCode);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doScan();
                }
                break;
        }
    }


    private View.OnClickListener btnSearchOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddDeviceActivity.this, R.style.HoloAlertDialog));
            final AlertDialog dlg = builder.create();
            dlg.setTitle(getText(R.string.dialog_LanSearch));
            dlg.setIcon(android.R.drawable.ic_menu_more);

            LayoutInflater inflater = dlg.getLayoutInflater();
            View view = inflater.inflate(R.layout.search_device, null);
            dlg.setView(view);

            ListView lstSearchResult = (ListView) view.findViewById(R.id.lstSearchResult);
            Button btnRefresh = (Button) view.findViewById(R.id.btnRefresh);

            list.clear();
            com.tutk.IOTC.st_LanSearchInfo[] arrResp = Camera.SearchLAN();

            if (arrResp != null && arrResp.length > 0) {
                for (com.tutk.IOTC.st_LanSearchInfo resp : arrResp) {

                    list.add(new SearchResult(new String(resp.UID).trim(), new String(resp.IP).trim(), (int) resp.port));
                }
            }

            final SearchResultListAdapter adapter = new SearchResultListAdapter(dlg.getLayoutInflater());
            lstSearchResult.setAdapter(adapter);

            lstSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    SearchResult r = list.get(position);
                    edtUID.setText(r.UID);
                    dlg.dismiss();
                }
            });

            btnRefresh.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    list.clear();
                    com.tutk.IOTC.st_LanSearchInfo[] arrResp = Camera.SearchLAN();

                    if (arrResp != null && arrResp.length > 0) {
                        for (com.tutk.IOTC.st_LanSearchInfo resp : arrResp) {

                            list.add(new SearchResult(new String(resp.UID).trim(), new String(resp.IP).trim(), (int) resp.port));
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
            });

            dlg.show();
        }
    };

    private View.OnClickListener btnOKOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            final String dev_nickname = edtName.getText().toString();
            final String dev_uid = edtUID.getText().toString().trim();
            final String view_pwd = edtSecurityCode.getText().toString().trim();
            if (dev_nickname.length() == 0) {
                MainActivity_video.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_camera_name), getText(R.string.ok));
                return;
            }

            if (dev_uid.length() == 0) {
                MainActivity_video.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_dev_uid), getText(R.string.ok));
                return;
            }

            if (dev_uid.length() != 20) {
                MainActivity_video.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_dev_uid_character), getText(R.string.ok));
                return;
            }

            if (!dev_uid.matches("[a-zA-Z0-9]+")) {
                MainActivity_video.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_dev_uid_special_characters), getText(R.string.ok));
                return;
            }

            if (view_pwd.length() == 0) {
                MainActivity_video.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_dev_security_code), getText(R.string.ok));
                return;
            }

            final int video_quality = 0;
            final int channel = 0;
            boolean duplicated = false;
            for (MyCamera camera : XMMainActivity.CameraList) {
                if (dev_uid.equalsIgnoreCase(camera.getUID())) {
                    duplicated = true;
                    break;
                }
            }

            if (duplicated) {
                //XMMainActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_add_camera_duplicated), getText(R.string.ok));
                AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(AddDeviceActivity.this);
                dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                dlgBuilder.setTitle(getText(R.string.tips_warning));
                dlgBuilder.setMessage(getText(R.string.tips_add_camera_duplicated));
                dlgBuilder.setPositiveButton(getText(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).show();
                return;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("type", "DeviceHandler");
            map.put("action", "add");
            Map<String, String> data = new HashMap<>();
            data.put("userPid", InfoUtil.getString(AddDeviceActivity.this, "pid"));
            data.put("devicePid", dev_uid);
            data.put("name", "");
            JSONObject jsonObject = new JSONObject(data);
            map.put("data", jsonObject);
            ConnectUtil.request("/smart/device/add", map, new OnRequestListener() {
                @Override
                public void onRequestSuccess(String result) {
                    BindResult bindResult = (BindResult) FastJsonUtil.get(result, BindResult.class);
//                    toSaveDevice(dev_nickname, dev_uid, view_pwd, channel, video_quality);
                    if (bindResult.getResult() == 1) {
                        toSaveDevice(dev_nickname, dev_uid, view_pwd, channel, video_quality);


                    } else {
                        Toast.makeText(AddDeviceActivity.this, "连接失败，原因：" + bindResult.getReason() + "，请联系设备管理员", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    private void toSaveDevice(String dev_nickname, String dev_uid, String view_pwd, int channel, int video_quality) {
        /* add value to data base */
        DatabaseManager manager = new DatabaseManager(AddDeviceActivity.this);
        long db_id = manager.addDevice(dev_nickname, dev_uid, "", "", "admin", view_pwd, 3, channel, 1);

        Toast.makeText(AddDeviceActivity.this, getText(R.string.tips_add_camera_ok).toString(), Toast.LENGTH_SHORT).show();
        /* return value to main activity */
        Bundle extras = new Bundle();
        extras.putLong("db_id", db_id);
        extras.putString("dev_nickname", dev_nickname);
        extras.putString("dev_uid", dev_uid);
        extras.putString("dev_name", "");
        extras.putString("dev_pwd", "");
        extras.putString("view_acc", "admin");
        extras.putString("view_pwd", view_pwd);
        extras.putInt("video_quality", video_quality);
        extras.putInt("camera_channel", 0);

        Intent Intent = new Intent();
        Intent.putExtras(extras);
        AddDeviceActivity.this.setResult(RESULT_OK, Intent);
        finish();
    }

    private View.OnClickListener btnCancelOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    };

    private boolean isSDCardValid() {

        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void startInstall(File tempFile) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(tempFile), "application/vnd.android.package-archive");

        startActivity(intent);
    }

    private void startDownload(String url) throws Exception {

        if (URLUtil.isNetworkUrl(url)) {

            URL myURL = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
            // conn.connect();

            InputStream is = conn.getInputStream();

            if (is == null) {
                return;
            }

            BufferedInputStream bis = new BufferedInputStream(is);

            File myTempFile = File.createTempFile("BarcodeScanner", ".apk", Environment.getExternalStorageDirectory());
            FileOutputStream fos = new FileOutputStream(myTempFile);

            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = bis.read(buffer)) > 0) {
                fos.write(buffer, 0, read);
            }

            try {
                fos.flush();
                fos.close();
            } catch (Exception ex) {
                System.out.println("error: " + ex.getMessage());
            }

            startInstall(myTempFile);
        }
    }

    private class SearchResult {

        public String UID;
        public String IP;

        // public int Port;

        public SearchResult(String uid, String ip, int port) {

            UID = uid;
            IP = ip;
            // Port = port;
        }
    }

    private class SearchResultListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public SearchResultListAdapter(LayoutInflater inflater) {

            this.mInflater = inflater;
        }

        public int getCount() {

            return list.size();
        }

        public Object getItem(int position) {

            return list.get(position);
        }

        public long getItemId(int position) {

            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            final SearchResult result = (SearchResult) getItem(position);
            ViewHolder holder = null;

            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.search_device_result, null);

                holder = new ViewHolder();
                holder.uid = (TextView) convertView.findViewById(R.id.uid);
                holder.ip = (TextView) convertView.findViewById(R.id.ip);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.uid.setText(result.UID);
            holder.ip.setText(result.IP);
            // holder.port.setText(result.Port);

            return convertView;
        }// getView()

        public final class ViewHolder {
            public TextView uid;
            public TextView ip;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
