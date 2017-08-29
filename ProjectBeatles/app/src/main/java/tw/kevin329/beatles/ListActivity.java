package tw.kevin329.beatles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ListActivity extends AppCompatActivity {
    protected static final int REFRESH_DATA = 0x00000001;
    protected static final int TIMEOUT = 0x00000002;
    protected static final int ALERT_DIALOG = 0x00000004;
    String[][] bus_stop_list;
    JSONArray b0, b1, b2, b3, b4, b5, b6, b7;
    ArrayList<HashMap<String,String>> test_list = new ArrayList<>();
    ArrayList<Integer> integerArrayList = new ArrayList<>();
    String  result, json_action, username, action, server_url;
    Handler mHandler;
    int check_weekday, check_dist = 0;
    SwipeRefreshLayout mSwipeRefreshLayout, mSwipeRefreshLayout2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;
        server_url = prefs.getString("server_url", null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        /*
         ********************下拉更新********************/
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout_list);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(runnable).start();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);
        TypedValue typed_value = new TypedValue();
        this.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
        mSwipeRefreshLayout.setRefreshing(true);

        mSwipeRefreshLayout2 = (SwipeRefreshLayout) findViewById(R.id.refresh_layout_empty);
        mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(runnable).start();
            }
        });
        mSwipeRefreshLayout2.setColorSchemeResources(R.color.accent);
        TypedValue typed_value_2 = new TypedValue();
        this.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value_2, true);
        mSwipeRefreshLayout2.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
        mSwipeRefreshLayout2.setRefreshing(true);
        /*
         ********************下拉更新********************/
        Bundle bundle = this.getIntent().getExtras();
        username        = bundle.getString("username");
        check_weekday   = bundle.getInt("weekday");
        check_dist      = bundle.getInt("way");
        action          = bundle.getString("action");
        assert action != null;
        if (action.equals("reserve")){
            toolbar.setTitle(getResources().getString(R.string.title_timetable));
            json_action = "ask_timetable_p";
        } else {
            toolbar.setTitle(getResources().getString(R.string.title_my_reservation));
            json_action = "ask_timetable_my";
        }
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_clear_material );
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListActivity.this.finish();
            }
        });

        new Thread(runnable).start();
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
    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            System.out.println("runnable");
            if (isConnected()) {
                String uriAPI = server_url + "my.php";

//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                /*
                 ************************** URLConnection Test *****************************/
                Message msg = new Message();
                try {
                    URL url = new URL(uriAPI);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    JSONObject jsonobject = new JSONObject();
                    jsonobject.put("action", json_action);
                    jsonobject.put("username", username);
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
                /*
                 ************************** URLConnection Test *****************************/

            }else{
                Message msg2 = new Message();
                msg2.what = ALERT_DIALOG;
                mHandler.sendMessage(msg2);
                //告訴使用者網路無法使用
            }
        }
    };
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case REFRESH_DATA:
                        System.out.println("mHandler");
                        test_list.clear();
                        if (result != null) {
                            try {
                                JSONObject bus0 = new JSONObject(result);

                                b0 = bus0.getJSONArray("bus_id");
                                b1 = bus0.getJSONArray("bus_stop");
                                b2 = bus0.getJSONArray("bus_time");
                                b3 = bus0.getJSONArray("bus_date");
                                b4 = bus0.getJSONArray("bus_quota");
                                b5 = bus0.getJSONArray("bus_empty");
                                b6 = bus0.getJSONArray("bus_available");
                                b7 = bus0.getJSONArray("bus_company");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (b0.length() == 0) {
                                TextView empty_info = (TextView) findViewById(R.id.textView_list_info);
                                String info_text;
                                mSwipeRefreshLayout.setVisibility(View.GONE);
                                mSwipeRefreshLayout2.setVisibility(View.VISIBLE);
                                mSwipeRefreshLayout.setRefreshing(false);
                                mSwipeRefreshLayout2.setRefreshing(false);
                                if (action.equals("reserve")){
                                    info_text = "查無班車資訊或本週時刻表尚未公布";
                                } else {
                                    info_text = "尚未預約交通車";
                                }
                                empty_info.setText(info_text);
                                break;
                            } else {


                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                mSwipeRefreshLayout2.setVisibility(View.GONE);
                            }
                            bus_stop_list = new String[b0.length()][0];
                            int bus_list_count = 0;
                            String date_category = "";
                            for (int i = 0, count = b0.length(); i < count; i++) {  //班車資料一筆一筆分別處理

                                HashMap<String,String> item = new HashMap<>();
                                int weekday;
                                try {
                                    JSONArray bus_stop_array = b1.getJSONArray(i);

                                    int bus_dest = bus_stop_array.getInt(bus_stop_array.length() - 1);
                                    if (bus_dest != 1){ //調整方向
                                        bus_dest = 2;
                                    }

                                    SimpleDateFormat dateStringFormat = new SimpleDateFormat( "yyyy/MM/dd" );//轉換日期取得星期
                                    Date date = dateStringFormat.parse( b3.getString(i) );
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    weekday = calendar.get(Calendar.DAY_OF_WEEK) - 1;

                                    if(weekday == check_weekday || check_weekday == 0) {    //過濾星期
                                        if (check_dist == bus_dest || check_dist == 0) {    //過濾目的地(0:全部,1:蘭陽,2:礁溪)

                                            String bus_stop_title = "";
                                            JSONArray bus_stop_json = b1.getJSONArray(i);
                                            bus_stop_list[bus_list_count]= new String[bus_stop_json.length()];
                                            for (int j = 0, count2 = bus_stop_json.length(); j < count2; j++){  //轉換站牌序號到字串
                                                String temp = "";
                                                switch (bus_stop_json.getInt(j)){
                                                    case 1:
                                                        temp = getResources().getString(R.string.stop_s_1_lanyang);
                                                        break;
                                                    case 2:
                                                        temp = getResources().getString(R.string.stop_s_2_wenquan);
                                                        break;
                                                    case 3:
                                                        temp = getResources().getString(R.string.stop_s_3_visitor_center);
                                                        break;
                                                    case 4:
                                                        temp = getResources().getString(R.string.stop_s_4_sanmin);
                                                        break;
                                                    case 5:
                                                        temp = getResources().getString(R.string.stop_s_5_jiaoxi_station);
                                                        break;
                                                    case 6:
                                                        temp = getResources().getString(R.string.stop_6_xihuhui);
                                                        break;
                                                }
                                                bus_stop_list[bus_list_count][j] = temp;
                                                bus_stop_title = bus_stop_title + ">" + temp;
                                            }
                                            bus_stop_title = bus_stop_title.substring(1, bus_stop_title.length());
                                            String temp_date;
                                            temp_date = makedate(b3.getString(i), weekday);
                                            item.put("date"     , temp_date);
                                            if (!date_category.equals(b3.getString(i))){    //製作日期Category
                                                integerArrayList.add(test_list.size());
//                                                System.out.println("Arraylist.add " + i + b3.getString(i));
                                                date_category = b3.getString(i);
                                                test_list.add(item);
                                            }

                                            item.put("eid"      , b0.getString(i));
                                            item.put("title"    , bus_stop_title);
                                            item.put("time"     , b2.getString(i));
                                            item.put("stop"     , b1.getString(i));
                                            item.put("empty"    , b5.getString(i));
                                            item.put("available", b6.getString(i));
                                            item.put("company", b7.getString(i));
//                                            item.put("category" , String.valueOf(integerArrayList.size() ));
//                                            System.out.print("category:" + String.valueOf(integerArrayList.size()));

                                            test_list.add(item);

                                            ListView lv= (ListView)findViewById(R.id.listView);
//                                            SimpleAdapter adapter;
//                                            adapter = new SimpleAdapter(ListActivity.this, test_list, R.layout.bus_info, new String[] { "title","time", "quota", "date","note" }, new int[] { R.id.title, R.id.time, R.id.quota } );
                                            Adapter adapter1;
                                            adapter1 = new Adapter(ListActivity.this, test_list, R.layout.bus_info, new String[] { "title","time", "quota", "date","note" }, new int[] { R.id.title, R.id.time, R.id.quota } );

                                            lv.setAdapter(adapter1);
                                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                    Intent intent = new Intent();
                                                    intent.setClass(ListActivity.this, OrderActivity.class);
                                                    HashMap hm = (HashMap) test_list.get((int) id);
                                                    Bundle bundle = new Bundle();

                                                    bundle.putString("eid"      , (String) hm.get("eid"));
                                                    bundle.putString("weekday"  , (String) hm.get("weekday"));
                                                    bundle.putString("date"     , (String) hm.get("date"));
                                                    bundle.putString("time"     , (String) hm.get("time"));
                                                    bundle.putString("empty"    , (String) hm.get("empty"));
                                                    bundle.putString("action"   , action);
                                                    bundle.putString("username" , username);
//                                                    bundle.putStringArray("bus_stop_list", bus_stop_list[position - Integer.valueOf((String) hm.get("category"))]);
                                                    bundle.putString("stop"     , (String) hm.get("stop"));
                                                    bundle.putString("available", (String) hm.get("available"));

                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                }
                                            });
                                            bus_list_count++;
                                        }
                                    }
                                } catch (JSONException | ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            mSwipeRefreshLayout2.setRefreshing(false);
                        }
                        break;
                    case ALERT_DIALOG:
                        mSwipeRefreshLayout.setRefreshing(false);
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ListActivity.this, R.style.MyAppCompatAlertDialogStyle);
                        builder.setTitle(getResources().getString(R.string.msg_disconnect_title))
                                .setMessage(getResources().getString(R.string.msg_disconnect))
                                .setCancelable(true)
                                .setPositiveButton(android.R.string.ok, null);
                        final android.support.v7.app.AlertDialog alert = builder.create();
                        alert.show();
                        break;
                    case TIMEOUT:
                        mSwipeRefreshLayout.setRefreshing(false);
                        android.support.v7.app.AlertDialog.Builder builder2 = new android.support.v7.app.AlertDialog.Builder(ListActivity.this, R.style.MyAppCompatAlertDialogStyle);
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
    private class Adapter extends SimpleAdapter{
        Adapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
        @Override
        public boolean isEnabled(int position) {
            if(integerArrayList.contains(position)){
                return false;
            }
            return super.isEnabled(position);
        }
        @Override
        public View getView (int position, View convertView, ViewGroup parent) {
            View view;
            HashMap<String, String> hashMap = new HashMap<>((Map<? extends String, ? extends String>) getItem(position));
            if( integerArrayList.contains(position) ){
                view = LayoutInflater.from(ListActivity.this).inflate(R.layout.group_list_item_tag, null);
                TextView textView0 = view.findViewById(R.id.group_list_item_text);
                textView0.setText(hashMap.get("date"));
            }else{
                view = LayoutInflater.from(ListActivity.this).inflate(R.layout.bus_info, null);
                TextView textView1 = view.findViewById(R.id.title);
                TextView textView2 = view.findViewById(R.id.quota);
                TextView textView3 = view.findViewById(R.id.time);
                textView1.setText(hashMap.get("title"));

                textView3.setText(hashMap.get("time"));
                if (hashMap.get("company").equals("0")){
                    textView3.setBackgroundColor(0xff0000ff);
                    textView3.setTextColor(0xffffffff);
                } else
                if (hashMap.get("company").equals("1")) {
                    textView3.setBackgroundColor(0xffcbe103);
                }
                if (hashMap.get("empty").equals("0")){
                    textView2.setText(getResources().getString(R.string.full));
                    textView2.setTextColor(0xffff0000);

                } else if (hashMap.get("empty").equals("-1")){
//                    textView2.setText(getResources().getString(R.string.full));/*****/
                    textView2.setText("免預約");//*****/
//                    textView2.setTextColor(0xffff0000);

                }else {
                    textView2.setText(hashMap.get("empty") + getResources().getString(R.string.empty));
//                    textView2.setTextColor(0xffff0000);
                }

            }
            return view;
        }
    }
    private  String makedate (String date_o, int weekday_int){
        String weekday ="";
        switch (weekday_int) {
            case 1:
                weekday = getResources().getString(R.string.week_s_1);
                break;
            case 2:
                weekday = getResources().getString(R.string.week_s_2);
                break;
            case 3:
                weekday = getResources().getString(R.string.week_s_3);
                break;
            case 4:
                weekday = getResources().getString(R.string.week_s_4);
                break;
            case 5:
                weekday = getResources().getString(R.string.week_s_5);
                break;
            case 6:
                weekday = getResources().getString(R.string.week_s_6);
                break;
            case 0:
                weekday = getResources().getString(R.string.week_s_7);
                break;
        }
        if (Locale.getDefault().getLanguage().equals("zh") || Locale.getDefault().getLanguage().equals("ja")) {
            return Integer.valueOf(date_o.substring(0, 4))-1911 + getResources().getString(R.string.time_year) +
                    date_o.substring(5, 7) + getResources().getString(R.string.time_month) +
                    date_o.substring(8, 10) + getResources().getString(R.string.time_day) +
                    "(" + weekday + ")";
        }else {
            return Integer.valueOf(date_o.substring(0, 4))-1911 + "-" + date_o.substring(5, 7) + "-" + date_o.substring(8, 10) + "(" + weekday + ")";
        }
    }
}
