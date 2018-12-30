package com.drafens.dranacg.comic;

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

import java.util.ArrayList;
import java.util.List;

public class Gufeng extends Sites {
    private static String TAG = "Gufeng";
    private static String url_gufeng = "https://m.gufengmh.com";
    @Override
    public List<Book> getSearch(String search_id) throws MyNetworkException,MyJsoupResolveException {
        List<Book> bookList=new ArrayList<>();
        Document document;
        try {
            Log.d(TAG, url_gufeng + "/search/?keywords=" + search_id);
            String url = url_gufeng + "/search/?keywords=" + search_id;
            document = Jsoup.connect(url).header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("referer", "m.gufengmh.com")
                    .header("Accept-Language", "zh-CN,zh;q=0.8")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                    .get();
        }catch (Exception e){
            e.printStackTrace();
            throw new MyNetworkException();
        }
        try{
            Elements elements = document.select("[class=UpdateList]");
            int i=0;
            for(Element element:elements.select("[class=itemTxt]")) {
                String id=element.select("a").attr("href").replace(url_gufeng,"");
                Book book = new Book(Sites.GUFENG,id);
                book.setName(element.select("a").text());
                book.setAuthor(element.select("p").get(0).text());
                book.setType(element.select("p").get(1).text());
                book.setUpdateTime("更新于："+element.select("p").get(2).text());
                book.setIcon(elements.select("[class=itemImg]").get(i).select("mip-img").attr("src"));
                Log.d(TAG, book.toString());
                bookList.add(book);
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new MyJsoupResolveException();
        }
        return bookList;
    }

    @Override
    public Book getBook(Book book){
        Log.d(TAG, "getBook: ");
        //book.setUpdateChapter(updateChapter);
        //book.setUpdateChapter_id(updateChapter_id);
        //book.setUpdateTime(updateTime);
        return book;
    }

    @Override
    public List<Episode> getEpisode(String book_id) throws MyJsoupResolveException{
        List<Episode> episodeList=new ArrayList<>();
        try {
            String url = url_gufeng + book_id;
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("[id=chapter-list-1]");
            for(Element ele:elements.select("li").select("a")) {
                String name=ele.select("span").text();
                String id=ele.attr("href");
                id=id.substring(0,id.indexOf(".html"));
                Episode episode = new Episode(name,id);
                episodeList.add(episode);
            }
            elements = document.select("[id=chapter-list-13]");
            for(Element ele:elements.select("li").select("a")) {
                String name=ele.select("span").text();
                String id=ele.attr("href");
                id=id.substring(0,id.indexOf(".html"));
                Episode episode = new Episode(name,id);
                episodeList.add(episode);
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new MyJsoupResolveException();
        }
        return episodeList;
    }

    @Override
    public List<String> getImage(String episode_id) throws MyNetworkException {
        String url = url_gufeng + episode_id + ".html";
        String[] strings;
        List<String> urlList = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            if (document.toString().contains("mip-img")){
                int total = Integer.parseInt(document.select("[id=k_total]").text());
                String s = document.select("mip-img").get(0).attr("src");
                urlList.add(s);
                for (int i=2;i<=total;i++) {
                    document = Jsoup.connect(url_gufeng + episode_id + "-" + i + ".html").get();
                    s = document.select("mip-img").get(0).attr("src");
                    urlList.add(s);
                }
            }else {
                Element element = document.select("script").get(1);
                String s = element.toString();
                int begin = s.indexOf("chapterPath") + 15;
                String chapterPath = "/" + s.substring(begin, s.indexOf(";", begin) - 1);
                begin = s.indexOf("[") + 1;
                String chapterImages = s.substring(begin, s.indexOf("]", begin));
                strings = chapterImages.split(",");
                for (String string : strings) {
                    urlList.add("https://res.gufengmh.com" + chapterPath + string.substring(1, string.length() - 1));
                }
            }
            Log.d(TAG, urlList.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyNetworkException();
        }
        return urlList;
    }
}
