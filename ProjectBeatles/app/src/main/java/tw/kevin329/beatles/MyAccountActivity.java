package tw.kevin329.beatles;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyAccountActivity extends AppCompatActivity {
    private int progressStatus1 = 0, progressStatus2 = 0, progressStatus3 = 0;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.my_account));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_clear_material );
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAccountActivity.this.finish();
            }
        });

        final ProgressBar pb1 = (ProgressBar) findViewById(R.id.progressBar1);
        final ProgressBar pb2 = (ProgressBar) findViewById(R.id.progressBar2);
        final ProgressBar pb3 = (ProgressBar) findViewById(R.id.progressBar3);
//        pb.getProgressDrawable().setColorFilter(R.color.accent, PorterDuff.Mode.SRC_IN);
        pb1.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        pb2.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        pb3.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
//        pb.setProgress(60);
        final int progress1 = 90;
        final int progress2 = 80;
        final int progress3 = 100;

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus1 < progress1) {
                    //progressStatus = doWork();
                    progressStatus1 +=1;
                    //Try to sleep the thread for 20 milliseconds
                    try{
                        Thread.sleep(5);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    //Update the progress bar
                    handler.post(new Runnable() {
                        public void run() {
                            pb1.setProgress(progressStatus1);
                        }
                    });
                }
                while (progressStatus2 < progress2) {
                    //progressStatus = doWork();
                    progressStatus2 +=1;
                    //Try to sleep the thread for 20 milliseconds
                    try{
                        Thread.sleep(5);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    //Update the progress bar
                    handler.post(new Runnable() {
                        public void run() {
                            pb2.setProgress(progressStatus2);
                        }
                    });
                }
                while (progressStatus3 < progress3) {
                    //progressStatus = doWork();
                    progressStatus3 +=1;
                    //Try to sleep the thread for 20 milliseconds
                    try{
                        Thread.sleep(5);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    //Update the progress bar
                    handler.post(new Runnable() {
                        public void run() {
                            pb3.setProgress(progressStatus3);
                        }
                    });
                }
            }
        }).start();
//        new Thread(new Runnable() {
//            public void run() {
//                while (progressStatus < progress3) {
//                    //progressStatus = doWork();
//                    progressStatus +=1;
//                    //Try to sleep the thread for 20 milliseconds
//                    try{
//                        Thread.sleep(20);
//                    }catch(InterruptedException e){
//                        e.printStackTrace();
//                    }
//                    //Update the progress bar
//                    handler.post(new Runnable() {
//                        public void run() {
//                            pb3.setProgress(progressStatus);
//                        }
//                    });
//                }
//            }
//        }).start();
        ((TextView) findViewById(R.id.textView10)).setText("學期：\n(18/20)" + String.valueOf(progress1) + "%");
        ((TextView) findViewById(R.id.textView11)).setText("本月：\n(4/5)" + String.valueOf(progress2) + "%");
        ((TextView) findViewById(R.id.textView12)).setText("本週：\n(2/2)" + String.valueOf(progress3) + "%");

        ((TextView) findViewById(R.id.textView13)).setText(getResources().getString(R.string.status) + ":"+ getResources().getString(R.string.activated));

    }
}
