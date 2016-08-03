package com.example.lsx.trainingmultipthread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView mImageView;
    private Button mLoadButton;
    private Button mToastButton;
    private ProgressBar mProgressBar;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mProgressBar.setProgress((Integer) msg.obj);
                    break;
                case 2:
                    mImageView.setImageBitmap((Bitmap) msg.obj);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.activity_main_image_view);
        mLoadButton = (Button) findViewById(R.id.activity_main_load_button);
        mToastButton = (Button) findViewById(R.id.activity_main_toast_button);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_main_progress_bar);

        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg1 = handler.obtainMessage();//设置可示
                        msg1.what = 0;
                        handler.sendMessage(msg1);
                        for (int i = 1; i < 11 ; i++) {
                            sleep();
                            Message msg2 = handler.obtainMessage();//显示进度
                            msg2.what = 1;
                            msg2.obj = i * 10;
                            handler.sendMessage(msg2);
                        }
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_launcher);
                        Message msg3 = handler.obtainMessage();
                        msg3.what = 2;
                        msg3.obj = bitmap;
                        handler.sendMessage(msg3);
                    }

                    private void sleep() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        mToastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "多线程", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImage() {

        new LoadImageTask().execute();
    }


    class LoadImageTask extends AsyncTask<Void, Integer, Bitmap> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Log.d(TAG, "new Thread:" + Thread.currentThread().getName());
            for (int i = 1; i < 11; i++) {
                sleep();
                publishProgress(i * 10);
            }


            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            return bitmap;

        }

        private void sleep() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setProgress(values[0]);
        }

        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "onPostExecute: " + Thread.currentThread().getName());
            mImageView.setImageBitmap(bitmap);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
