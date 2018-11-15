package com.um.handlerdemo;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private static final int MSG_UPDATE = 1;
    @BindView(R.id.btn_load)
    Button mBtnLoad;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_load_2)
    Button mBtnLoad2;
    private MyTask myTask;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE:
                    updateUI(msg.arg1);
                break;

            }

        }
    };

    private void updateUI(int progress) {
        if (progress >= 100){
            mProgressBar.setProgress(100);
            mTvProgress.setText("加载完成");
        }else {
            mProgressBar.setProgress(progress);
            mTvProgress.setText("loading ..." + progress + "%");
        }
    }

    private class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: ");
            super.onPreExecute();
            mTvProgress.setText(R.string.loading);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: ");
            int count = 0;
            while (count <= 100) {
                count++;
                publishProgress(count);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "onProgressUpdate: ");
            super.onProgressUpdate(values);
            updateUI(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "onPostExecute: ");
            super.onPostExecute(s);
            mTvProgress.setText("加载完成");
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "onCancelled: ");
            super.onCancelled();
            mTvProgress.setText("已取消");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_load, R.id.btn_cancel,R.id.btn_load_2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
                myTask = new MyTask();//AsyncTask方式
                myTask.execute();
                break;
            case R.id.btn_load_2: //Handler方式
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        int count = 0;
                        while (count <= 100) {
                            count++;
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_UPDATE;
                            message.arg1 = count;
                            mHandler.sendMessage(message);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();

                break;
            case R.id.btn_cancel:
                myTask.cancel(true);
                break;
        }
    }
}
