package com.baizhitong.spider;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupHelper {
   
  
    
    public static String parseURL(String url){
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.toString();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
    public static void main(String[] args) {
        String url="http://www.jyeoo.com/math/ques/partialques?q=1&f=0&ct=9&dg=1&fg=0&po=0&pd=1&pi=21&lbs=&so=0&r=0.96070776008525512";
        System.out.println(parseURL(url));
        
    }
    
}
