package com.mad.trafficclient.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.mad.trafficclient.R;
import com.mad.trafficclient.httppost.HttpRequest;
import com.mad.trafficclient.model.XActionInfo;
import com.mad.trafficclient.model.XCarInfo;
import com.mad.trafficclient.model.XPecInfo;
import com.mad.trafficclient.model.XPecType;
import com.mad.trafficclient.model.XUserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fragment_7 extends Fragment {
    private FragmentPagerAdapter pagerAdapter;
    private List<Fragment> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_7, container, false);
    }

    private ViewPager pager;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = (ViewPager) view.findViewById(R.id.pager);
        initValues();
        data = new ArrayList<Fragment>();
        data.add(new PecFragment());
        pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return data.get(position);
            }

            @Override
            public int getCount() {
                return data.size();
            }
        };
        pager.setAdapter(pagerAdapter);
    }

    final int TAG1 = 1;
    final int TAG2 = 2;
    final int TAG3 = 3;
    final int TAG5 = 5;
    final int TAG9 = 9;
    final int TAG10 = 10;

    private Gson gson;
    private static List<XPecInfo> pecInfoList;
    private List<XPecType> pecTypeList;
    private List<XUserInfo> userInfoList;
    private List<XCarInfo> carInfoList;

    private static List<XUserInfo> userHasPecList;
    private static List<XUserInfo> userNotHasPecList;

    private static int hasRepeatNum;
    private static int notHasRepeatNum;

    private static List<Integer> repeatNumList;

    private static Handler pecHandler;
    private static Handler repeatHandler;

    private static List<XCarInfo> hasPecList;//包含黑牌小车的所有违章车辆
    private static List<XCarInfo> notHasPecList;//包含黑牌小车的所有违章车辆

    private static List<XPecInfo> notRepeatPecList;


    public void initValues() {
        gson = new Gson();
        pecTypeList = new ArrayList<XPecType>();
        pecInfoList = new ArrayList<XPecInfo>();
        userInfoList = new ArrayList<XUserInfo>();
        carInfoList = new ArrayList<XCarInfo>();
        userHasPecList = new ArrayList<XUserInfo>();
        userNotHasPecList = new ArrayList<XUserInfo>();
        hasRepeatNum = 0;
        notHasRepeatNum = 0;
        repeatNumList = new ArrayList<Integer>();
        hasPecList = new ArrayList<XCarInfo>();
        notHasPecList = new ArrayList<XCarInfo>();
        notRepeatPecList = new ArrayList<XPecInfo>();
        handler.sendEmptyMessage(TAG1);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TAG1:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getPecInfoList();
                        }
                    }).start();
                    break;
                case TAG2:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getPecTypeList();
                        }
                    }).start();
                    break;
                case TAG3:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getCarInfoList();
                        }
                    }).start();
                    break;
                case TAG5:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getHasPecList();
                        }
                    }).start();
                    break;
                case TAG9:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getHasBlackRepeatList();
                        }
                    }).start();
                    break;
                case TAG10:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getHasBlackPecList();
                        }
                    }).start();
                    break;

            }
        }
    };

    public void getPecInfoList() {
        try {
            HttpRequest.request("get_all_car_peccancy", new JSONObject().put("UserName", "user1"), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (TextUtils.isEmpty(jsonObject.toString()))
                        return;
                    try {
                        String ROWS_DETAIL = jsonObject.getString("ROWS_DETAIL");
                        JSONArray array = new JSONArray(ROWS_DETAIL);
                        for (int i = 0; i < array.length(); i++) {
                            pecInfoList.add(gson.fromJson(array.getJSONObject(i).toString(), XPecInfo.class));
                        }
                        Log.e("pecInfoList", pecInfoList.size() + "");
                        handler.sendEmptyMessage(TAG2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPecTypeList() {
        try {
            HttpRequest.request("get_peccancy_type", new JSONObject().put("UserName", "user1"), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (TextUtils.isEmpty(jsonObject.toString()))
                        return;
                    try {
                        String ROWS_DETAIL = jsonObject.getString("ROWS_DETAIL");
                        JSONArray array = new JSONArray(ROWS_DETAIL);
                        for (int i = 0; i < array.length(); i++) {
                            pecTypeList.add(gson.fromJson(array.getJSONObject(i).toString(), XPecType.class));
                        }
                        Log.e("pecTypeList", pecTypeList.size() + "");
                        handler.sendEmptyMessage(TAG3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getCarInfoList() {
        try {
            HttpRequest.request("get_car_info", new JSONObject().put("UserName", "user1"), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (TextUtils.isEmpty(jsonObject.toString()))
                        return;
                    try {
                        String ROWS_DETAIL = jsonObject.getString("ROWS_DETAIL");
                        JSONArray array = new JSONArray(ROWS_DETAIL);
                        for (int i = 0; i < array.length(); i++) {
                            carInfoList.add(gson.fromJson(array.getJSONObject(i).toString(), XCarInfo.class));
                        }
                        Log.e("carInfoList", carInfoList.size() + "");
                        handler.sendEmptyMessage(TAG5);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendHandler(Handler handler) {
        if (handler != null)
            handler.sendEmptyMessage(1);
    }

    public void getHasPecList() {
        XUserInfo userInfo;
        XCarInfo carInfo;
        int count;
        for (int i = 0; i < userInfoList.size(); i++) {
            userInfo = userInfoList.get(i);
            count = 0;
            for (int j = 0; j < carInfoList.size(); j++) {
                carInfo = carInfoList.get(j);
                if (TextUtils.equals(userInfo.getPcardid(), carInfo.getPcardid())) {
                    for (int k = 0; k < pecInfoList.size(); k++) {
                        if (TextUtils.equals(carInfo.getCarnumber(), pecInfoList.get(k).getCarnumber())) {
                            count++;
                        }
                    }
                }
            }
            if (count == 0) {
                userNotHasPecList.add(userInfo);
            } else {
                userHasPecList.add(userInfo);
            }
        }

        Log.e("userHasPecList", userHasPecList.size() + "");
        Log.e("userNotHasPecList", userNotHasPecList.size() + "");

        handler.sendEmptyMessage(TAG9);
    }

    public void getHasBlackRepeatList() {
        XPecInfo pecInfo;
        boolean isExists;
        for (int i = 0; i < pecInfoList.size(); i++) {
            pecInfo = pecInfoList.get(i);
            isExists = false;
            for (int j = 0; j < notRepeatPecList.size(); j++) {
                if (TextUtils.equals(pecInfo.getCarnumber(), notRepeatPecList.get(j).getCarnumber())) {
                    isExists = true;
                    break;
                }
            }
            if (isExists)
                continue;
            notRepeatPecList.add(pecInfo);
        }
        Log.e("notRepeatPecList", notRepeatPecList.size() + "\t\t" + notRepeatPecList.toString());

        int count;
        int pec12 = 0, pec35 = 0, pec6 = 0, hasRepeatPec = 0, notHasRepeatPec = 0;
        for (int i = 0; i < notRepeatPecList.size(); i++) {
            pecInfo = notRepeatPecList.get(i);
            count = 0;
            for (int j = 0; j < pecInfoList.size(); j++) {
                if (TextUtils.equals(pecInfo.getCarnumber(), pecInfoList.get(j).getCarnumber())) {
                    count++;
                }
            }
            if (count >= 1 && count <= 2) {
                pec12++;
            } else if (count >= 3 && count <= 5) {
                pec35++;
            } else if (count >= 6) {
                pec6++;
            }

            if (count == 1) {
                notHasRepeatPec++;
            } else if (count > 1) {
                hasRepeatPec++;
            }
        }
        repeatNumList.add(pec12);
        repeatNumList.add(pec35);
        repeatNumList.add(pec6);

        hasRepeatNum = hasRepeatPec;
        notHasRepeatNum = notHasRepeatPec;

        Log.e("repeatNumList", repeatNumList.size() + "\t\t" + repeatNumList.toString());
        Log.e("hasRepeatNum", hasRepeatNum + "");
        Log.e("notHasRepeatNum", notHasRepeatNum + "");

        handler.sendEmptyMessage(TAG10);
        sendHandler(repeatHandler);
    }

    public void getHasBlackPecList() {
        XCarInfo info;
        boolean isExists;
        for (int i = 0; i < carInfoList.size(); i++) {
            info = carInfoList.get(i);
            isExists = false;
            for (int j = 0; j < pecInfoList.size(); j++) {
                if (TextUtils.equals(info.getCarnumber(), pecInfoList.get(j).getCarnumber())) {
                    isExists = true;
                    hasPecList.add(info);
                    break;
                }
            }
            if (!isExists)
                notHasPecList.add(info);
        }
        sendHandler(pecHandler);
    }

    public static DecimalFormat format = new DecimalFormat("###,###,###,##0.00");

    public static class PecFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_7_pec, container, false);
        }

        private PieChart pieChart;
        private String[] titles = {"有违章车辆", "无违章车辆"};
        private List<Integer> list;

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            pieChart = (PieChart) view.findViewById(R.id.pieChart);

            pieChart.setDrawHoleEnabled(false);
            pieChart.setUsePercentValues(true);
            pieChart.setDescription("");

            Legend legend = pieChart.getLegend();
            legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
            legend.setTextSize(15);
            list = new ArrayList<Integer>();
            list.add(notRepeatPecList.size());
            list.add(notHasPecList.size());
            setData(list);
            pecHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    list.clear();
                    list.add(notRepeatPecList.size());
                    list.add(notHasPecList.size());
                    setData(list);
                }
            };
        }

        public void setData(List<Integer> list) {
            List<String> xVals = new ArrayList<String>();
            List<Entry> yVals = new ArrayList<Entry>();
            for (int i = 0; i < list.size(); i++) {
                xVals.add(titles[i]);
                yVals.add(new Entry(list.get(i), i));
            }
            PieDataSet dataSet = new PieDataSet(yVals, "有无违章车辆占比分布图");
            dataSet.addColor(ColorTemplate.VORDIPLOM_COLORS[0]);
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                    return format.format(v) + "%" + "\t" + (int) entry.getVal() + "辆";
                }
            });
            PieData data = new PieData(xVals, dataSet);

            pieChart.setData(data);
            pieChart.invalidate();
        }
    }


}
