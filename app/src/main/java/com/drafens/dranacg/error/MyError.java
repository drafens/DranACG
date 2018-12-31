package com.drafens.dranacg.error;

import android.content.Context;
import android.widget.Toast;

public class MyError{
    public final static int MyNetworkException = 0;
    public final static int MyJsoupResolveException = 1;
    public final static int MyFileWriteException = 2;
    public final static int MyJsonFormatException = 3;

    public static void show(Context context, int errorCode){
        String detail = "";
        switch (errorCode){
            case MyNetworkException:
                detail="网络错误";
                break;
            case MyJsoupResolveException:
                detail="网站解析错误";
                break;
            case MyFileWriteException:
                detail="文件读写错误";
                break;
            case MyJsonFormatException:
                detail="本地文件格式错误";
                break;
        }
        Toast.makeText(context,detail,Toast.LENGTH_LONG).show();
        /*Snackbar.make(view, detail, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Intent intent = new Intent(context, MyError.class);
        intent.putExtra("error_code", errorCode);
        context.startActivity(intent);*/
    }
}
