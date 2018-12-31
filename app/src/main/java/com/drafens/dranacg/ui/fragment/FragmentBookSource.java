package com.drafens.dranacg.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.drafens.dranacg.R;
import com.drafens.dranacg.Sites;

public class FragmentBookSource extends Fragment {

    private RadioGroup radioGroup;
    private CallBackValue callBackValue;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_source,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        radioGroup = view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = radioGroup.findViewById(checkedId);
                callBackValue.sendMessage(radioButton.getText().toString());
            }
        });

        for (int i = 0; i< Sites.COMIC_GROUP.length; i++){
            RadioButton radioButton = new RadioButton(view.getContext());
            radioButton.setText(Sites.COMIC_GROUP[i]);
            radioGroup.addView(radioButton);
            if(i==0 && !radioButton.isChecked()){
                radioButton.setChecked(true);
            }
        }
    }

    //传数据至Activity
    public interface CallBackValue{
        void sendMessage(String str);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callBackValue = (CallBackValue) getActivity();
    }
}
