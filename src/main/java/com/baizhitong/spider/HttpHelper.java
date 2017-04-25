package com.baizhitong.spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpHelper {
    static Logger logger=LoggerFactory.getLogger(HttpHelper.class);
    public static BasicCookieStore  cookieStore;
    /*
     * public static CloseableHttpClient
     * httpclient=HttpClients.custom().setDefaultCookieStore(cookieStore).build();
     */
    public static DefaultHttpClient httpClient;
    public static HttpClientContext context;
    public static List<Cookie>      cookieList;
    public static String            cookie;


    static {
        cookieStore = new BasicCookieStore();
        httpClient = new DefaultHttpClient();
        context = HttpClientContext.create();
    }

    public static String get(String url,Map<String,String> header) {
        HttpGet get = new HttpGet(url);
        try {
         if (StringUtils.isNotEmpty(cookie)) {
            get.setHeader("Cookie", cookie);
        } else {
            get.setHeader("Cookie", defaultCookie);
        }
        for(Entry entry:header.entrySet()){
            get.setHeader(entry.getKey().toString(),entry.getValue().toString());
        }  
        HttpResponse response = httpClient.execute(get);
        org.apache.http.StatusLine statusLine= response.getStatusLine();
        statusLine.getReasonPhrase();
        statusLine.getStatusCode();
        System.out.println( statusLine.getReasonPhrase()+":"+statusLine.getStatusCode());
        HttpEntity entity=response.getEntity();
        InputStream inputStream = entity.getContent();
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufr = new BufferedReader(reader);
        StringBuffer content = new StringBuffer();
        String line = "";
        while ((line = bufr.readLine()) != null) {
            content.append(line);
        }
        return content.toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    static String defaultCookie = "_qddac=3-3-1.1.yxa85s.j1h92mor; aaaaHqNL_ef65_saltkey=uxOKr4tP; aaaaHqNL_ef65_lastvisit=1491907624; UM_distinctid=15b5cd7559f1-01b6d2da49ef6a-4e47052e-1fa400-15b5cd755a3177; HqNL_ef65_saltkey=HJnC1rCW; HqNL_ef65_lastvisit=1491970726; tencentSig=2092792832; Hm_lvt_0280ecaa2722243b1de4829d59602c72=1491911268,1491974348,1492048072,1492050689; _csrf=0287d91a4bf72c61b78cac83ac53b8bc80672e1f46fa55ad551c5d80aa5671eea%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%223QPWr-t3rvhtXxXkrvV3VAkG4aUG6Czo%22%3B%7D;_qddaz=QD.sjigsy.5czd9k.j1dhgb6d; _qdda=3-1.1; _qddab=3-yxa85s.j1h92mor; _qddamta_4006379991=3-0;";
    
    public static String post(String url, Map<String, String> param,Map<String,String> header){
       return  post( url,param,null,header);
    }
    public static String post(String url, Map<String, String> param,String cookie,Map<String,String> header) {
        try {
            httpClient=new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            EntityBuilder builder = EntityBuilder.create();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if (param != null) {
                for (Entry entry : param.entrySet()) {
                    NameValuePair nameValue = new BasicNameValuePair(entry.getKey().toString(),
                                    entry.getValue().toString());
                    params.add(nameValue);
                }
            }
            ;
            builder.setParameters(params);
            post.setEntity(builder.build());
            if (StringUtils.isNotEmpty(cookie)) {
                post.setHeader("Cookie", cookie);
            }
            if(header!=null){
                for(Entry entry:header.entrySet()){
                    post.setHeader(entry.getKey().toString(),entry.getValue().toString());
                }
            }
//            System.out.println(com.baizhitong.spider.StringUtils.formatter("地址{1},cookie{2}", url,cookie.substring(cookie.indexOf("xd"))));
//            post.setHeader("Host", "zujuan.21cnjy.com");
//            post.setHeader("Accept","Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//            post.setHeader("X-CSRF-Token", "RkxiM254ZER1HTJkHFUQdzQ6Ckc2ADwvNDo0ADg5DwNyLTd0WDseKw==");
//            post.setHeader("Accept-Encoding", "identity");
           /* post.setHeader("Referer", "//zujuan.21cnjy.com/question/index?chid=2&xd=2&tree_type=knowledge");*/
//            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
            HttpResponse response2 = httpClient.execute(post);
            Header[] headers = post.getAllHeaders();
            HttpEntity entity = response2.getEntity();
            InputStream inputStream = entity.getContent();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufr = new BufferedReader(reader);
            StringBuffer content = new StringBuffer();
            String line = "";
            while ((line = bufr.readLine()) != null) {
                content.append(line);
            }
            logger.error(content.toString());
            return content.toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    // 创建目录
    public static void createDirectory(String path) {

        File directory;
        directory = new File(path);
        if (!directory.exists()) {
            if (path.endsWith(File.separator)) {
                directory.mkdirs();
            } else {
                String directoryPath = path.substring(0, path.lastIndexOf("/"));
                directory = new File(directoryPath);
                directory.mkdirs();
            }
        }
    }

    // 下载图片
    public static boolean download(String urlStr, String path) {
        if (StringUtils.isEmpty(urlStr)) {
            return false;
        }
        try {
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setDoOutput(true);
            createDirectory(path);
            File file = new File(path);
            file.setWritable(true);
            file.setReadable(true);
            FileOutputStream out = new FileOutputStream(path);
            InputStream in = connection.getInputStream();
            byte b[] = new byte[1024];
            int j = 0;
            while ((j = in.read(b)) != -1) {
                out.write(b, 0, j);
            }
            out.flush();
            File file2 = new File(path);
            if (file2.exists() && file2.length() == 0)
                return false;
            return true;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static void setCookies(String url, Map<String, String> map) {
        String content = post(url, map,null);
        cookieList = httpClient.getCookieStore().getCookies();
        StringBuffer cookiesSB = new StringBuffer();
        cookiesSB.append(defaultCookie);
        if (cookieList.isEmpty()) {
            System.out.println("None");
        } else {
            for (int i = 0; i < cookieList.size(); i++) {
                // System.out.println("- " + cookies.get(i).toString());
                if (!cookieList.get(i).getName().equals("_csrf")) {
                    cookiesSB.append(cookieList.get(i).getName()).append("=").append(cookieList.get(i).getValue())
                                    .append("; ");
                    System.out.println(cookieList.get(i).getValue());
                }
            }
            cookie = cookiesSB.toString();
        }

    }

   

    // 获取回话tocken
    public static String getAnonID(String url) {
        Pattern pattern = Pattern.compile("captcha\\/([^?]*)");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return "";

    }

    public static void main(String[] args) {
        /*
         * String url=
         * "http://www.jyeoo.com/math/ques/partialques?q=1&f=1&ct=0&dg=0&fg=0&po=0&pd=1&pi=2&lbs=&so=0&r=0.9099912250612396";
         * Map param=new HashMap<String,String>();
         * 
         * String content=HttpHelper.post(url, param); System.out.println(content);
         */
        /*
         * Pattern pattern=Pattern.compile("captcha\\/([^?]*)"); Matcher matcher= pattern.matcher(
         * "http://www.jyeoo.com/api/captcha/2e0d20f67af94007af46eb2b00f92804?w=160&r=0.9184309027970619"
         * ); while( matcher.find()){ System.out.println(matcher.group(1)); }
         */
        /* login(); */
        createDirectory("D://img/tikupic/6c/6c8641a0ed1b0c9355ac0865102c4a8e.png");
    }

}
