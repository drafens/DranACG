package com.drafens.dranacg.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.drafens.dranacg.R;
import com.drafens.dranacg.tools.FavouriteManager;
import com.drafens.dranacg.tools.FileManager;
import com.drafens.dranacg.tools.Webdav;

import java.util.Objects;

public class FragmentLogin extends Fragment implements View.OnClickListener {
    private TextInputEditText editUsername;
    private TextInputEditText editPassword;

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
        Button buttonSync = view.findViewById(R.id.btn_sync);
        buttonLogin.setOnClickListener(this);
        buttonSync.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String username = Objects.requireNonNull(editUsername.getText()).toString();
        final String password = Objects.requireNonNull(editPassword.getText()).toString();
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
                            FileManager.putPreferences("isLogin", true, Objects.requireNonNull(getContext()));
                            FileManager.putPreferences("username", username, Objects.requireNonNull(getContext()));
                            FileManager.putPreferences("password", password, Objects.requireNonNull(getContext()));
                            FavouriteManager.syncFavourite(getContext());
                        }

                        @Override
                        public void connectedFailed(int failedCode) {
                            if (failedCode == 401){
                                Toast.makeText(getActivity(),"账号密码错误",Toast.LENGTH_SHORT).show();
                                FileManager.putPreferences("isLogin", false, Objects.requireNonNull(getContext()));
                            }else {
                                Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_SHORT).show();
                                FileManager.putPreferences("isLogin", false, Objects.requireNonNull(getContext()));
                                Log.d("TAG", "connectedFailed: " + failedCode);
                            }
                        }
                    });
                }
                break;
            case R.id.btn_sync:
                FavouriteManager.syncFavourite(getContext());
                break;
        }
    }
}
