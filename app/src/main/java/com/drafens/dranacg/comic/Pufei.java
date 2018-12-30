package com.drafens.dranacg.comic;

import android.util.Base64;
import android.util.Log;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.Episode;
import com.drafens.dranacg.Sites;
import com.drafens.dranacg.error.MyJsoupResolveException;
import com.drafens.dranacg.error.MyNetworkException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Pufei extends Sites {
    private static final String TAG = "Pufei";
    private static String url_pufei = "http://m.pufei.net";

    @Override
    public List<Book> getSearch(String search_id) throws MyNetworkException, MyJsoupResolveException {
        List<Book> bookList=new ArrayList<>();
        Document document;
        try {
            search_id = URLEncoder.encode(search_id, "gb2312");
            String url = url_pufei + "/e/search/?searchget=1&tbname=mh&show=title,player,playadmin,bieming,pinyin&tempid=4&keyboard=" + search_id;
            Log.d(TAG, "getSearch: " + url);
            document = Jsoup.connect(url).header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("referer", "m.pufei.com")
                    .header("Accept-Language", "zh-CN,zh;q=0.8")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                    .get();
        }catch (Exception e){
            e.printStackTrace();
            throw new MyNetworkException();
        }
        try {
            Elements elements = document.select("ul[id=detail]");
            for(Element element:elements.select("li")) {
                String id=element.select("a").first().attr("href");
                Book book = new Book(Sites.PUFEI,id);
                book.setIcon(element.select("img").attr("data-src"));
                book.setName(element.select("h3").text());
                book.setAuthor(element.select("dd").get(0).text());
                book.setType(element.select("dd").get(1).text());
                book.setUpdateChapter(element.select("dd").get(2).text());
                book.setUpdateTime("更新于："+elements.select("dd").get(3).text());
                bookList.add(book);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new MyJsoupResolveException();
        }
        return bookList;
    }

    @Override
    public Book getBook(Book book) throws MyJsoupResolveException {
        try {
            String url = url_pufei + book.getId();
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("div[class=book-detail]");
            String briefInfo=elements.select("div[id=bookIntro]").text();
            Element element = document.select("div[class=chapter]").select("li").get(0);
            String updateChapter_id = element.select("a").attr("href");
            updateChapter_id = updateChapter_id.substring(0,updateChapter_id.indexOf(".html"));
            book.setBriefInfo(briefInfo);
            book.setUpdateChapter_id(updateChapter_id);
            //book.setUpdateChapter(updateChapter);
            //book.setUpdateTime(updateTime);
        }catch(Exception e){
            e.printStackTrace();
            throw new MyJsoupResolveException();
        }
        return book;
    }

    @Override
    public List<Episode> getEpisode(String book_id) throws MyJsoupResolveException {
        List<Episode> episodeList=new ArrayList<>();
        try {
            String url = url_pufei + book_id;
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("div[class=chapter]");
            for(Element ele:elements.select("li").select("a")) {
                String name=ele.attr("title");
                String id=ele.attr("href");
                id=id.substring(0,id.indexOf(".html"));
                Episode episode = new Episode(name,id);
                episodeList.add(episode);
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new MyJsoupResolveException();
        }
        Collections.reverse(episodeList);
        return episodeList;
    }

    @Override
    public List<String> getImage(String episode_id) throws MyNetworkException {
        String url = url_pufei + episode_id + ".html";
        String[] strings;
        List<String> urlList;
        String[] replace_old = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        String string;
        try {
            Document document = Jsoup.connect(url).get();
            Element element = document.select("script[type=text/javascript]").get(5);
            string = element.toString();
            int begin = string.indexOf("cp=\"")+4;
            string=string.substring(begin,string.indexOf("\"",begin));
            string = new String(Base64.decode(string.getBytes(),Base64.DEFAULT));
            string=string.substring(string.indexOf("}('")+3,string.indexOf("split")-2);
            int index=string.indexOf("]");
            String s1=string.substring(string.indexOf("[")+3,index-2).replace("\\'","");
            String s2=string.substring(string.indexOf("'",index+2)+1);
            String[] s1_a=s1.split(",");
            String[] s2_arr=s2.split("\\|");
            String[][] s2_a=new String[s2_arr.length/62+1][62];
            for (int i = 0; i < s1_a.length; i++) {
                s1_a[i] = "/"+s1_a[i].replace(".","/./")+"/";
            }
            for (int i=0;i<s2_a.length;i++){
                for(int j=0;j<62&&j<s2_arr.length-i*62;j++){
                    s2_a[i][j]=s2_arr[i*62+j];
                }
            }
            strings = new String[s1_a.length];
            String t_value;
            for (int t=0;t<s2_a.length;t++){
                if(t==0) {
                    t_value="";
                }else {
                    t_value= String.valueOf(t);
                }
                for (int i=0;i<s1_a.length;i++){
                    for (int j=0;j<62;j++) {
                        if (s2_a[t][j] != null && !(s2_a[t][j].equals(""))) {
                            s1_a[i] = s1_a[i].replace("/" + t_value + replace_old[j] + "/", "/" + s2_a[t][j] + "/");
                            s1_a[i] = s1_a[i].replace("/" + t_value + replace_old[j] + "/", "/" + s2_a[t][j] + "/");
                            strings[i] = "http://res.img.pufei.net/" + s1_a[i].substring(1, s1_a[i].length() - 1).replace("/./",".");
                        }
                    }
                }
            }
            urlList = new ArrayList<>(Arrays.asList(strings).subList(0, s1_a.length));
        }catch (Exception e){
            e.printStackTrace();
            throw new MyNetworkException();
        }
        return urlList;
    }
}