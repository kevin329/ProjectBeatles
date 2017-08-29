package tw.kevin329.beatles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    protected static final int REFRESH_DATA = 0x00000001;
    protected static final int UPDATE_NO_RESPOND = 0x00000004;
    String result, server_url;
    Handler mHandler;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PreferenceManager.setDefaultValues(this, R.xml.about, true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;
        server_url = prefs.getString("server_url", null);

        Button button01 = (Button)findViewById(R.id.button);
        button01.setText(getResources().getString(R.string.login));
        button01.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                progressDialog = ProgressDialog.show(LoginActivity.this, "", "登入中...");
                new Thread(runnable).start();
            }
        });
        EditText editText= (EditText) findViewById(R.id.editText2);
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    progressDialog = ProgressDialog.show(LoginActivity.this, "", "登入中...");
                    new Thread(runnable).start();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
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
            EditText tv1 = (EditText)findViewById(R.id.editText);
            EditText tv2 = (EditText)findViewById(R.id.editText2);
            String username = tv1.getText().toString();
            String password = tv2.getText().toString();
            /************************** URLConnection Test *****************************/
            Message msg = new Message();
            try {
                URL url = new URL(uriAPI);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                JSONObject jsonobject = new JSONObject();
                jsonobject.put("action", "login");
                jsonobject.put("username", username);
                jsonobject.put("password", password);
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
                msg.what = UPDATE_NO_RESPOND;
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
                        if (msg.obj instanceof String)
                            result = (String) msg.obj;
                        if (result != null) {
                            JSONObject correctinfo;
                            String usernamecookie = null;
                            try {
                                correctinfo = new JSONObject(result);
                                usernamecookie = correctinfo.getString("correct");
                                System.out.println("usernamecookie" + usernamecookie);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                break;
                            }

                            FileOutputStream outputStream;
                            try {
                                outputStream = openFileOutput("login", Activity.MODE_PRIVATE);
                                assert usernamecookie != null;
                                outputStream.write(usernamecookie.getBytes());
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this,MainActivity.class);
                        progressDialog.dismiss();
                        startActivity(intent);
                        LoginActivity.this.finish();
                        break;
                    case UPDATE_NO_RESPOND:
                        System.out.println("mHandler4");
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.MyAppCompatAlertDialogStyle);
                        builder.setMessage("伺服器無回應")
                                .setCancelable(true)
                                .setPositiveButton(android.R.string.ok, null);
                        final AlertDialog alert = builder.create();
                        alert.show();
                        break;
                }
            }
        };
    }
}
