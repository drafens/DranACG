package com.drafens.dranacg.tools;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.drafens.dranacg.error.MyFileWriteException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;

import java.io.File;
import java.io.FileInputStream;

public class Webdav {
    private static String davUrl = "https://dav.jianguoyun.com/dav/DranACG";
    private static String fileName = "/favourite_comic.json";

    public static void isConnected(final Context context, final String userName, final String passWord,final IsConnectedCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    Credentials creds = new UsernamePasswordCredentials(userName, passWord);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    final DavMethod mkCol = new MkColMethod(davUrl);
                    client.executeMethod(mkCol);
                    if (mkCol.getStatusCode() == 201) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.connectedSucceed();
                            }
                        });
                    }else {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.connectedFailed(mkCol.getStatusCode());
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.connectedFailed(0);
                        }
                    });
                }
            }
        }).start();
    }


    public interface IsConnectedCallback {
        void connectedSucceed();
        void connectedFailed(int failedCode);
    }

    static void putFile(final File file, final String userName, final String passWord){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    Credentials creds = new UsernamePasswordCredentials(userName, passWord);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    DavMethod mkCol = new MkColMethod(davUrl);
                    client.executeMethod(mkCol);
                    if (mkCol.getStatusCode() == 201) {
                        PutMethod put = new PutMethod(davUrl + fileName);
                        RequestEntity requestEntity = new InputStreamRequestEntity(new FileInputStream(file));
                        put.setRequestEntity(requestEntity);
                        client.executeMethod(put);
                    }else {
                        throw new MyFileWriteException();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getFile(final String userName, final String passWord){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    Credentials creds = new UsernamePasswordCredentials(userName, passWord);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    DavMethod mkCol = new MkColMethod(davUrl);
                    client.executeMethod(mkCol);
                    if (mkCol.getStatusCode() == 201) {
                        GetMethod get = new GetMethod(davUrl);
                        client.executeMethod(get);
                        byte[] bytes = get.getResponseBody();
                        String string = new String(bytes);
                        Log.d("TAG", string);
                    }else {
                        throw new MyFileWriteException();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void findFile(final String userName, final String passWord){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    Credentials creds = new UsernamePasswordCredentials(userName, passWord);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    DavMethod mkCol = new MkColMethod(davUrl);
                    client.executeMethod(mkCol);
                    if (mkCol.getStatusCode() == 201) {
                        DavMethod find = new PropFindMethod(davUrl, DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
                        client.executeMethod(find);
                        MultiStatus multiStatus = find.getResponseBodyAsMultiStatus();
                        MultiStatusResponse[] responses = multiStatus.getResponses();
                        for (MultiStatusResponse response : responses) {
                            Log.d("TAG", response.getHref());
                        }
                    }else {
                        throw new MyFileWriteException();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
