package com.drafens.dranacg.tools;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.error.MyJsonFormatException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.List;

public class Webdav {
    private static String davUrl = "https://dav.jianguoyun.com";
    private static String FavCatalog = "/dav/DranACG/favourite_comic";
    private static String DavCatalog = "/dav/DranACG";

    public static void isConnected(final Context context, final String username, final String password, final IsConnectedCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    Credentials creds = new UsernamePasswordCredentials(username, password);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    Log.d("TAG", davUrl + DavCatalog);
                    final DavMethod mkCol = new MkColMethod(davUrl + DavCatalog);
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

    static void putFile(final Context context, final JSONObject object){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    String username = FileManager.getPreferenceStr("username", context);
                    String password = FileManager.getPreferenceStr("password", context);
                    Credentials creds = new UsernamePasswordCredentials(username, password);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    DavMethod mkCol = new MkColMethod(davUrl + FavCatalog);
                    client.executeMethod(mkCol);
                    if (mkCol.getStatusCode() == 201) {
                        PutMethod put = new PutMethod(davUrl + FavCatalog + getFileName(object));
                        RequestEntity requestEntity = new InputStreamRequestEntity(new ByteArrayInputStream(object.toString().getBytes("UTF-8")));
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

    static void delFile(final Context context, final String fileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    String username = FileManager.getPreferenceStr("username", context);
                    String password = FileManager.getPreferenceStr("password", context);
                    Credentials creds = new UsernamePasswordCredentials(username, password);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    DavMethod mkCol = new MkColMethod(davUrl + FavCatalog);
                    client.executeMethod(mkCol);
                    if (mkCol.getStatusCode() == 201) {
                        DeleteMethod delete = new DeleteMethod(davUrl + FavCatalog + fileName);
                        client.executeMethod(delete);
                    }else {
                        throw new MyFileWriteException();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getFile(final Context context, final String fileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    String username = FileManager.getPreferenceStr("username", context);
                    String password = FileManager.getPreferenceStr("password", context);
                    Credentials creds = new UsernamePasswordCredentials(username, password);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    DavMethod mkCol = new MkColMethod(davUrl);
                    client.executeMethod(mkCol);
                    if (mkCol.getStatusCode() == 201) {
                        GetMethod get = new GetMethod(davUrl + fileName);
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

    static void syncFile(final Context context, final List<Book> bookList){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    String username = FileManager.getPreferenceStr("username", context);
                    String password = FileManager.getPreferenceStr("password", context);
                    Credentials creds = new UsernamePasswordCredentials(username, password);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    DavMethod mkCol = new MkColMethod(davUrl + FavCatalog);
                    client.executeMethod(mkCol);
                    if (mkCol.getStatusCode() == 201) {
                        DavMethod find = new PropFindMethod(davUrl + FavCatalog, DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
                        client.executeMethod(find);
                        MultiStatus multiStatus = find.getResponseBodyAsMultiStatus();
                        MultiStatusResponse[] responses = multiStatus.getResponses();
                        Log.d("TAG", responses[0].getHref());
                        for (int i=1;i<responses.length;i++) {
                            GetMethod get = new GetMethod(davUrl + responses[i].getHref());
                            client.executeMethod(get);
                            byte[] bytes = get.getResponseBody();
                            String string = new String(bytes);
                            Book book = JsonManger.jsonToBook(new JSONObject(string));
                            string = responses[i].getHref().substring(responses[i].getHref().lastIndexOf("/")+1).replaceAll("_","/");
                            String[] strings = string.split("\\.");
                            int isFavourite = FavouriteManager.isFavourite(strings[0],strings[1],Book.COMIC);
                            if(isFavourite == -1){
                                FavouriteManager.add_favourite(context,book,Book.COMIC);
                            }else{
                                if (Tools.strToInt(book.getLastReadTime()) > Tools.strToInt(bookList.get(isFavourite).getLastReadTime())){
                                    FavouriteManager.update_favourite(false,context,book,Book.COMIC);
                                }
                            }
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

    static String getFileName(JSONObject object) throws MyJsonFormatException {
        try {
            return "/" + (object.getString("webSite") + "." + object.getString("id")).replaceAll("/","_");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MyJsonFormatException();
        }
    }
}
