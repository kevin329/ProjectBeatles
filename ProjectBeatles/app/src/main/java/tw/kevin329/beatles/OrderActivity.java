package tw.kevin329.beatles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {
    protected static final int REFRESH_DATA = 0x00000001;
    protected static final int TIMEOUT = 0x00000002;
    Handler mHandler;
    String result, eid, time, date, empty, username, action, json_action = "bus_booking", available, server_url;
    String[] stop;
    ProgressDialog progressDialog;
    JSONArray stop_json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;
        server_url = prefs.getString("server_url", null);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        toolbar.setTitle(getResources().getString(R.string.title_reserve));
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.abc_ic_clear_material );
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderActivity.this.finish();
            }
        });
        Bundle bundle =this.getIntent().getExtras();
        username    = bundle.getString("username");
        action      = bundle.getString("action");
        eid         = bundle.getString("eid");
//        weekday     = bundle.getString("weekday");
        time        = bundle.getString("time");
        date        = bundle.getString("date");
//        note        = bundle.getString("note");
        empty       = bundle.getString("empty");
        available   = bundle.getString("available");
        System.out.println(available);
//        bus_stop_list = bundle.getStringArray("bus_stop_list");
        try {
            stop_json = new JSONArray(bundle.getString("stop"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        stop = new String[stop_json.length()];
        for (int j = 0, count2 = stop_json.length(); j < count2; j++){  //轉換站牌序號到字串
            String temp = "";
            try {
                switch (stop_json.getInt(j)){
                    case 1:
                        temp = getResources().getString(R.string.stop_1_lanyang);
                        break;
                    case 2:
                        temp = getResources().getString(R.string.stop_2_wenquan);
                        break;
                    case 3:
                        temp = getResources().getString(R.string.stop_3_visitor_center);
                        break;
                    case 4:
                        temp = getResources().getString(R.string.stop_4_sanmin);
                        break;
                    case 5:
                        temp = getResources().getString(R.string.stop_5_jiaoxi_station);
                        break;
                    case 6:
                        temp = getResources().getString(R.string.stop_6_xihuhui);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            stop[j] = temp;
        }

        final TextView tv1 = (TextView)findViewById(R.id.textView4);
        final TextView tv2 = (TextView)findViewById(R.id.textView5);
        TextView tvlist[] = new TextView[3];
        tvlist[0] = (TextView)findViewById(R.id.textView6);
        tvlist[1] = (TextView)findViewById(R.id.textView7);
        tvlist[2] = (TextView)findViewById(R.id.textView8);

        int time_hour = Integer.valueOf(time.substring(0, 2));
        String am_pm = getResources().getString(R.string.time_am);
        if (time_hour > 12){
            time_hour-=12;
            am_pm = getResources().getString(R.string.time_pm);
        }
        if (time_hour == 12){
            am_pm = getResources().getString(R.string.time_pm);
        }
        tv1.setText(date);

        if (Locale.getDefault().getLanguage().equals("zh") || Locale.getDefault().getLanguage().equals("ja")) {
            time = time_hour + getResources().getString(R.string.time_hour) + time.substring(3,5) + getResources().getString(R.string.time_minute);
            tv2.setText(am_pm + " " + time);
        } else {
            tv2.setText(time + " " + am_pm);
        }

        for (int i = 0; i< stop.length; i++ ){
            tvlist[i].setText(stop[i]);
        }
        if (stop.length == 2){
            ImageView imageView = (ImageView)findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.pic2);
            tvlist[2].setVisibility(View.GONE);
        } else if (stop.length == 3) {
            ImageView imageView = (ImageView)findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.pic3);
            tvlist[2].setVisibility(View.VISIBLE);
        }

        AppCompatButton button = (AppCompatButton)findViewById(R.id.button);
        if ( (empty.equals("0") || empty.equals("-1")) && action.equals("reserve") ){
            button.setEnabled(false);
            ((TextView)findViewById(R.id.textView9)).setText("※" + getResources().getString(R.string.full));
        } else if (available.equals("0")){
            button.setEnabled(false);
            ((TextView)findViewById(R.id.textView9)).setText(getResources().getString(R.string.msg_unavailable_0));
        } else if (available.equals("2")){
            button.setEnabled(false);
            ((TextView)findViewById(R.id.textView9)).setText(getResources().getString(R.string.msg_unavailable_2));
        } else {
            button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog = ProgressDialog.show(OrderActivity.this, "", getResources().getString(R.string.msg_loading));
                    new Thread(runnable).start();
                }
            });
            ((TextView)findViewById(R.id.textView9)).setVisibility(View.GONE);
        }
        if (action.equals("cancel")) {
            button.setText(getResources().getString(R.string.btn_cancel));
            toolbar.setTitle(getResources().getString(R.string.title_cancel));
            json_action = "bus_cancel";
        }
    }
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            System.out.println("runnable");
            String uriAPI = server_url + "my.php";
            /************************** URLConnection Test *****************************/
            Message msg = new Message();
            try {
                URL url = new URL(uriAPI);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                JSONObject jsonobject = new JSONObject();
                jsonobject.put("action", json_action);
                String data =  "m= " + URLEncoder.encode(jsonobject.toString(), "utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();
                int code = conn.getResponseCode();
                if(code==200){
                    InputStream is = conn.getInputStream();
                    result = convertStreamToString(is);
                    System.out.println("conn_result = " + result);
                    msg.what = REFRESH_DATA;
                }else{
                    System.out.println("connect fail, code = " + code);
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.what = TIMEOUT;
            }
            mHandler.sendMessage(msg);
            /************************** URLConnection Test *****************************/

        }
    };

    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case REFRESH_DATA:
                        System.out.println("mHandler");

                        if (result != null) {
                            JSONObject correct_info;
                            String username_cookie, status = "";
                            try {
                                correct_info = new JSONObject(result);
//                                username_cookie = correct_info.getString("correct");
                                status = correct_info.getString("status");
                                System.out.println("status: " + status);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (status.equals("check")) {
                                if (action.equals("reserve")) {
                                    Toast.makeText(OrderActivity.this, getResources().getString(R.string.msg_reserve_success), Toast.LENGTH_LONG).show();
                                } else if (action.equals("cancel")) {
                                    Toast.makeText(OrderActivity.this, getResources().getString(R.string.msg_cancel_success), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if (action.equals("reserve")) {
                                    Toast.makeText(OrderActivity.this, getResources().getString(R.string.msg_reserve_fail), Toast.LENGTH_LONG).show();
                                } else if (action.equals("cancel")) {
                                    Toast.makeText(OrderActivity.this, getResources().getString(R.string.msg_cancel_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        Intent intent = new Intent();
                        intent.setClass(OrderActivity.this,MainActivity.class);
                        progressDialog.dismiss();
                        startActivity(intent);
                        OrderActivity.this.finish();

                        break;
                    case TIMEOUT:
                        progressDialog.dismiss();
                        android.support.v7.app.AlertDialog.Builder builder2 = new android.support.v7.app.AlertDialog.Builder(OrderActivity.this, R.style.MyAppCompatAlertDialogStyle);
                        builder2.setMessage(getResources().getString(R.string.msg_timeout))
                                .setCancelable(true)
                                .setPositiveButton(android.R.string.ok, null);
                        final android.support.v7.app.AlertDialog alert2 = builder2.create();
                        alert2.show();
                        break;
                }
            }
        };
    }
}
