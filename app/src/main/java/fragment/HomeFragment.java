package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.EvaluateListActivity;
import com.example.administrator.capacityhome.Main_NewActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.VideoPlayActivity;
import com.example.administrator.capacityhome.WebViewUrlActivity;
import com.example.administrator.capacityhome.activitylist.ActivityListActivity;
import com.example.administrator.capacityhome.combo.ComboDetailActivity;
import com.example.administrator.capacityhome.combo.StoreComboActivity;
import com.example.administrator.capacityhome.myorder.MyOrderActivity;
import com.example.administrator.capacityhome.mywallet.MyWalletActivity;
import com.squareup.picasso.Picasso;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MyGridAdapter_home;
import adapter.RecommendAdvAdapter;
import adapter.RecommendAdv_evaluateAdapter;
import adapter.StoreComnoAdapter;
import adapter.StoreComnoAdapter2;
import http.RequestTag;
import utils.ColumnUtils;
import utils.GetAgrement;
import utils.Md5;
import widget.Indicator;
import widget.RecommentViewPager;
import widget.VerticalTextview;
import widget.Xcircleindicator;

/**
 * Created by Administrator on 2018/6/11.
 */

public class HomeFragment extends BaseFragment implements RecommentViewPager.onSimpleClickListener, View.OnClickListener {
    private RecommentViewPager mPager;
    private RecommentViewPager recomment_viewpager_evaluate;
    private MyGridAdapter_home myGridAdapter_home;
    private GridView item_grid;
    private ImageView img_combo;
    private RelativeLayout main_imgUser;//打开侧滑
    private List<JSONObject> imgUrl = new ArrayList<>();
    private List<JSONObject> list_gridDate = new ArrayList<>();
    private float xDistance, yDistance;
    private float xDistance_eva, yDistance_eva;
    private ColumnUtils columnUtils;
    private ImageView img_step;
    private JSONObject jsonObject_step;
    RecommendAdvAdapter mAdapter;
    RecommendAdv_evaluateAdapter evaluateAdapter;
    private GetAgrement getAgrement;
    private TextView more_detail;
    private List<JSONObject> list_combo = new ArrayList<>();
    private StoreComnoAdapter2 adapter;//套餐
    private ListView listView_combo;
    private TextView tv_more;//查看更多
    private Indicator indicator;
    /**
     * 循环滚动
     */

    private ArrayList<JSONObject> list_marque = new ArrayList<>();
    private VerticalTextview tv_vertical;

    private ArrayList<JSONObject> list_evaluate = new ArrayList<>();

