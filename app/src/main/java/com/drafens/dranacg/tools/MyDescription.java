package com.drafens.dranacg.tools;

import android.util.Base64;

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
}
