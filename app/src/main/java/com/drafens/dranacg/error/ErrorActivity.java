package com.drafens.dranacg.error;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drafens.dranacg.R;

public class ErrorActivity extends AppCompatActivity implements View.OnClickListener{
    public final static int MyNetworkException = 0;
    public final static int MyJsoupResolveException = 1;

    private int detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        Intent intent = getIntent();
        detail = intent.getIntExtra("error_code",-1);
        initView();
    }

    private void initView() {
        Button bt_reconnect = findViewById(R.id.bt_reconnect);
        TextView textView = findViewById(R.id.text_view);
        switch (detail){
            case MyNetworkException:
                textView.setText("网络错误");
                break;
            case MyJsoupResolveException:
                textView.setText("解析错误");
                break;
        }
        bt_reconnect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_reconnect:
                finish();
                break;
        }
    }
}
