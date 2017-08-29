package tw.kevin329.beatles;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class LobbyActivity extends AppCompatActivity {
    int check_weekday = 0, check_dist = 0;
    String username;
    ListView listView;
    String[] msubtitles0, msubtitles1, mtitles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Bundle bundle = this.getIntent().getExtras();
        username = bundle.getString("username");

        msubtitles0 = getResources().getStringArray(R.array.week);
        msubtitles1 = getResources().getStringArray(R.array.dist);
        mtitles = getResources().getStringArray(R.array.filter);

        Button button02 = (Button)findViewById(R.id.button2);
        Button button03 = (Button)findViewById(R.id.button3);
        Button button04 = (Button)findViewById(R.id.button4);
        button02.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("action", "reserve");
                bundle.putInt("weekday", check_weekday);
                bundle.putInt("way", check_dist);

                intent.setClass(LobbyActivity.this, ListActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        button03.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);

                intent.setClass(LobbyActivity.this, MyAccountActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        button04.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("action", "cancel");
                bundle.putInt("weekday", 0);
                bundle.putInt("way", 0);

                intent.setClass(LobbyActivity.this, ListActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
//        final AlertDialog mutiItemDialog0 = getMutiItemDialog1(msubtitles0, mtitles[0], 0);
//        final AlertDialog mutiItemDialog1 = getMutiItemDialog1(msubtitles1, mtitles[1], 1);


        listView = (ListView)findViewById(R.id.listView2);
        listView.setAdapter(makeadapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//                        mutiItemDialog0.show();
                        showSingleChoiceDialog(msubtitles0, mtitles[0], 0, check_weekday);
                        break;
                    case 1:
//                        mutiItemDialog1.show();
                        showSingleChoiceDialog(msubtitles1, mtitles[1], 1, check_dist);
                        break;
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent();
            intent.setClass(LobbyActivity.this, Info.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_logout) {
            new Thread(runnable_logout).start();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    Runnable runnable_logout = new Runnable(){
        @Override
        public void run() {
            System.out.println("runnable_logout");

//            HttpPost httpRequest = new HttpPost(uriAPI);
//            List<NameValuePair> params = new ArrayList<>();
//
//            try	{
//                JSONObject m = new JSONObject();
//                m.put("n1", username);
//
//                String json;
//                json = m.toString();
//                params.add(new BasicNameValuePair("m", json));
//                System.out.println("json: " + json);
//
//                httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//                HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
//                if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                    strResult = EntityUtils.toString(httpResponse.getEntity());
//                    System.out.println("strResult: " + strResult);
//                    if (strResult.equals("done")){

                        File dir = getFilesDir();
                        File file = new File(dir, "login");
                        file.delete();
//
//                        Message msg = new Message();
//                        msg.what = DISMISS;
//                        mHandler.sendMessage(msg);
                        Intent intent = new Intent();
                        intent.setClass(LobbyActivity.this,MainActivity.class);
                        startActivity(intent);
                        LobbyActivity.this.finish();
//                    }
//                } else {
//                    System.out.println("HTTP Error");
//                    Toast.makeText(LobbyActivity.this, "Fail", Toast.LENGTH_LONG).show();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    };

    public android.support.v7.app.AlertDialog getMutiItemDialog1(final String[] items, final String title, final int i) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this, R.style.MyAppCompatAlertDialogStyle);
        //設定對話框內的項目
        builder.setTitle(title);
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (i == 0) {
                    check_weekday = which;
                } else {
                    check_dist = which;
                }
                listView.setAdapter(makeadapter());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
    private void showSingleChoiceDialog(final String[] items, final String title, final int i, int check) {
        android.support.v7.app.AlertDialog.Builder builder;
        builder=new AlertDialog.Builder(this);
        builder.setTitle(title);

        builder.setSingleChoiceItems(items, check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (i == 0) {
                    check_weekday = which;
                } else {
                    check_dist = which;
                }
                listView.setAdapter(makeadapter());
                dialogInterface.dismiss();
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
//        return builder.create();
    }
    public SimpleAdapter makeadapter() {

        String[] msubtitles = new String[2];
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        SimpleAdapter adapter;

        msubtitles[0] = msubtitles0[check_weekday];
        msubtitles[1] = msubtitles1[check_dist];

        //把資料加入ArrayList中
        for (int i = 0; i < mtitles.length; i++) {
            HashMap<String, String> item = new HashMap<>();
            item.put("title", mtitles[i]);
            item.put("subtitle", msubtitles[i]);
            list.add(item);
        }

        //新增SimpleAdapter
        adapter = new SimpleAdapter(
                this,
                list,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "subtitle"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        return adapter;
    }

}