package com.mad.trafficclient.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.mad.trafficclient.R;
import com.mad.trafficclient.db.RechargeLogMan;
import com.mad.trafficclient.httppost.HttpRequest;
import com.mad.trafficclient.model.CarInfo_fg1;
import com.mad.trafficclient.model.RechargeLog;
import com.mad.trafficclient.model.UserInfo_fg1;

import org.json.JSONException;
import org.json.JSONObject;

public class Adapter_fg1 extends BaseAdapter {

    private List<CarInfo_fg1.ROWSDETAILBean> objects = new ArrayList<CarInfo_fg1.ROWSDETAILBean>();
    public List<CarInfo_fg1.ROWSDETAILBean> selects = new ArrayList<CarInfo_fg1.ROWSDETAILBean>();
    public int balance[] = new int[4];
    private SharedPreferences sp;
    private Context context;
    private LayoutInflater layoutInflater;

    public Adapter_fg1(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        sp = context.getSharedPreferences("setting", Context.MODE_APPEND);
    }

    public void set(List<CarInfo_fg1.ROWSDETAILBean> objects, int balance[]) {
        this.objects = objects;
        this.balance = balance;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public CarInfo_fg1.ROWSDETAILBean getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.lv_fg1, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews(position, getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(int position, final CarInfo_fg1.ROWSDETAILBean object, ViewHolder holder) {
        holder.tv_number.setText(String.valueOf(object.getNumber()));
        holder.imBrandFg1Lv.setImageResource(context.getResources().getIdentifier(object.getCarbrand(), "drawable", context.getPackageName()));
        int yuzhi = sp.getInt("carYuzhi", -1);
        if ( yuzhi>=0){
            if (balance[position]<yuzhi){
                holder.rootView.setBackgroundColor(Color.YELLOW);
            }else {
                holder.rootView.setBackgroundColor(Color.WHITE);
            }
        }
        holder.ckFg1Lv.setOnCheckedChangeListener(null);
        if (selects.contains(object)) {
            holder.ckFg1Lv.setChecked(true);
        } else {
            holder.ckFg1Lv.setChecked(false);
        }
        holder.ckFg1Lv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selects.add(object);
                } else {
                    selects.remove(object);
                }
            }
        });
        holder.tvCurrentBalanceFg1Lv.setText("余额:" + balance[position] + "元");
        Log.d("tag", "balance[ "+position+"]"+"="+balance[position]);
        holder.btnFg1Lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(object.getPcardid(), object.getNumber(), object.getCarnumber());
            }
        });

    }

    protected class ViewHolder {
        private ImageView imBrandFg1Lv;
        private TextView tvCurrentBalanceFg1Lv,tv_number;
        private CheckBox ckFg1Lv;
        private Button btnFg1Lv;
        private View rootView;

        public ViewHolder(View view) {
            rootView=view;
            tv_number = (TextView) view.findViewById(R.id.tv_number);
            imBrandFg1Lv = (ImageView) view.findViewById(R.id.im_brand_fg1Lv);
            tvCurrentBalanceFg1Lv = (TextView) view.findViewById(R.id.tv_CurrentBalance_fg1Lv);
            ckFg1Lv = (CheckBox) view.findViewById(R.id.ck_fg1Lv);
            btnFg1Lv = (Button) view.findViewById(R.id.btn_fg1Lv);
        }
    }

    private void showDialog(final String pid, final int number, String carNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_fg1, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView tvCarNumberFg1Dia = (TextView) view.findViewById(R.id.tv_carNumber_fg1Dia);
        final EditText ed = (EditText) view.findViewById(R.id.ed_number_fg1Dia);
        tvCarNumberFg1Dia.setText("车牌号:" + carNumber);
        view.findViewById(R.id.btn_s_fg1Dia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ed.getText())) {
                    Toast.makeText(context, "输入为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int recharge = Integer.valueOf(ed.getText().toString());
                GetThenRecharge(pid, number, recharge);
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.btn_c_fg1Dia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    private void GetThenRecharge(final String pid, final int number, final int recharge) {
        final JSONObject js = new JSONObject();
        try {
            js.put("UserName", "user1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.request("get_all_user_info", js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Gson gson = new Gson();
                UserInfo_fg1 beanUs = gson.fromJson(jsonObject.toString(), UserInfo_fg1.class);
                for (UserInfo_fg1.ROWSDETAILBean bean : beanUs.getROWS_DETAIL()) {
                    if (bean.getPcardid().equals(pid)) {
                        recharge(number, bean.getUsername(), recharge);
                        break;
                    }
                }
            }
        }, null);
    }

    private void recharge(final int number, final String userName, final int recharge) {
        JSONObject js = new JSONObject();
        try {
            js.put("UserName", userName);
            js.put("CarId", number);
            js.put("Money", recharge);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.request("set_car_account_recharge", js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int newBalance = balance[number - 1] + recharge;
                balance[number - 1] = newBalance;
                notifyDataSetChanged();
                RechargeLogMan man = new RechargeLogMan(context);
                man.add(new RechargeLog(HttpRequest.getUserName(), userName, recharge, newBalance, System.currentTimeMillis(), number));
                man.db.close();
            }
        }, null);
    }
}
