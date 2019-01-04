package com.drafens.dranacg.comic;

import android.util.Base64;
import android.util.Log;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.Episode;
import com.drafens.dranacg.Sites;
import com.drafens.dranacg.error.MyJsonFormatException;
import com.drafens.dranacg.error.MyJsoupResolveException;
import com.drafens.dranacg.error.MyNetworkException;
import com.drafens.dranacg.tools.MyDescription;
import com.orhanobut.logger.Logger;

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
    private static String url_pufei = "http://m.pufei.net";

    @Override
    public List<Book> getSearch(String search_id) throws MyNetworkException, MyJsoupResolveException {
        List<Book> bookList=new ArrayList<>();
        Document document;
        try {
            search_id = URLEncoder.encode(search_id, "gb2312");
            String url = url_pufei + "/e/search/?searchget=1&tbname=mh&show=title,player,playadmin,bieming,pinyin&tempid=4&keyboard=" + search_id;
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
            book.setBriefInfo(elements.select("div[id=bookIntro]").text());
            Element element = document.select("div[class=chapter]").select("li").get(0);
            String updateChapter_id = element.select("a").attr("href");
            book.setUpdateChapter_id(updateChapter_id.substring(0,updateChapter_id.indexOf(".html")));
            book.setUpdateChapter(elements.select("dd").get(0).text());
            book.setUpdateTime(elements.select("dd").get(1).text());
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
    public List<String> getImage(String episode_id) throws MyNetworkException{
        String url = url_pufei + episode_id + ".html";
        String header = "http://res.img.pufei.net/";
        List<String> urlList = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            Element element = document.select("script[type=text/javascript]").get(5);
            String string = element.toString();
            int begin = string.indexOf("cp=\"")+4;
            string=string.substring(begin,string.indexOf("\"",begin));
            string = MyDescription.base64Decode(string);
            string = MyDescription.evalDecode(string);
            String[] arr = string.split(",");
            for (String anArr : arr) {
                urlList.add(header + anArr);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new MyNetworkException();
        }
        return urlList;
    }
}