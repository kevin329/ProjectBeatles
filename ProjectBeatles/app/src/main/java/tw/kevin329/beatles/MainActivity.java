package tw.kevin329.beatles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            FileInputStream inputStream = this.openFileInput("login");
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            int length;
//            PreferenceManager.setDefaultValues(this, R.xml.about, true);

            while( (length = inputStream.read(bytes)) != -1){
                arrayOutputStream.write(bytes, 0, length);
            }


//            while (inputStream.read(bytes) != -1) {
//                arrayOutputStream.write(bytes, 0, bytes.length);
//            }
            inputStream.close();
            arrayOutputStream.close();
            String content;
            content = new String(arrayOutputStream.toByteArray());

            System.out.println(content);
            if (!content.equals("correct")) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LobbyActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("username", content);
                intent.putExtras(bundle);

                startActivity(intent);
                MainActivity.this.finish();
            } else {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }

        } catch (FileNotFoundException e) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
            e.printStackTrace();
        }
        catch (IOException e){
//            return ;
        }
    }
}