    /**
     * 记录按下的X坐标
     **/
    private float mLastMotionX, mLastMotionY;
    private float mLastMotionX_eva, mLastMotionY_eva;
    /**
     * 是否是左右滑动
     **/
    private boolean mIsBeingDragged = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (imgUrl.size() < 2) {
                return;
            }
            if (mPager == null) {
                return;
            }
            if (mPager.getCurrentItem() == imgUrl.size() - 2 || mPager.getCurrentItem() == imgUrl.size() - 1) {
                mPager.setCurrentItem(1, false);
            } else {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }

        }

        ;
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                handler.postDelayed(this, 3000);
                handler.sendEmptyMessage(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getBanner_combo("app_package_ann");
        get_stepBanner("app_process_ann");
        // getAnnList();
        getAnnList();
        setItem();
        getComboList();
        getAllEvaluateList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        if (hidden) {
            // ((Main_NewActivity) getActivity()).setGoneTop(false);
            //相当于Fragment的onPause
            //((Main_NewActivity)getActivity()).setGoneTop(true);
        } else {

            // 相当于Fragment的onResume

            ((Main_NewActivity) getActivity()).setGoneTop(true);

        }

    }

    private void initView() {
        tv_more = findViewById(R.id.tv_more);
        tv_vertical = findViewById(R.id.tv_verticalTextview);
        recomment_viewpager_evaluate = findViewById(R.id.recomment_viewpager_evaluate);
        listView_combo = findViewById(R.id.listView_combo);
        adapter = new StoreComnoAdapter2(getActivity(), list_combo);
        listView_combo.setAdapter(adapter);
        img_combo = findViewById(R.id.img_combo);
        item_grid = (GridView) findViewById(R.id.item_grid);
        img_step = findViewById(R.id.img_step);
        findViewById(R.id.layout_saoma).setOnClickListener(this);
        img_combo.setOnClickListener(this);
        img_step.setOnClickListener(this);
        tv_more.setOnClickListener(this);
        findViewById(R.id.tv_evaluate_more).setOnClickListener(this);
        myGridAdapter_home = new MyGridAdapter_home(getActivity(), list_gridDate);
        item_grid.setAdapter(myGridAdapter_home);
        listView_combo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ComboDetailActivity.class);
                try {
                    intent.putExtra("id", list_combo.get(position).getString("id"));
                    startActivity(intent);
                } catch (Exception e) {
                    toastMessage("没有相关数据");
                }
            }
        });
    }


    /**
     * 为通知设置点击事件以及初始化
     */
    private void setNoticeOnclick() {
        tv_vertical.setTextList(list_marque);//加入显示内容,集合类型
        tv_vertical.setText(getResources().getDimension(R.dimen.x24), 5, R.color.fontcolor_f3);//设置属性,具体跟踪源码
        tv_vertical.setTextStillTime(3000);//设置停留时长间隔
        tv_vertical.setAnimTime(300);//设置进入和退出的时间间隔
        //对单条文字的点击监听
        tv_vertical.setOnItemClickListener(new VerticalTextview.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
// TO DO
                Intent intent2 = new Intent(getActivity(), WebViewUrlActivity.class);
                try {
                    intent2.putExtra("mark", "ann");
                    intent2.putExtra("id", list_marque.get(position).getString("id"));
                    intent2.putExtra("title", list_marque.get(position).getString("title"));
                } catch (Exception e) {

                }
                startActivity(intent2);
            }
        });
        tv_vertical.startAutoScroll();
    }


    /**
     * 设置菜单按钮
     */
    private void setItem() {
        if (columnUtils == null) {
            columnUtils = new ColumnUtils(getActivity(), new ColumnUtils.Result() {
                @Override
                public void onSuccess(String title, JSONArray jsonArray) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            list_gridDate.add(jsonArray.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    myGridAdapter_home.notifyDataSetChanged();
                }

                @Override
                public void onFailure(String msg) {
                    toastMessage("网络异常");
                }
            });
        }
        columnUtils.getColumn("", "AppButton");
    }

    /**
     * 设置轮播图
     */
    @SuppressWarnings("deprecation")
    private void setRollViewLayout(LayoutInflater inflater) {
        mPager = (RecommentViewPager) findViewById(R.id.recomment_viewpager);
        more_detail = findViewById(R.id.more_detail);
        main_imgUser = findViewById(R.id.main_imgUser);
        main_imgUser.setOnClickListener(this);
        more_detail.setOnClickListener(this);

        item_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jsonObject_video = new JSONObject(list_gridDate.get(i).getString("url_android"));
                    Intent intent;
                    //首先判断属于fragmen还是activity
                    if (jsonObject_video.getString("type").equals("OpenUrlWindow")) {
                        //打开网页相关
                        if (list_gridDate.get(i).getString("title").contains("关于我们")) {
                            if (getAgrement == null) {
                                getAgrement = new GetAgrement(getActivity());
                            }
                            getAgrement.popIsNull_sigle("", "关于我们", "我知道了");
                        } else {
                            intent = new Intent(getActivity(), WebViewUrlActivity.class);
                            intent.putExtra("mark", "activity");
                            intent.putExtra("url", jsonObject_video.getString("url"));
//            intent.putExtra("title",list.get(i).getString("title"));
                            startActivity(intent);
                        }

                    } else {
                        if (jsonObject_video.getString("type").equals("OpenFullVideo")) {
                            //视屏
                            startActivity(new Intent(getActivity(), VideoPlayActivity.class).putExtra("path_video", jsonObject_video.getString("path")));
                        } else {
                            //打开本地相关
                            if (jsonObject_video.getString("name").contains("Fragment")) {
                                //属于首界面fragment相关
                                if (jsonObject_video.getString("name").contains("StorePriceFragment")) {
                                    //门店价格
                                    ((Main_NewActivity) getActivity()).setPage2(jsonObject_video.getString("id"));
                                } else if (jsonObject_video.getString("name").contains("MyOrderFragment")) {
                                    //其余fragment
                                    ((Main_NewActivity) getActivity()).setPage3();
                                } else if (jsonObject_video.getString("name").contains("RechargeCenterFragment")) {
                                    ((Main_NewActivity) getActivity()).setPage4();
                                }
                            } else {
                                //各个activity界面
                                BaseActivity s = null;
                                try {

                                    try {
                                        s = (BaseActivity) Class.forName(jsonObject_video.getString("name")).newInstance();
                                    } catch (java.lang.InstantiationException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                if (s != null && s instanceof BaseActivity) {
                                    intent = new Intent(getActivity(), s.getClass());
                                    startActivity(intent);
                                } else {
                                    toastMessage("没有相关界面");
                                    // intent = new Intent(getActivity(), WebViewUrlActivity.class);
                                }

                            }
                        }
                    }
//                    if (list_gridDate.get(i).getString("title").contains("衣服") || list_gridDate.get(i).getString("title").contains("裤子") || list_gridDate.get(i).getString("title").contains("鞋子")) {
//                        ((Main_NewActivity) getActivity()).setPage2(jsonObject_video.getString("id"));
//                    } else if (list_gridDate.get(i).getString("title").contains("活动展示")) {
//                        startActivity(new Intent(getActivity(), ActivityListActivity.class));
//                    } else if (list_gridDate.get(i).getString("title").contains("我的钱包")) {
//                        ((Main_NewActivity) getActivity()).isLoginOther(MyWalletActivity.class);
//                    } else if (list_gridDate.get(i).getString("title").contains("我的订单")) {
//                        ((Main_NewActivity) getActivity()).isLoginOther(MyOrderActivity.class);
//                    } else if (list_gridDate.get(i).getString("title").contains("关于我们")) {
//                        //关于我们
//                        if (getAgrement == null) {
//                            getAgrement = new GetAgrement(getActivity());
//                        }
////                     getAgrement.getAggrement("single", "我知道了");
//                        getAgrement.popIsNull_sigle("", "关于我们", "我知道了");
//                    } else if (list_gridDate.get(i).getString("title").contains("演示")) {
//
//                        startActivity(new Intent(getActivity(), VideoPlayActivity.class).putExtra("path_video", jsonObject_video.getString("path")));
//                    }
                } catch (Exception e) {
                    Log.d("eee,", e.toString());
                }

            }
        });
        getBanner("app_main_ann");
    }

    /**
     * 设置首页banner
     */
    private void setBanner() {
        if (imgUrl.size() > 0) {
            mAdapter = new RecommendAdvAdapter(LayoutInflater.from(getActivity()), imgUrl);
            mPager.setAdapter(mAdapter);
            mPager.setCurrentItem(1);
            mPager.setOffscreenPageLimit(3);
            mPager.setOnSimpleClickListener(this);
            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {


                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                    if (arg0 == imgUrl.size() - 1 && arg1 == 0.0f) {
                        mPager.setCurrentItem(1, false);

                    } else if (arg0 == 0 && arg1 == 0.0f) {
                        mPager.setCurrentItem(imgUrl.size() - 2, false);

                    }

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            });
            // 设置自动轮播
            handler.postDelayed(runnable, 3000);
            //上下滑动和左右滑动冲突解决
            mPager.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mPager.getGestureDetector().onTouchEvent(event);
                    final float x = event.getRawX();
                    final float y = event.getRawY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            xDistance = yDistance = 0f;
                            mLastMotionX = x;
                            mLastMotionY = y;
                        case MotionEvent.ACTION_MOVE:
                            final float xDiff = Math.abs(x - mLastMotionX);
                            final float yDiff = Math.abs(y - mLastMotionY);
                            xDistance += xDiff;
                            yDistance += yDiff;
                            float dx = xDistance - yDistance;
                            /** 左右滑动避免和下拉刷新冲突 **/
                            if (xDistance > yDistance || Math.abs(xDistance - yDistance) < 0.00001f) {
                                mIsBeingDragged = true;
                                mLastMotionX = x;
                                mLastMotionY = y;
                                ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(true);

                            } else {
                                mIsBeingDragged = false;
                                ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);

                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            if (mIsBeingDragged) {

                                ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }
    /**
     * 设置首页banner
     */
    private void setBanner_evaluate() {
        if (list_evaluate.size() > 0) {
            evaluateAdapter = new RecommendAdv_evaluateAdapter(LayoutInflater.from(getActivity()), list_evaluate,getActivity());
            indicator = findViewById(R.id.indicator);
            indicator.setTotalIndex(list_evaluate.size());
            recomment_viewpager_evaluate.setAdapter(evaluateAdapter);
            recomment_viewpager_evaluate.setCurrentItem(0);
            indicator.setCurrentIndex(0);
            recomment_viewpager_evaluate.setOffscreenPageLimit(3);
            recomment_viewpager_evaluate.setOnSimpleClickListener(new RecommentViewPager.onSimpleClickListener() {
                @Override
                public void setOnSimpleClickListenr(int position) {

                }
            });
            recomment_viewpager_evaluate.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    indicator.setCurrentIndex(position);

                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

//                    if (arg0 == list_evaluate.size() - 1 && arg1 == 0.0f) {
//                        recomment_viewpager_evaluate.setCurrentItem(1, false);
//
//                    } else if (arg0 == 0 && arg1 == 0.0f) {
//                        recomment_viewpager_evaluate.setCurrentItem(list_evaluate.size() - 2, false);
//
//                    }

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            });
            // 设置自动轮播
          //  handler.postDelayed(runnable, 3000);
            //上下滑动和左右滑动冲突解决
            recomment_viewpager_evaluate.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    recomment_viewpager_evaluate.getGestureDetector().onTouchEvent(event);
                    final float x = event.getRawX();
                    final float y = event.getRawY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            xDistance_eva = yDistance_eva = 0f;
                            mLastMotionX_eva = x;
                            mLastMotionY_eva = y;
                        case MotionEvent.ACTION_MOVE:
                            final float xDiff = Math.abs(x - mLastMotionX_eva);
                            final float yDiff = Math.abs(y - mLastMotionY_eva);
                            xDistance_eva += xDiff;
                            yDistance_eva += yDiff;
//                            float dx = xDistance_eva - yDistance_eva;
//                            /** 左右滑动避免和下拉刷新冲突 **/
//                            if (xDistance_eva > yDistance_eva || Math.abs(xDistance_eva - yDistance_eva) < 0.00001f) {
//                                mIsBeingDragged = true;
//                                mLastMotionX_eva = x;
//                                mLastMotionY_eva = y;
//                                ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(true);
//
//                            } else {
//                                mIsBeingDragged = false;
//                                ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);
//
//                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            if (mIsBeingDragged) {

                                ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void setOnSimpleClickListenr(int position) {
        // toastMessage(position + "");
        BaseActivity s = null;
        Intent intent;


        try {
//OpenUrlWindow
            JSONObject jsonObject = new JSONObject(imgUrl.get(position).getString("url_android"));

            if (jsonObject.getString("type").equals("OpenUrlWindow")) {
                intent = new Intent(getActivity(), WebViewUrlActivity.class);
                intent.putExtra("mark", "activity");
                intent.putExtra("url", jsonObject.getString("url"));
//            intent.putExtra("title",list.get(i).getString("title"));
                startActivity(intent);
            } else {
                //打开本地相关界面
                try {
                    try {
                        s = (BaseActivity) Class.forName(jsonObject.getString("name")).newInstance();
                    } catch (java.lang.InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (s != null && s instanceof BaseActivity) {
                    intent = new Intent(getActivity(), s.getClass());
                } else {
                    intent = new Intent(getActivity(), WebViewUrlActivity.class);
                }
                startActivity(intent);
            }

        } catch (Exception e) {
            toastMessage("没有相关数据");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_combo:
                //套餐
                startActivity(new Intent(getActivity(), StoreComboActivity.class));
                break;
            case R.id.main_imgUser:
                ((Main_NewActivity) getActivity()).isLogin2();
                break;
            case R.id.tv_more:
                startActivity(new Intent(getActivity(), StoreComboActivity.class));
                break;
            case R.id.layout_saoma:
                ((Main_NewActivity) getActivity()).saoma();
                break;
            case R.id.img_step:
                //洗涤流程
                Intent intent = new Intent(getActivity(), WebViewUrlActivity.class);
                try {
//OpenUrlWindow
                    JSONObject jsonObject = new JSONObject(jsonObject_step.getString("url_android"));
                    if (jsonObject.getString("type").equals("OpenUrlWindow")) {
                        intent.putExtra("mark", "activity");
                        intent.putExtra("url", jsonObject.getString("url"));
//            intent.putExtra("title",list.get(i).getString("title"));
                        startActivity(intent);
                    } else {
                        //da bendi
                        startActivity(new Intent(getActivity(), StoreComboActivity.class));
                    }

                } catch (Exception e) {
                    toastMessage("没有相关数据");
                }
                break;
            case R.id.more_detail:
                //更多洗涤详情
                Intent intent2 = new Intent(getActivity(), WebViewUrlActivity.class);
                try {
                    intent2.putExtra("mark", "activity");
                    intent2.putExtra("mark", "AboutLiucheng");
                    intent2.putExtra("url", RequestTag.ArticaleUrl + "mobile/Article/single?mark=AboutLiucheng&app=1");
                    intent2.putExtra("title", "洗涤详情");
                    startActivity(intent2);
                } catch (Exception e) {
                    toastMessage("没有相关数据");
                }
                break;
            case R.id.tv_evaluate_more:
                //更多评论
                startActivity(new Intent(getActivity(), EvaluateListActivity.class));
                break;
        }
    }

    /**
     * 获取广告位
     */
    private void getBanner(String type) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("type", type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(getActivity(), RequestTag.BaseUrl + RequestTag.HOME_BANNER, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, final JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                            imgUrl.add(response.getJSONArray("data").getJSONObject(i));
                        }
                        for (int i = response.getJSONArray("data").length() - 1; i >= 0; i--) {
                            imgUrl.add(0, response.getJSONArray("data").getJSONObject(i));
                            break;
                        }
                        for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                            imgUrl.add(response.getJSONArray("data").getJSONObject(i));
                            break;
                        }
                        setBanner();
                    }
                } catch (Exception e) {
                    dismissLoadingView();
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();
            }
        });
    }

    /**
     * 获取套餐
     */
    private void getBanner_combo(String type) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("type", type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(getActivity(), RequestTag.BaseUrl + RequestTag.HOME_BANNER, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, final JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                            Picasso.with(getActivity()).load(RequestTag.BaseImageUrl + response.getJSONArray("data").getJSONObject(i).getString("pic")).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(img_combo);
                        }

                    }
                } catch (Exception e) {
                    dismissLoadingView();
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();
            }
        });
    }

    /**
     * 主界面流程广告
     */
    private void get_stepBanner(String type) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("type", type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(getActivity(), RequestTag.BaseUrl + RequestTag.HOME_BANNER, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, final JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                            jsonObject_step = response.getJSONArray("data").getJSONObject(i);
                            Picasso.with(getActivity()).load(RequestTag.BaseImageUrl + response.getJSONArray("data").getJSONObject(i).getString("pic")).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(img_step);
                        }

                    }
                } catch (Exception e) {
                    dismissLoadingView();
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();
            }
        });
    }

    /**
     * 获取平台公告
     */
    private void getAnnList() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("mark", "ann");
            jsonObject.put("page", 1 + "");
            jsonObject.put("pagesize", 3 + "");
            //jsonObject.put("pagesize",MyApplication.getPageSize()+"");//非必传

            // jsonObject.put("additional",additional+"");//非必传
