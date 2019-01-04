package com.drafens.dranacg.comic;

import android.util.Base64;
import android.util.Log;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.Episode;
import com.drafens.dranacg.Sites;
import com.drafens.dranacg.error.MyJsoupResolveException;
import com.drafens.dranacg.error.MyNetworkException;
import com.drafens.dranacg.tools.MyDescription;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chuixue extends Sites {
    private static String url_chuixue = "http://m.chuixue.net";

    @Override
    public List<Book> getSearch(String search_id) throws MyNetworkException,MyJsoupResolveException {
        List<Book> bookList=new ArrayList<>();
        Document document;
        try {
            search_id = URLEncoder.encode(search_id, "gb2312");
            String url = url_chuixue + "/e/search/?searchget=1&tbname=movie&tempid=1&show=title,keyboard&keyboard=" + search_id;
            document = Jsoup.connect(url).header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("referer", "m.chuixue.net")
                    .header("Accept-Language", "zh-CN,zh;q=0.8")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                    .get();
        }catch (Exception e){
            e.printStackTrace();
            throw new MyNetworkException();
        }
        try {
            Elements elements = document.select("ul[id=detail]");
            for (Element element : elements.select("li")) {
                String id = element.select("a").first().attr("href");
                Book book = new Book(Sites.CHUIXUE, id);
                book.setIcon(element.select("img").attr("data-src"));
                book.setName(element.select("h3").text());
                book.setAuthor(element.select("dd").get(0).text());
                book.setType(element.select("dd").get(1).text());
                book.setUpdateChapter(element.select("dd").get(2).text());
                book.setUpdateTime("更新于：" + elements.select("dd").get(3).text());
                bookList.add(book);
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new MyJsoupResolveException();
        }
        return bookList;
    }

    @Override
    public Book getBook(Book book) throws MyJsoupResolveException{
        try {
            String url = url_chuixue + book.getId().replace("mh","manhua");
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("div[class=book-detail]");
            String updateChapter_id=elements.select("dd").get(4).select("a").attr("href");
            updateChapter_id=updateChapter_id.substring(0,updateChapter_id.indexOf(".html")).replace("http://www.chuixue.net/","/");
            book.setUpdateChapter_id(updateChapter_id);
            book.setBriefInfo(elements.select("div[id=bookIntro]").text());
            book.setUpdateChapter(elements.select("dd").get(4).select("a").text());
            book.setUpdateTime(elements.select("dd").get(3).text());
        }catch(Exception e){
            e.printStackTrace();
            throw new MyJsoupResolveException();
        }
        return book;
    }

    @Override
    public List<Episode> getEpisode(String book_id) throws MyJsoupResolveException{
        List<Episode> episode_id=new ArrayList<>();
        try {
            String url = url_chuixue + book_id.replace("mh", "manhua");
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("div[class=chapter]");
            for(Element element:elements.select("li").select("a")) {
                String name=element.attr("title");
                String id=element.attr("href");
                id=id.substring(0,id.indexOf(".html")).replace("http://www.chuixue.net/","/");
                Episode episode = new Episode(name,id);
                episode_id.add(episode);
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new MyJsoupResolveException();
        }
        Collections.reverse(episode_id);
        return episode_id;
    }

    @Override
    public List<String> getImage(String episode_id) throws MyNetworkException{
        String url = url_chuixue + episode_id.replace("mh","manhua") + ".html";
        Log.d("TAG", url);
        String header1 = "http://2.huanleyunpai.com/";
        String header2 = "http://img.huanleyunpai.com/";
        List<String> urlList = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("script[type=text/javascript]");
            String string = elements.toString();

            int i = string.indexOf("lyhzh");
            String headerMark = string.substring(i+7,string.indexOf(",",i)-1);
            Log.d("TAG", headerMark);
            String header;
            if (headerMark.equals("zjwb")){
                header = header1;
            }else {
                header = header2;
            }
            if (!string.contains("photosr[1]")){
                int begin = string.indexOf("packed=\"")+8;
                string=string.substring(begin,string.indexOf("\"",begin));
                string = MyDescription.base64Decode(string);
                string = MyDescription.evalArrayToString(string);
                string = MyDescription.evalDecode(string);
                String[] arr = string.split(",");
                for (String anArr : arr) {
                    urlList.add(header + anArr);
                }
            }else {
                int begin = string.indexOf("photosr[1]") + 7;
                int end = string.indexOf("var", begin);
                String[] arr = string.substring(begin, end).split("photosr");
                for (String anArr : arr) {
                    begin = anArr.indexOf("\"") + 1;
                    end = anArr.indexOf("\"", begin);
                    urlList.add(header + anArr.substring(begin, end));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new MyNetworkException();
        }
        com.orhanobut.logger.Logger.d(urlList);
        return urlList;
    }
}
