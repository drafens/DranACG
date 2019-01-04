package com.drafens.dranacg.tools;

import android.util.Base64;
import android.util.Log;

import com.drafens.dranacg.error.MyJsoupResolveException;
import com.orhanobut.logger.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class MyDescription {

    public static String base64Decode(String string) {
        return new String(Base64.decode(string.getBytes(),Base64.DEFAULT));
    }

    public static String evalDecode(String jsCode) throws MyJsoupResolveException {
        try {
            Context rhino = Context.enter();
            rhino.setOptimizationLevel(-1);
            Scriptable scope = rhino.initStandardObjects();
            Object object = rhino.evaluateString(scope, jsCode, null, 1, null);
            String string = Context.toString(object);
            if (string.equals("[Object object]")){
                throw new MyJsoupResolveException();
            }
            return string;
        }catch (Exception e){
            throw new MyJsoupResolveException();
        }
    }

    public static String evalArrayToString(String evalArray){
        int begin = evalArray.indexOf("}('");
        String header = evalArray.substring(0,begin+1);

        String string = evalArray.substring(begin);
        String[] strings = string.split(";");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<strings.length-1;i++){
            begin = strings[i].indexOf("=");
            stringBuilder.append("\\'").append(strings[i].substring(begin + 2, strings[i].length() - 1)).append("\\',");
        }
        String evalString = stringBuilder.toString();

        evalString = header+"('myEvalString=["+evalString.substring(0,evalString.length()-1)+"]"+strings[strings.length-1];
        return evalString;
    }
}