//            jsonObject.put("lat", MyApplication.getUserJson().getString("m_username"));
//            jsonObject.put("lon", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(getActivity(), RequestTag.BaseUrl + RequestTag.ARTICAL_LIST, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {

                    if (response.getInt("status") != 0) {

                    } else {
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {

                        } else {
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                //   list.add(response.getJSONArray("data").getJSONObject(i));
                                list_marque.add(response.getJSONArray("data").getJSONObject(i));
                            }
                            setNoticeOnclick();
                            setRollViewLayout(LayoutInflater.from(getActivity()));
                        }
                    }

                } catch (Exception e) {


                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查后重试");
            }
        });
    }

    /**
     * 获取套餐列表
     */
    private void getComboList() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("page", 1 + "");
            jsonObject.put("pagesize", 4 + "");//非必传

            // jsonObject.put("additional",additional+"");//非必传
//            jsonObject.put("lat", MyApplication.getUserJson().getString("m_username"));
//            jsonObject.put("lon", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(getActivity(), RequestTag.BaseUrl + RequestTag.READ_COMBO_LIST, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("responsesss", response.toString());

                    if (response.getInt("status") != 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {


                        } else {

                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                list_combo.add(response.getJSONArray("data").getJSONObject(i));
                            }
//                            if(list_combo.size() >= response.getInt("total")){
//                                //已加载完毕
//                                pcf_container.loadMoreComplete(false);
//                            }else{
//                                pcf_container.loadMoreComplete(true);
//                            }


                        }
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }
    /**
     * 所有评论列表
     */
    private void getAllEvaluateList() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("hot","1");
            jsonObject.put("page", "1");
            jsonObject.put("pagesize", MyApplication.getPageSize() + "");
            // jsonObject.put("order_id", order_id);//用户订单ID（不传时hot不能为空）
            // jsonObject.put("id","");//当传入ID时表示取当前这条评价，但不能取子评价（不传时hot不能为空）
            if(MyApplication.getUserJson() != null) {
               // jsonObject.put("uid", MyApplication.getUid());//用户ID（不传时hot不能为空）
                jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
                jsonObject.put("pwd", MyApplication.getPasswprd());
            }else{

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(getActivity(), RequestTag.BaseUrl + RequestTag.LAUNDY_ORDER_DOUBT, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("response", response.toString());

                    if (response.getInt("status") != 0) {

                        toastMessage(response.getString("msg"));
                    } else {
                        if (response.getJSONArray("data") == null) {
                        } else {
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                list_evaluate.add(response.getJSONArray("data").getJSONObject(i));
                            }
                            setBanner_evaluate();
                        }
                    }

                } catch (Exception e) {
                    recomment_viewpager_evaluate.setVisibility(View.GONE);
                }
               // adapter.notifyDataSetChanged();
                dismissLoadingView();

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
            }
        });
    }


}
