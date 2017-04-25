package com.baizhitong.spider;

import java.io.File;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ResourceHelper {
    
    // 下载图片
    public static class DownLoadImg implements Runnable {
        public List<String> imgs;
        public String targetUrl;
        public String imgPath;

        public DownLoadImg(List<String> imgs,String targetUrl,String imgPath) {
            super();
            this.imgs = imgs;
            this.targetUrl = targetUrl;
            this.imgPath = imgPath;
        }

        public void run() {
            for (String imgsrc : imgs) {
               /* String src = img.attr("src");*/
                String fileName="";
                if(imgsrc.lastIndexOf(".")<0){
                    return;
                }
                String ext=imgsrc.substring(imgsrc.lastIndexOf(".")+1,imgsrc.length());
                if(("bmp、gif、jpg、pic、png、tif").indexOf(ext.toLowerCase()) > -1){
                    if(imgsrc.lastIndexOf(targetUrl)>-1){
                        fileName = imgsrc.substring(imgsrc.lastIndexOf(targetUrl) + targetUrl.length(), imgsrc.length());  
                   }else{
                        fileName=imgsrc;  
                   }
                    HttpHelper.download(imgsrc, imgPath + File.separator + fileName);  
                    
                }
            }
        }
    }
}
