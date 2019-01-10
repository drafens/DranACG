package com.drafens.dranacg.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.drafens.dranacg.R;
import com.drafens.dranacg.tools.Webdav;

public class FragmentLogin extends Fragment implements View.OnClickListener {
    private EditText editUsername;
    private EditText editPassword;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        editUsername = view.findViewById(R.id.et_username);
        editPassword = view.findViewById(R.id.et_password);
        Button buttonLogin = view.findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        switch (v.getId()){
            case R.id.btn_login:
                if (TextUtils.isEmpty(username)){
                    editUsername.setError("请输入用户名");
                }else if (TextUtils.isEmpty(password)){
                    editPassword.setError("请输入密码");
                }else {
                    Webdav.isConnected(getActivity(), username, password, new Webdav.IsConnectedCallback() {
                        @Override
                        public void connectedSucceed() {
                            Toast.makeText(getActivity(),"登陆成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void connectedFailed(int failedCode) {
                            Toast.makeText(getActivity(),"登陆失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }
}
