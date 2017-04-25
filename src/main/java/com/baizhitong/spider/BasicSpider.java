package com.baizhitong.spider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.cookie.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.baizhitong.spider.ResourceHelper.DownLoadImg;

public class BasicSpider {
    public static final String IMG_PATH         = "D://img";                                                                             // 本地图片路径
    public static final String LOCAL_IMG_PATH   = "http://test.emmoc.com";                                                               // 公司图片服务器路径
    public static final String TARGET_URL       = "http://tiku.21cnjy.com";                                                              // 网站基本路径
    public static String       defaultSubject   = "2";                                                                                   // 语文
    public static String       chuzhongSection   = "2";  
    public static String       gaozhongSection   = "3"; // 初中
    public static String       basePath         = "http://tiku.21cnjy.com/tiku.php?mod=quest&channel={1}&xd={2}";
    public static String       questinPath      = "http://tiku.21cnjy.com/tiku.php?mod=quest&channel={1}&xd={2}&cid=0&type={3}&page={4}";
    public static String       subjectCodeRegex = "channel=(\\d*)";                                                                      // 解析学科正则
    public static String       typeRegex        = "type=(\\d*)";                                                                         // 解析题型正则
    public static String       pageRex          = "&page=(\\d*)";                                                                        // 解析页码正则
    public static Set          subjectSet       = new HashSet<Map<String, Integer>>();
    public static Set          questionType     = new HashSet<Map<String, Object>>();
    public static final String      baseUrl      = "http://www.jyeoo.com";
    public static final String      loginURL     = "http://www.jyeoo.com/account/loginform?t=&u=&r=cc417319-372a-4b04-9893-0cdb655217a7";
    public static final String      submitLogin  = "http://www.jyeoo.com/account/loginform";
    public static String            password     = "123123";
    public static String            loginAccount = "bzht@xyh.com";
    public static void startSpider(){
        basicInit(chuzhongSection);
        basicInit(gaozhongSection);
        
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
    public static void login() {
        Map param = new HashMap<String, String>();
        String content = HttpHelper.post(loginURL, param,null);
        Document doc = Jsoup.parse(content);
        String capimgSrc = doc.select("#capimg").get(0).attr("src");
        HttpHelper.download(baseUrl + capimgSrc, IMG_PATH+ File.separator + "capimg.png");
        Scanner in = new Scanner(System.in);
        String code = in.nextLine();
        in.close();
        param.put("Email", loginAccount);
        param.put("Password", password);
        param.put("Captcha", code);
        param.put("Remember", false);
        param.put("Ver", true);
        param.put("AnonID", getAnonID(capimgSrc));
        HttpHelper.post(submitLogin, param,null);
        List<Cookie>StringeList = HttpHelper.httpClient.getCookieStore().getCookies();
        StringBuffer cookiesSB = new StringBuffer();
        if (StringeList==null||StringeList.size()<=0) {
            System.out.println("None");
        } else {
            for (Cookie cook:StringeList) {
                // System.out.println("- " + cookies.get(i).toString());
                cookiesSB.append(cook.getName()).append("=").append(cook.getValue())
                                .append("; ");
            }
            HttpHelper.cookie = cookiesSB.toString();
        }
        String url = "http://www.jyeoo.com/math/ques/partialques?q=1&f=1&ct=0&dg=0&fg=0&po=0&pd=1&pi=2&lbs=&so=0&r=0.9099912250612396";
        Map param2 = new HashMap<String, String>();

        String content2 = HttpHelper.post(url, param,null);
        System.out.println(content2.toString());
    }
    
    public static void basicInit(String sectionCode) {
        try {
            Document document = Jsoup.connect(StringUtils.formatter(basePath, defaultSubject, sectionCode)).get();

            // 获取学科
            Elements subjectList = document.select("div.catagory>p:first-of-type>a");
            for (Element element : subjectList) {
                String src = element.attr("href");
                String subjectName = element.html();
                String _subjectCode = StringUtils.getPattern(src, subjectCodeRegex);
                Map subjectMap = new HashMap<String, Integer>();
                subjectMap.put("subjectName", subjectName);
                subjectMap.put("subejctCode", _subjectCode);
                if (!subjectSet.contains(subjectMap)) {
                    DBUtils.insert("subject", subjectMap, true);
                    subjectSet.add(subjectMap);
                    getQuestionType(_subjectCode, "2");
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    //获取学科题型
    public static void getQuestionType(String subjectCode, String sectionCode){
        // 获取学科题型
        try {
            Document document = Jsoup.connect(StringUtils.formatter(basePath, subjectCode, sectionCode)).get();
            Elements questionTypeList = document.select("div.catagory>p:last-of-type>a");
            for (Element element : questionTypeList) {
                String src = element.attr("href");
                String questionSubjectTypeName = element.html();
                String questionTypeCode = StringUtils.getPattern(src, typeRegex);
                Map questonTypeMap = new HashMap<String, Integer>();
                questonTypeMap.put("questionSubjectTypeName", questionSubjectTypeName);
                questonTypeMap.put("questionTypeCode", questionTypeCode);
                questonTypeMap.put("subjectCode", subjectCode);
                if (!questionType.contains(questonTypeMap)) {
                    DBUtils.insert("question_Type", questonTypeMap, true);
                    questionType.add(questonTypeMap);
                    /* parseQuestion(subjectCode,questionTypeCode,sectionCode); */
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 解析某个学科某个题型的所有试题
     * 
     * @param subjectCode
     * @param questionType
     * @param sectionCode
     */
    public static void parseQuestion(String subjectCode, String questionType, String sectionCode) {
        try {
            // 先获取一共多少页
            Document document = Jsoup.connect(StringUtils.formatter(questinPath, subjectCode, sectionCode, questionType, 1)).get();
            Elements page = document.select("a.last");
            String pageHref = page.attr("href");
            int totalPage = Integer.parseInt(StringUtils.getPattern(pageHref, pageRex));

            // 解析试题
            for (int i = 1; i <= totalPage; i++) {
                Document questionDoc = Jsoup.connect(StringUtils.formatter(questinPath, subjectCode, sectionCode, questionType, i))
                                .get();
                Elements questionList = questionDoc.select(".questions_col>ul>li");
                for (Element question : questionList) {
                    Elements imgs = question.select("img");
                    List<String> imgPath=new ArrayList<String>();
                    for(Element img:imgs){
                        imgPath.add(img.attr("src")); 
                    }
                    DownLoadImg imgThead =new DownLoadImg(imgPath,TARGET_URL,IMG_PATH);
                    imgThead.run();
                    String questionContent = question.html();
                    int index = questionContent.indexOf("<p class=\"btns\">");
                    questionContent = questionContent.substring(0, index);
                    questionContent = urlReplace(questionContent, LOCAL_IMG_PATH);

                    Element jixi = question.select("p.btns>a.view_all").get(0);
                    String jixiUrl = jixi.attr("href");
                    Map<String, Object> questionMap = new HashMap<String, Object>();
                    questionMap.put("subjectCode", subjectCode);
                    questionMap.put("sectionCode", sectionCode);
                    questionMap.put("questionType", questionType);
                    questionMap.put("content", questionContent);
                    // 获取答案和解析
                    putQuestionAnswerAndAnalysis(TARGET_URL + "/" + jixiUrl, questionMap);
                    DBUtils.insert("question", questionMap, true);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String urlReplace(String url, String repalcement) {
        return url.replaceAll(TARGET_URL, repalcement);
    }

    /**
     * 获取题目解析
     * 
     * @param url
     */
    public static void putQuestionAnswerAndAnalysis(String url, Map<String, Object> map) {
        try {
            Document document = Jsoup.connect(url).get();
            Element answer = document.select("dd>p:first-of-type>i").get(0);
            Element analysis = document.select("dd>p:last-of-type>i").get(0);
            String answerStr = answer.html();
            String analysisStr = analysis.html();
            map.put("answer", answerStr);
            map.put("analysis", analysisStr);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    

    public static void main(String[] args) {
        /* String s= getPattern("tiku.php?mod=questamp;channel=2amp;xd=2", subjectCodeRegex); */

        //DBUtils.start();
        /*
         * DBUtils.dropTable("question_Type"); DBUtils.dropTable("subject");
         * getSubejctQuestionType(defaultSubject,defaultSection);
         */
        //parseQuestion("3", "1", "2");
        //DBUtils.close();
        login();

    }

}
