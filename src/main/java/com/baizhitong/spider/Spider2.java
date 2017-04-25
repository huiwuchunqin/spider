package com.baizhitong.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.MapUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.baizhitong.spider.ResourceHelper.DownLoadImg;
import com.baizhitong.vo.QuestionVo;
import com.baizhitong.vo.QuestionWithAnswerVO;

/**
 * 21组卷爬虫
 * 
 * 
 */
public class Spider2 {
    static Logger                      logger                 = LoggerFactory.getLogger(Spider2.class);
    private static String              basePath               = "http://zujuan.21cnjy.com/question/index?chid={1}&xd={2}";
    private static Set<String>         gradeSet               = new HashSet<String>();
    private static Set<String>         subjectSet             = new HashSet<String>();

    private static final String        SUBJECT_TABLE          = "subject_new";
    private static final String        QUESTION_TYPE_TABLE    = "subject_question_type_new";
    private static final String        GRADE_TABLE            = "grade_new";
    private static final String        QUESTION_TABLE         = "question_new";
    private static final String        QUESTION__ANSWER_TABLE = "question_answer";

    private static Map<String, Object> codeEncStr             = new HashMap<String, Object>();

    public static final String         IMG_PATH               = "D://imgnew";                                                 // 本地图片路径
    public static final String         LOCAL_PATH             = "http://test.emmoc.com";                                      // 公司图片服务器路径
    public static final String         TARGET_URL             = "http://tikupic.21cnjy.com";
    // 目标网站服务器地址
    public static Map<String, String>  headerMap              = new HashMap<String, String>();

    private static final String        subjectRegx            = "chid=(\\d*)";
    private static final String        sectionRegx            = "xd=(\\d*)";
    public static String               MockDataTestPaperRegex = "(?<=var MockDataTestPaper = ).*(?=;[\\s]*OT2\\.renderQList)";

    static {
        // 网站对学段学科的一些加密 抄下来直接用
        codeEncStr.put("1",
                        "494a1f745cfdce14dad87288ba1fd45465b7d32eec1df130011fd3cd0b6415c2a%3A2%3A%7Bi%3A0%3Bs%3A2%3A%22xd%22%3Bi%3A1%3Bs%3A1%3A%221%22%3B%7D;");
        codeEncStr.put("2",
                        "053c08991644b3be1f1cce76412b1634d2eb9867b529f5e7d3129bc291581e9ba%3A2%3A%7Bi%3A0%3Bs%3A2%3A%22xd%22%3Bi%3A1%3Bs%3A1%3A%222%22%3B%7D;");
        codeEncStr.put("3",
                        "bbc5bced806f427ff9aca7cf1cf56a37c9016539f1da160ae385126fb0d1cc2fa%3A2%3A%7Bi%3A0%3Bs%3A2%3A%22xd%22%3Bi%3A1%3Bs%3A1%3A%223%22%3B%7D;");
        codeEncStr.put("语文",
                        "d79688f1e7866722fd16866f40a252116287a86b4e6e2213e3150af3ba96e038a%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A1%3A%222%22%3B%7D;");
        codeEncStr.put("数学",
                        "9f22ba4715c9a6998a74ca46f669cbdf971fdc8e71df933fca465c01ce29d004a%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A1%3A%223%22%3B%7D;");
        codeEncStr.put("英语",
                        "bd8313dfafcee0d97949afd2be099a6e5a44437eef1f94d21b0a7c33a4321f98a%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A1%3A%224%22%3B%7D;");
        codeEncStr.put("科学",
                        "bd6891efb02e2089ca240a76027e7059a83f5f17127ff3702e5715f697e4b64ca%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A1%3A%225%22%3B%7D;");
        codeEncStr.put("政治",
                        "97301a51a277c02ff19b5051b40c65944910a84f66196518b5babd75068ca205a%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A1%3A%229%22%3B%7D;");
        codeEncStr.put("物理",
                        "91ab0098f41dadc79a63b6bf560dd5c255a7c8c4203ca602dd1302827ff0ed9da%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A1%3A%226%22%3B%7D;");
        codeEncStr.put("化学",
                        " da65ff167a9faf5df5ec0345deb6802854c028769a77d1f54a0848b07a0fdfeba%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A1%3A%227%22%3B%7D;");
        codeEncStr.put("历史",
                        "85f060ac0bc8a850e54bec93c415098b33cfd6a18b326e5fc21ec771cd5e8d90a%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A1%3A%228%22%3B%7D;");
        codeEncStr.put("地理",
                        "8ef07e065bf217b6a63e0f1b06fd7310e362692cdc47872da76f95635aaa996ca%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A2%3A%2210%22%3B%7D;");
        codeEncStr.put("历史与社会",
                        "4531e17db05d2dd8c871211885da9b89f03af38c13bd5dd4a52a150fdd29e211a%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A2%3A%2220%22%3B%7D;");
        codeEncStr.put("生物",
                        "6b75dd5e2d6bf03a9c1bb8e815f8feb3c1ee0df4e46a590bbaa2262cb02839fca%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22chid%22%3Bi%3A1%3Bs%3A2%3A%2211%22%3B%7D;");
        // 请求头信息
        headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headerMap.put("Accept-Encoding", "gzip, deflate, sdch");
        headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Cache-Control", "max-age=0");
        headerMap.put("Host", "zujuan.21cnjy.com");
        headerMap.put("Referer", "http://zujuan.21cnjy.com/question/index?chid=3&xd=1&tree_type=knowledge");
        headerMap.put("Upgrade-Insecure-Requests", "1");
        headerMap.put("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        headerMap.put("X-CSRF-Token", "RkxiM254ZER1HTJkHFUQdzQ6Ckc2ADwvNDo0ADg5DwNyLTd0WDseKw==");
    }

    // 开始抓取基础数据
    public static void basicInit() {
        try {
            Document document = Jsoup.connect(StringUtils.formatter(basePath, 1, 1)).get();

            // 学科
            Elements subjectsAnchors = document.select(".nav-items>.item-list a");
            for (Element subjectAnchor : subjectsAnchors) {
                String href = subjectAnchor.attr("href");
                String _subjectCode = StringUtils.getPattern(href, subjectRegx, 1);
                String _sectionCode = StringUtils.getPattern(href, sectionRegx, 1);
                String subjectName = subjectAnchor.html();
                if (!subjectSet.contains(_subjectCode)) {
                    subjectSet.add(_subjectCode);
                    Map<String, Object> subjectMap = new HashMap<String, Object>();
                    subjectMap.put("subjectCode", _subjectCode);
                    subjectMap.put("subjectName", subjectName);
                    /* subjectMap.put("sectionCode", _sectionCode); */
                    subjectSet.add(_subjectCode);
                    DBUtils.insert(SUBJECT_TABLE, subjectMap, true);
                }

                // 题型和年级
                getOther(_subjectCode, _sectionCode, subjectName);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 获取题型年级信息
    public static void getOther(String subjectCode, String sectionCode, String subjectName) {
        String cookie = HttpHelper.defaultCookie + "xd=" + codeEncStr.get(sectionCode) + "chid="
                        + codeEncStr.get(subjectName);
        String fmtUrl = StringUtils.formatter(basePath, subjectCode, sectionCode);
        Document document = Jsoup.parse(HttpHelper.post(fmtUrl, null, cookie, headerMap));
        // 学科题型
        Elements subjectQuestionType = document.select("a[data-name='question_channel_type']");
        for (Element sqt : subjectQuestionType) {
            String sqtCode = sqt.attr("data-value");
            String sqtName = sqt.html();
            Map<String, Object> sqtMap = new HashMap<String, Object>();
            sqtMap.put("subjectCode", subjectCode);
            sqtMap.put("subjectQuestionTypeName", sqtName);
            sqtMap.put("subjectQuestionTypeCode", sqtCode);
            DBUtils.insert(QUESTION_TYPE_TABLE, sqtMap, true);
        }
        // 年级
        Elements gradeElements = document.select("#J_Nj .checkbox");
        for (Element gradeDiv : gradeElements) {
            String gradeCode = gradeDiv.select("input").val();
            String gradeName = gradeDiv.text();
            if (!gradeSet.contains(gradeCode)) {
                gradeSet.add(gradeCode);
                Map<String, Object> gradeMap = new HashMap<String, Object>();
                gradeMap.put("sectionCode", sectionCode);
                gradeMap.put("gradeCode", gradeCode);
                gradeMap.put("gradeName", gradeName);
                DBUtils.insert(GRADE_TABLE, gradeMap, true);
            }
        }
    }

    // 解析题目
    public static void parseQuestion() {
        Map<String, List<Map>> subjectQuestionType = new HashMap<String, List<Map>>();
        Map<String, List<Map>> sectionGrade = new HashMap<String, List<Map>>();
        Map<String, List<Map>> sectionSubejct = new HashMap<String, List<Map>>();
        String[] sectionArrary = { "1", "2", "3" };
        for (String section : sectionArrary) {
            String gradeSql = "select * from " + DBUtils.preTableName + GRADE_TABLE + " where sectionCode=?";
            List<Map> gradeList = DBUtils.select(gradeSql, section);
            sectionGrade.put(section, gradeList);

            String subjectSql = "select * from " + DBUtils.preTableName + SUBJECT_TABLE;
            List<Map> subjectList = DBUtils.select(subjectSql);
            sectionSubejct.put(section, subjectList);

            String questionTypeSql = "select * from " + DBUtils.preTableName + QUESTION_TYPE_TABLE
                            + " where subjectCode=?";
            for (Map subject : subjectList) {
                String subjectCode = MapUtils.getString(subject, "subjectCode");
                List<Map> subjectQuestionTypeList = DBUtils.select(questionTypeSql, subjectCode);
                subjectQuestionType.put(subjectCode, subjectQuestionTypeList);
            }
        }

        for (String section : sectionArrary) {
            List<Map> gradeList = sectionGrade.get(section);
            List<Map> subjectList = sectionSubejct.get((section));
            for (Map subject : subjectList) {
                String subjectName = MapUtils.getString(subject, "subjectName");
                for (Map grade : gradeList) {
                    String gradeName = MapUtils.getString(grade, "gradeName");
                    String gradeCode = MapUtils.getString(grade, "gradeCode");
                    System.out.println("开始解析 年级：" + gradeName + "  学科：" + subjectName);
                    try {
                        parse("", gradeCode, section, subjectName, 1);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                /*
                 * List<Map> subjectQuestionTypeList =
                 * subjectQuestionType.get(MapUtils.getString(subject, "subjectCode")); for (Map
                 * questionType : subjectQuestionTypeList) { String questionTypeCpde =
                 * MapUtils.getString(questionType, "subjectQuestionTypeCode"); for (Map grade :
                 * gradeList) { String gradeName = MapUtils.getString(grade, "gradeName"); String
                 * gradeCode = MapUtils.getString(grade, "gradeCode"); System.out.println("开始解析 年级："
                 * +gradeName+"  学科："+subjectName); parse(questionTypeCpde, gradeCode, section,
                 * subjectName, 1); } }
                 */
            }
        }
    }

    private static String questionDetailPath = "http://zujuan.21cnjy.com/question/detail/{1}";
    private static String questionUrl        = "http://zujuan.21cnjy.com/question/list?knowledges="
                    + "&question_channel_type={1}" + "&difficult_index=" + "&exam_type=" + "&kid_num="
                    + "&grade_id%5B%5D={2}" + "&sortField=time" + "&page={3}" + "&_=1492051658109";

    // 解析得到每一题的questionId
    public static void parse(String questionTypeCpde, String gradeCode, String sectionName, String subjectName,
                    Integer page) throws Exception {
        String cookie = HttpHelper.defaultCookie + "xd=" + codeEncStr.get(sectionName) + "chid="
                        + codeEncStr.get(subjectName);
        String fmtUrl = StringUtils.formatter(questionUrl, questionTypeCpde, gradeCode, page);
        String content = HttpHelper.post(fmtUrl, null, cookie, headerMap);
        content.replaceAll("TARGET_URL", LOCAL_PATH);
        Map questionMap = null;
        questionMap = JSON.parseObject(content, Map.class);
        String data = MapUtils.getString(questionMap, "data");

        List<Map> dataArray = JSON.parseArray(data, Map.class);
        if (dataArray == null || dataArray.size() <= 0)
            return;
        Integer total = MapUtils.getInteger(questionMap, "total");
        if (total == null) {
            return;
        }
        Integer totalPage = (int) Math.ceil((total / 10.0));
        Map dataMap = dataArray.get(0);
        List<QuestionVo> questionList = JSON.parseArray(MapUtils.getString(dataMap, "questions"), QuestionVo.class);
        if (questionList != null && questionList.size() > 0) {
            for (QuestionVo question : questionList) {
                String questionId = question.getQuestion_id();

                // 题目和解析现在都到解析页面去获取
                /*
                 * List<QuestionVo> subQuestionList = question.getList(); String[]
                 * subQueId={questionId};
                 * 
                 * //小题 if(subQuestionList!=null&&subQuestionList.size()>0){ subQueId= new
                 * String[subQuestionList.size()]; for (QuestionVo subQuestion : subQuestionList) {
                 * DBUtils.insert(QUESTION_TABLE, JSON.parseObject(JSON.toJSONString(subQuestion),
                 * Map.class),true); int i = 0; subQueId[i] = subQuestion.getQuestion_id(); i++; } }
                 * try { DBUtils.insert(QUESTION_TABLE,
                 * JSON.parseObject(JSON.toJSONString(question), Map.class),true); } catch
                 * (Exception e) { // TODO Auto-generated catch block question.setList(null);
                 * DBUtils.insert(QUESTION_TABLE, JSON.parseObject(JSON.toJSONString(question),
                 * Map.class),true); }
                 */

                // 答案和解析
                parseQuetsionAnalysis(questionId);

            }
        }

        // 解析其他页的题目
        while (--totalPage > 1) {
            try {
                parse(questionTypeCpde, gradeCode, sectionName, subjectName, totalPage);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                break;
            }
        }

    }

    // 解析一题
    public static void parseQuetsionAnalysis(String questionID) {
        String content = HttpHelper.get(StringUtils.formatter(questionDetailPath, questionID), headerMap);
        downImgs(content);
        content = content.replaceAll(TARGET_URL, LOCAL_PATH);
        String questionAnalysisStr = StringUtils.getPattern(content, MockDataTestPaperRegex);
        List<Map> questionList;
        try {
            questionList = JSON.parseArray(questionAnalysisStr, Map.class);
        } catch (Exception e1) {
            System.out.println(content);
            System.out.println(questionAnalysisStr);
            e1.printStackTrace();
            // System.out.println(questionID+"阿里巴巴解析失败");
            return;
        }
        if (questionList != null && questionList.size() > 0) {
            Map question = ((List<Map>) questionList.get(0).get("questions")).get(0);
            QuestionWithAnswerVO questionVo = JSON.parseObject(JSON.toJSONString(question), QuestionWithAnswerVO.class);
            String questionId = questionVo.getQuestion_id();
            List<QuestionWithAnswerVO> subQuestionList = questionVo.getList();
            String[] subQueId = { questionId };

            // 小题
            if (subQuestionList != null && subQuestionList.size() > 0) {
                subQueId = new String[subQuestionList.size()];
                for (QuestionWithAnswerVO subQuestion : subQuestionList) {
                    try {
                        DBUtils.insert(QUESTION_TABLE, JSON.parseObject(JSON.toJSONString(subQuestion), Map.class),
                                        true);
                    } catch (Exception e) {
                        subQuestion.setList(null);
                        DBUtils.insert(QUESTION_TABLE, JSON.parseObject(JSON.toJSONString(subQuestion), Map.class),
                                        true);
                    }
                }
            }
            try {
                DBUtils.insert(QUESTION_TABLE, JSON.parseObject(JSON.toJSONString(questionVo), Map.class), true);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                questionVo.setList(null);
                DBUtils.insert(QUESTION_TABLE, JSON.parseObject(JSON.toJSONString(questionVo), Map.class), true);
            }
        }

    }

    // 下载图片
    public static void downImgs(String content) {
        Pattern pattern = Pattern.compile("data-cke-saved-src=\"([^\"]*)");
        Matcher matcher = pattern.matcher(content);
        List<String> imgs = new ArrayList<String>();
        while (matcher.find()) {
            String res = matcher.group(1);
            if (res.indexOf(TARGET_URL) >= 0) {
                imgs.add(res);
            }
        }
        DownLoadImg down = new DownLoadImg(imgs, TARGET_URL, IMG_PATH);
        down.run();
    }

    @Test
    public void Test() {
        String src = "<!DOCTYPE html><html><head>    <meta charset='utf-8'/>    <meta name='renderer' content='webkit'><!--用在360中-->    <meta name='force-rendering' content='webkit'><!--用在其它-->    <meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'/>        <meta name='viewport' content='width=device-width,initial-scale=1,maximum-scale=1'>    <link rel='shortcut icon' type='image/x-icon' href='/images/favicon.ico' />    <link rel='apple-touch-icon' href='/images/touchicon.png' />    <meta name='csrf-param' content='_csrf'>    <meta name='csrf-token' content='TnFXWnFPYm99IAcNA2IWXDwHPy4pNzoEPAcBaScOCSh6EAIdRwwYAA=='>    <title>给带颜色字选择合适的解释。花：①能开花供观赏的植物②用，耗费③形状像花的东西④虚假的，迷惑人的⑤模糊不清奶奶今年快七十了，但她仍然...</title>    <meta name='keywords' content='' />    <meta name='description' content='' />    <script>        (function(doc, win) {            // 基于 window.screen.width 实现自适应布局            var screenWidth = 0,                size = 'XL',                root = doc.documentElement;            if (window.screen && screen.width) {                screenWidth = screen.width;                if (screenWidth >= 1200) size = 'XL'; // 大屏 - 1200px                         else if (screenWidth < 1200) size = 'XS'; // 小屏 - 1000px            }            root.className += ' ' + size; // 标记CSS            win.SIZE = size; // 标记JS                })(document, window);        window.OT2 = {}; // 全局命名空间        OT2.AboveIE9 = true; // 默认非iE或IE9及以上        OT2.ns = function(name) {            var container = OT2;            var parts = name.split('.');            var current = '';            if (parts[0] == 'OT2') parts.shift();            while (current = parts.shift()) {                if (!container[current]) container[current] = {};                container = container[current];            }            return container;        };                USER = {};//通知js用户的信息                OT2.CSRF = {'_csrf': 'TnFXWnFPYm99IAcNA2IWXDwHPy4pNzoEPAcBaScOCSh6EAIdRwwYAA=='} ;    </script><!--[if lt IE 9]><script> OT2.AboveIE9= false;</script><![endif]-->    <link rel='stylesheet' href='/css/base.min.css?v=3de2db9dcf' />    <link rel='stylesheet' href='/css/spriter-mix.min.css?v=f209370397' />    <link rel='stylesheet' href='/lib/artDialog/css/ui-dialog.css' />    <link rel='stylesheet' href='/css/artDialog-skin-ot2.min.css' />    <link rel='stylesheet' href='/css/main.min.css?v=b52c68b755' />    <link rel='stylesheet' href='/css/popup.min.css?v=52de7bc1f5' />        <script src='/lib/jquery.min.js?v=6edfe6bd30'></script>    <script type='text/javascript'>$.ajaxSetup({cache: false});</script>    <script src='/lib/underscore-min.js'></script>    <script src='/js/cookie.min.js?v=6c6f9dd218'></script>    <script src='/lib/json3.min.js'></script>    <script src='/lib/artDialog/dist/dialog-plus.js'></script>    <script src='/js/artDialog-config.min.js'></script>    <script src='/js/element.min.js?v=52d0a65018'></script>    <script src='/js/localdata.min.js?v=5d22ea43ab'></script>    <script src='/js/util.min.js?v=9caab6e4b8'></script>    </head><body>        <link rel='stylesheet' href='/css/question-analytic.min.css?v=7ba8edf7cd' /><!--顶部导航开始--><div class='topbox'>    <div class='topinner g-container'>        <style>    .user-news { position: absolute; top: 40px; right: 0; background: #fefefe; width: 260px; min-height: 112px;  line-height: 24px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; box-shadow: 0 0 10px #dcdcdc; display: none; }    .user-news-hd { border-bottom: 1px solid #efefef; padding-bottom: 4px; margin: 6px 10px; font-size: 12px; color:#666; }    .user-news .icon { display: block; top: -8px; right: 40px; position: absolute; }    .user-news-close { position: absolute; right: 10px; top: 10px; width: 16px; height: 16px; }    .user-news-close img { width: 100%; height: 100%;}    .vip-overtip {text-align: center; }    .vip-overtip { padding-top: 6px;}    .vip-overtip p strong { color: red; margin:0 3px; }    .vip-overtip .tip-notice { color: red; font-size: 16px; font-weight: bold; }    .vip-overtip a { color: #52c684; }</style><script>function user_news() {    var remain = -1; // 剩余到期天数    var $usernews = $('.user-news');    var p = $usernews.parent();    var h = p.height();    p.css('position', 'relative');    $usernews.css('top', h + 'px');    var vip_overtip_close = OT2.Cookie.get('vip_overtip_date');    if (remain == 0 && vip_overtip_close == 'end') {        OT2.Cookie.set('vip_overtip_date', 'end', new Date(new Date().getTime() + 30 * 24 * 3600 * 1000)); // 更新cookie有效期        return false; // 结束不显示    }    var d = new Date();    var v = [d.getFullYear(), d.getMonth(), d.getDate()].join('/');    if (!vip_overtip_close || vip_overtip_close != v) $usernews.show();    $usernews.find('.user-news-close').on('click', function() {        $usernews.hide();        if (remain == 0) {            OT2.Cookie.set('vip_overtip_date', 'end', new Date(new Date().getTime() + 30 * 24 * 3600 * 1000));            return false;        }        OT2.Cookie.set('vip_overtip_date', v, new Date(new Date().getTime() + 24 * 3600 * 1000));    });}</script>        <div class='slogan'>            <a href='#'>二一教育旗下产品</a>        </div>                <div class='webhelp'>            <!--<a href='home.html' class='help'><i class='icona-home'></i>网站首页</a>-->            <!--<span class='split'></span>-->            <a href='http://oldzujuan.21cnjy.com/' target='_blank'>【返回旧版】</a>            <span class='split'></span>            <a href='/help' class='help' target='_blank'><i class='icona-help'></i>帮助中心</a>            <span class='split'></span>            <span class='topbar-dropmap'>                <a href='javascript:void(0);'><i class='icona-buy'></i>购买服务</a>                <div class='drop-bd'>                    <ul>                        <li><a  onclick='return OT2.Global.initLogin();'  >VIP服务</a></li>                        <li><a href='/help/request' target='_blank'>组卷通服务</a></li>                      <li><a  onclick='return OT2.Global.initLogin();'  >激活VIP</a></li>                    </ul>                    <span class='icona-dia-tri'></span>                </div>            </span>            <span class='split'></span>            <span class='topbar-dropmap'>                <a href='javascript:;'><i class='icona-qixia'></i>旗下站点</a>                <div class='drop-bd'>                    <ul>                        <li><a target='_blank' href='http://www.21cnjy.com/zhitongche/'>校网通</a></li>                        <li><a target='_blank' href='http://zujuan.21cnjy.com/'>在线组卷</a></li>                        <li><a target='_blank' href='http://www.jbzyk.com/'>校本资源库</a></li>                        <li><a target='_blank' href='http://tiku.21cnjy.com/'>在线题库</a></li>                        <li><a target='_blank' href='http://www.21cnjy.com/video.php'>名师课堂</a></li>                        <li><a target='_blank' href='http://www.21cnjy.com/productshow/index.php?prod=school'>数字化校园</a></li>                        <li><a target='_blank' href='http://www.21cnjy.com/productshow/index.php?prod=yun'>区域云平台</a></li>                        <li><a target='_blank' href='http://www.21cnjy.com/productshow/app/'>二一教育APP</a></li>                    </ul>                    <span class='icona-dia-tri'></span>                </div>            </span>                        <span class='split'></span>            <div class='loginbox'>                            <a shref='/login' class='login' onclick='OT2.Global.initLogin(); return false;'><span>登录</span></a>                            <!--a href='/regist' class='register'><span>注册</span></a-->                                <a href='http://passport.21cnjy.com/site/register?jump_url=http://zujuan.21cnjy.com/question/detail/2976565' class='register'><span>注册</span></a>            </div>                        <span class='split'></span>        </div>    </div></div><!--顶部导航结束--><!--头部搜索部分开始--><div class='top-middle'>    <div class='top-minner g-container f-cb'>        <div class='logobox'>            <a href='/?1=1'><img src='/images/test_logo.png' alt='二一教育在线组卷平台' title='在线组卷平台' /></a>        </div>        <div class='g-mn'>            <div class='searchbox'>                <form id='search-form' action='/question/search' method='get'>                    <div class='search-text' id='J_SearchMenu'>                        <span class='text-select'><em class='J_tit'>试题</em><i class='icona-tri'></i></span>                        <p class='select-items'>                            <a href='javascript:;'><input type='radio' name='' value='/question/search' class='f-dn' checked><span>试题</span></a>                            <a href='javascript:;'><input type='radio' name='' value='/paper/search' class='f-dn'><span>试卷</span></a>                        </p>                    </div>                    <div class='search-inputbox'>                        <input type='text' name='content' value='' class='search-input' placeholder='请输入关键词搜索' />                    </div>                    <div class='search-btn'>                        <button class='btn' type='submit'><i class='icona-search'></i></button>                    </div>                </form>                <script>                $(function(){                    var name = $('.nav-items a.item span').html();                    $('input[name='content']').attr({                        'placeholder':'请输入关键词搜索 '+name+' 资源'                    });                })                </script>            </div>        </div>        <!--<div class='contactbox'>            <p class='qq-qun'>QQ交流群：<strong>142401456</strong></p>        </div>-->        <a  onclick='return OT2.Global.initLogin()'  class='Govip-btn'>            <img src='/images/topic/vip-btn.png' alt=''>        </a>    </div></div><!--头部搜索部分结束--><!--头部导航部分开始--><script>OT2.xd_chid = {'chid':'2','xd':'1'} || null;</script><div class='top-navbox'>    <div class='top-nav g-container'>        <div class='nav-items'>            <a href='#' class='item'>当前：<span>小学语文</span><i class='icona-tri2'></i></a>            <div class='item-list'>                                <div class='list-xx'>                 <h3>小学</h3>                                        <a href='/question/detail?chid=2&amp;xd=1&amp;tree_type=knowledge'>语文</a>                                        <a href='/question/detail?chid=3&amp;xd=1&amp;tree_type=knowledge'>数学</a>                                        <a href='/question/detail?chid=4&amp;xd=1&amp;tree_type=knowledge'>英语</a>                                        <a href='/question/detail?chid=5&amp;xd=1&amp;tree_type=knowledge'>科学</a>                                        <a href='/question/detail?chid=9&amp;xd=1&amp;tree_type=knowledge'>政治思品</a>                                    </div>                                <div class='list-cz'>                 <h3>初中</h3>                                        <a href='/question/detail?chid=2&amp;xd=2&amp;tree_type=knowledge'>语文</a>                                        <a href='/question/detail?chid=3&amp;xd=2&amp;tree_type=knowledge'>数学</a>                                        <a href='/question/detail?chid=4&amp;xd=2&amp;tree_type=knowledge'>英语</a>                                        <a href='/question/detail?chid=5&amp;xd=2&amp;tree_type=knowledge'>科学</a>                                        <a href='/question/detail?chid=6&amp;xd=2&amp;tree_type=knowledge'>物理</a>                                        <a href='/question/detail?chid=7&amp;xd=2&amp;tree_type=knowledge'>化学</a>                                        <a href='/question/detail?chid=8&amp;xd=2&amp;tree_type=knowledge'>历史</a>                                        <a href='/question/detail?chid=9&amp;xd=2&amp;tree_type=knowledge'>政治思品</a>                                        <a href='/question/detail?chid=10&amp;xd=2&amp;tree_type=knowledge'>地理</a>                                        <a href='/question/detail?chid=20&amp;xd=2&amp;tree_type=knowledge'>历史与社会</a>                                        <a href='/question/detail?chid=11&amp;xd=2&amp;tree_type=knowledge'>生物</a>                                    </div>                                <div class='list-gz'>                 <h3>高中</h3>                                        <a href='/question/detail?chid=2&amp;xd=3&amp;tree_type=knowledge'>语文</a>                                        <a href='/question/detail?chid=3&amp;xd=3&amp;tree_type=knowledge'>数学</a>                                        <a href='/question/detail?chid=4&amp;xd=3&amp;tree_type=knowledge'>英语</a>                                        <a href='/question/detail?chid=6&amp;xd=3&amp;tree_type=knowledge'>物理</a>                                        <a href='/question/detail?chid=7&amp;xd=3&amp;tree_type=knowledge'>化学</a>                                        <a href='/question/detail?chid=8&amp;xd=3&amp;tree_type=knowledge'>历史</a>                                        <a href='/question/detail?chid=9&amp;xd=3&amp;tree_type=knowledge'>政治思品</a>                                        <a href='/question/detail?chid=10&amp;xd=3&amp;tree_type=knowledge'>地理</a>                                        <a href='/question/detail?chid=11&amp;xd=3&amp;tree_type=knowledge'>生物</a>                                    </div>                            </div>        </div>        <div class='nav-list'>            <ul>                <li>                    <a class='' href='/'>首页</a>                </li>                <li class='complex-nav-item'>                     <a href='javascript:;' class='active' >手动组卷</a>                        <div class='dd-list'>                            <a href='/question?tree_type=category&chid=2&xd=1' ><i>章节同步选题</i></a>                            <a href='/question?tree_type=knowledge&chid=2&xd=1' ><i>知识点选题</i></a>                        </div>                </li>                <li class='complex-nav-item'>                    <a href='javascript:;' class='' >智能组卷</a>                    <div class='dd-list'>                        <a href='/smarter?tree_type=category'><i>章节智能组卷</i></a>                        <a href='/smarter?tree_type=knowledge'><i>知识点智能组卷</i></a>                        <a  onclick='return OT2.Global.initLogin();' ><i>双向细目表组卷</i></a>                    </div>                </li>                <li>                    <a class='' href='/paper'>试卷库</a>                </li>                <li>                    <a class='' href='/subject'>专题特供</a>                </li>            </ul>        </div>    </div></div><!--头部导航部分结束--><!--头部结束--><div class='breadcrumb g-container'>    <i class='icona-dingwei'></i>当前位置：            <a href='/'>首页</a>        <b>&gt;</b>    <a href='/question?chid=2&amp;xd=1'>小学语文</a>    </div><script>// 试题解析页面：试题篮对应试题学段学科OT2.xd_chid = {'chid':'2','xd':'1'} || null;</script><div>    </div><div class='search-list g-container'>    <div class=' f-fl f-w'>           <div class='dj-preview'>            <div id='J_QuestionList' class='f-usn' onselectstart='return false' oncontextmenu='return false'></div>                        <div class='keen'>                <div class='keen-title'>                    <span>举一反三</span>                </div>                <div class='keen-con'>                                        <div class='item'>                        <i class='icona-ellipsegray'></i>                        <div><a href='/question/detail/1409487'>想一想，写一写<br/>霄&nbsp; <u>九霄</u><br/>消&nbsp; <u>消息</u><br/>悔 ________&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; 烛 ________&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 偷 ________&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;晓 ________<br/>________&nbsp;&nbsp;&nbsp;________&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ________&nbsp; ________&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 　 　________&nbsp; ________&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;　________&nbsp;________</a></div>                    </div>                                        <div class='item'>                        <i class='icona-ellipsegray'></i>                        <div><a href='/question/detail/1489970'>同音字组词<br/>识________&nbsp; ________&nbsp; 科________&nbsp; ________&nbsp;&nbsp; 时________&nbsp; ________&nbsp; 颗________&nbsp; ________</a></div>                    </div>                                        <div class='item'>                        <i class='icona-ellipsegray'></i>                        <div><a href='/question/detail/1493555'>读课文，填一填。<br/>一个________&nbsp;一个________一个________&nbsp;&nbsp;&nbsp;</a></div>                    </div>                                        <div class='item'>                        <i class='icona-ellipsegray'></i>                        <div><a href='/question/detail/1587292'>加上部首，再组词<br/>①昔----________&nbsp;&nbsp; ________<br/>②良----________&nbsp; &nbsp;________<br/>③争----________ &nbsp;&nbsp;________​<br/><div data-cke-hidden-sel='1' data-cke-temp='1' style='position:fixed;top:0;left:-1000px'><br/></div></a></div>                    </div>                                        <div class='item'>                        <i class='icona-ellipsegray'></i>                        <div><a href='/question/detail/2525140'>选择带下划线字词的正确意义。<br/>春风又绿<u>江</u>南岸。_____</a></div>                    </div>                                        <div class='item'>                        <i class='icona-ellipsegray'></i>                        <div><a href='/question/detail/2996414'>比一比，组成词语。<table cellspacing='0' cellpadding='0' border='0'><tbody><tr><td width='109' valign='top'>消________</td><td width='109' valign='top'>胧________</td><td width='109' valign='top'>峰________</td></tr><tr><td width='109' valign='top'>梢________</td><td width='109' valign='top'>拢________</td><td width='109' valign='top'>锋________</td></tr></tbody></table></a></div>                    </div>                                    </div>            </div>                    </div>    </div>  <div class='dj-about'>      <div class='dj-img'>                <a href='/topic/vip' target='_blank'>                    <img src='/images/vip-act.png' alt=''>                </a>     </div>      <div class='dj-detail'>         <h1>相关试卷</h1>           <ul>                                    <li>                        <a target='_blank' href='/paper/view/147154'                           title='人教版(新课程标准）小学语文三年级下册 期末检测卷'>人教版(新课程标准）小学语文三年级下册 期末检测卷</a>                    </li>                                    <li>                        <a target='_blank' href='/paper/view/147153'                           title='人教版(新课程标准）小学语文三年级下册第八单元检测卷'>人教版(新课程标准）小学语文三年级下册第八单元检测卷</a>                    </li>                                    <li>                        <a target='_blank' href='/paper/view/147147'                           title='人教版(新课程标准）小学语文三年级下册第七单元检测卷'>人教版(新课程标准）小学语文三年级下册第七单元检测卷</a>                    </li>                                    <li>                        <a target='_blank' href='/paper/view/147143'                           title='人教版（新课程标准）小学语文三年级下册第五单元检测卷'>人教版（新课程标准）小学语文三年级下册第五单元检测卷</a>                    </li>                                    <li>                        <a target='_blank' href='/paper/view/146836'                           title='人教版（新课程标准）小学语文三年级下册第一单元检测卷'>人教版（新课程标准）小学语文三年级下册第一单元检测卷</a>                    </li>                         </ul>       </div>                      <div id='kt5u'>             </div>          </div></div><!--试题篮--><div class='basket active' id='J_Basket'>    <div class='basket-tit'>        <p><i class='icona-gouwulan'></i><em>试题篮</em></p>        <span><i class='icona-gouwuleft'></i></span>    </div>    <div class='basket-con'>        <div class='basket-count'>            <script type='text/template' data-template='basketList'>                <div class='basket-head'>                共计：（<span><%= num %></span>）道题                </div>                <div class='baskrt-list'>                <% _.each(list, function(v) { %>                <p title='<%= v.key %>'><%= (v.key.length > 5) ? (v.key.substr(0, 4)+'...') : v.key %>：<span><%= v.val.length %></span>道<i class='icona-del1 f-fr' onclick='basket.removeAll('<%= v.key %>', <%= v.val.join(',') %>)'></i></p>                <% }) %>                </div>            </script>        </div>        <div class='basket-foot'>            <a id='to-paper-admin-edit' data-method='post' class='basket-btn' href='/paper/admin-edit' style='display: none'>编辑</a>            <a id='to-paper-edit' data-method='post' class='basket-btn' href='/paper/edit' style='display: none'>生成试卷</a>            <a id='to-paper-admin-cancel' class='basket-btn' href='/question' style='display: none'>取消</a>        </div>    </div></div><script>    $(function () {        var cachePaper = null;        try {            cachePaper = JSON3.parse(OT2.LocalData.get('basket_cachePid'));        }        catch (e) {        }        var admin_url = '/paper/admin-edit';        if(cachePaper){            var pid = cachePaper.pid;            admin_url += '?pid='+pid+'&clean=0';            $('#to-paper-admin-edit').show().attr({href:admin_url});            $('#to-paper-admin-cancel').show().click(function () {                OT2.LocalData.remove(cachePaper.paper_key);                OT2.LocalData.remove('basket_cacheObj');                OT2.LocalData.remove('basket_cachePid');            });            $('.basket-tit').css({background: '#ffb03d none repeat scroll 0 0'});        }else{            $('#to-paper-edit').show();        }        $('.basket-foot a[data-method='post']').click(function(){            if(typeof USER.uid === 'undefined'){                OT2.Global.initLogin();                return false;            }            var cacheObj = {};            try {                cacheObj = JSON3.parse(OT2.LocalData.get('basket_cacheObj')) || {};            }            catch (e) {            }            var qids = [];            _.each(cacheObj, function (v) {                if (v.xd == OT2.xd_chid.xd && v.xk == OT2.xd_chid.chid) {                    qids.push(v.id);                }            });            if (typeof USER.basketLimit === 'undefined') USER.basketLimit = 30; // 未登录默认三十条            var len = qids.length;            if (len == 0) {                OT2.Util.alert('试题篮是空的，请添加试题！');                return false;            }            if(len > USER.basketLimit){                OT2.Util.alert('试题篮试题数量超过限制');                return false;            }            $(this).attr({                'data-params': JSON3.stringify({                    'qids':qids                })            });            //alert(0);            return true;        });    })</script><script type='text/template' data-template='question-item'><li data-qid='<%= id %>'>    <div class='search-exam dj-search-exam'>        <div class='exam-head'>            <p class='exam-head-left'>                <span>题型：<%= type %></span><i class='line'></i>                <span>题类：<%= exam_type %></span><i class='line'></i>                <span>难易度：<%= difficult_index %></span>            </p>                         <p class='exam-head-right'>               <span><%= question_from %></span>                   </p>        </div>        <div class='exam-foot'>            <p class='exam-foot-left'>                <a href='javascript:;' onclick='OT2.QCollect(this, <%= id %> )'><i class='<% if (is_collect == '收藏') { %>icona-shoucang<% } else { %>icona-quxiaosc<% } %>'></i><%= is_collect %></a>                <a href='javascript:;' onclick='new OT2.ErrorReport( <%= id %> )'><i class='icona-jiucuo'></i>纠错</a>            </p>            <p class='exam-foot-right'>                <span>组卷次数：<%= save_num %>次</span>                <% if (!added) { %>                <a class='addbtn J_AddQuestion' href='javascript:;'><b>+</b>选题</a>                <%  } else { %>                <a class='removebtn J_AddQuestion' href='javascript:;'><b>-</b>移除</a>                <% } %>            </p>        </div>    </div></li></script><script type='text/template' data-template='question-txt'>       <div class='exam-con'>      <% if (question_type != '7' && parent_id != '0') { %>           <div class='analyticbox'>               <div class='subexam-head'><span>（<%= index+1 %>）、</span><%= question_text %></div>              <% if ('12'.indexOf(question_type) > -1) { %>                   <div class='exam-s'>                        <% _.each(options, function(v, k) { %>                      <span class='op-item'><span class='op-item-nut'><%= k %>、</span><span class='op-item-meat'><%= v %></span></span>                       <% }) %>                    </div>              <% } %>             <% if (question_type == '6') { %>                   <% _.each(options, function(v, k) { %>                  <div class='exam-s'>                        <% _.each(v, function(val, key) { %>                        <span class='op-item'><span class='op-item-nut'><%= key %>、</span><span class='op-item-meat'><%= val %></span></span>                       <% }) %>                    </div>                  <% }) %>                <% } %>                                         </div>      <% } else { %>          <div class='exam-q'>            <%= question_text %>            </div>          <% if ('12'.indexOf(question_type) > -1) { %>               <div class='exam-s'>                    <% _.each(options, function(v, k) { %>                  <span class='op-item'><span class='op-item-nut'><%= k %>、</span><span class='op-item-meat'><%= v %></span></span>                   <% }) %>                </div>          <% } %>         <% if (question_type == '6') { %>               <% _.each(options, function(v, k) { %>              <div class='f-cb'>                  <div class='exam-mynum'>（<%= k+1 %>）</div>                  <div class='w'>                     <div class='exam-s exam-sw'>                            <% _.each(v, function(val, key) { %>                            <span class='op-item'><span class='op-item-nut'><%= key %>、</span><span class='op-item-meat'><%= val %></span></span>                           <% }) %>                        </div>                  </div>              </div>              <% }) %>            <% } %>     <% } %>     <% if (question_type == '7') { %>       <div class='exam-qlist'></div>      <% } %> </div></script><script type='text/template' data-template='errorReport'>    <div class='error-con'> <form action=''>        <input type='hidden' name='qid' value='<%= qid %>' />       <p>*请输入您遇到的错误，错误一经确认，我们会给予您一定的奖励</p>        <textarea name='message'></textarea>        <div class='warn'></div>    </form></div></script><script type='text/x-template-underscore' data-template='exam-point'><% if (q.knowledge) { %><div class='analyticbox'>    <span class='exam-point'>【考点】</span>    <div class='analyticbox-body'>        <% if (q.knowledge) { %>            <%= _.pluck(q.knowledge, 'name').join('；') %>        <% } else { %>            &nbsp; &nbsp;        <% } %>    </div></div><% } %></script><script type='text/x-template-underscore' data-template='exam-answer'><% if (('46'.indexOf(q.question_type) > -1 && q.answer_json) ||         ('1235'.indexOf(q.question_type) > -1 && q.answer)) { %><div class='analyticbox'>    <span class='exam-answer'>【答案】</span>    <div class='analyticbox-body'>    <%         var answer_html = null;        if (q.question_type == '4') {            answer_html = _.map(q.answer_json, function(v, k) {                return '【第' + (k+1) + '空】' + v.toString();            }).join('<br>');        }        else if (q.question_type == '6') {            answer_html = _.map(q.answer_json, function(v, k) {                return '<span>（' + (k+1) + '）、' + v.join('') + '</span>';            }).join('');        }         else if(q.question_type == '3'){            answer_html = q.answer == 'A' ? '正确':'错误';        }        else {            answer_html = q.answer;        }    %>    <%= answer_html %>    </div></div><% } %></script><script type='text/x-template-underscore' data-template='exam-explanation'><% if (1) { %>    <% if (typeof q.explanation !== 'undefined') { %>        <% if(q.explanation) { %>    <div class='analyticbox analyticbox1'>         <span class='exam-analytic'>【解析】</span>         <div class='analyticbox-body'>            <%= q.explanation %> &nbsp;&nbsp;        </div>    </div>     <% } %>    <% } else { %><div class='analyticbox-tips'>抱歉，在当前页面查看解析的功能仅限于“组卷通”用户！<a href='/help/request' target='_blank'>用户升级</a></div>    <% } %><% } else { %><div class='analyticbox-tips'>抱歉，您未登录！暂时无法查看答案与解析，<a href='/login' onclick='OT2.Global.initLogin(); return false;'>点击登录</a></div><% } %></script><script type='text/javascript' src='/site/get-parameters'></script><script src='/js/error-report.min.js?v=351ce87893'></script><script src='/js/question-txt.min.js?v=6bddbe25e7'></script><script src='/js/question.min.js?v=e96038bdb7'></script><script src='/js/q-list.min.js?v=099058b8b5'></script><script>    $(document.body).addClass('f-usn').on('selectstart', function() { return false; }).on('contextmenu', function() { return false; });    var MockDataTestPaper = [{'head_title':'题目','questions':[{'question_id':'2976565','qsn_id':null,'question_type':'4','question_channel_type':'4','question_status':'2','question_tags':'北师大版 语文 二年级上册 《绒毛小熊》','chid':'2','xd':'1','create_by':'小婷同学','create_uid':'7026178','create_date':'2016-11-17 04:25:43','update_by':'朱玉婕','update_uid':'7169407','update_date':'2016-11-17 16:51:06','update_reason':'','audit_by':'','audit_uid':'0','audit_date':null,'audit2_by':'lizhenju21','audit2_uid':'6398809','audit2_date':'2016-11-17 16:51:06','opinion':'','parent_id':'0','sort':1,'is_objective':'1','difficult_index':'1','master_level':'2','exam_type':'2','evaluated':'0','region_ids':'0','grade_id':'0','question_source':'北师大版语文二年级上册《绒毛小熊》同步练习','mode':'3','is_delete':'0','md5sum':'7c30d77f28e6d29a6041da11a3e02e85','title':'给带颜色字选择合适的解释。花：①能开花供观赏的植物②用，耗费③形状像花的东西④虚假的，迷惑人的⑤模糊不清奶奶今年快七十了，但她仍然...','kid_num':'2','paperid':'78558','save_num':'4','oldqid':'0','paper':{'pid':'78558','title':'北师大版语文二年级上册《绒毛小熊》同步练习'},'question_text':'给带颜色字选择合适的解释。<br/>花：①能开花供观赏的植物<br/>②用，耗费<br/>③形状像花的东西<br/>④虚假的，迷惑人的<br/>⑤模糊不清<br/>奶奶今年快七十了，但她仍然头不昏眼不<u>花</u>。&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type=\'text\' placeholder=\'1\' ms-duplex-string =\'question.myanswer[1]\' class=\'cke_questions_blankInput\' /><br/>秋游的时候，同学们<u>花</u>钱真厉害。&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type=\'text\' placeholder=\'2\' ms-duplex-string =\'question.myanswer[2]\' class=\'cke_questions_blankInput\' /><br/>狼想用<u>花</u>言巧语迷惑老山羊，但老山羊没有上当。&nbsp;&nbsp; <input type=\'text\' placeholder=\'3\' ms-duplex-string =\'question.myanswer[3]\' class=\'cke_questions_blankInput\' /><br/>春暖<u>花</u>开，我们去踏青。&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type=\'text\' placeholder=\'4\' ms-duplex-string =\'question.myanswer[4]\' class=\'cke_questions_blankInput\' /><br/>海上的浪<u>花</u>真美！&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type=\'text\' placeholder=\'5\' ms-duplex-string =\'question.myanswer[5]\' class=\'cke_questions_blankInput\' />','options':'','answer':'[\'<p>\\u2464<\\/p>\',\'<p>\\u2461<\\/p>\',\'<p>\\u2463<\\/p>\',\'<p>\\u2460<\\/p>\',\'<p>\\u2462<\\/p>\']','answer_json':['⑤','②','④','①','③'],'explanation':'','pic_answer':'1','pic_explanation':'1','knowledge':{'21282':{'kid':'21282','name':'绒毛小熊'},'4605':{'kid':'4605','name':'字义'}},'category':null,'is_collect':false,'done':false,'myanswer':{'1':'','2':'','3':'','4':'','5':''},'score':{'score':5},'sort2':1}],'scores':[]}] ;    OT2.renderQList(MockDataTestPaper,true);    $('.search-exam').children('.exam-con').off();    basket.publish('show-analyticbox', true);</script><script>// 页面js校本编写var kidstr = '21282,4605';if(kidstr!='0'){    $('#kt5u').load('/question/get-kt5u-data?kid='+kidstr);}</script>    <!--footer开始--><div class='footer'>    <div class='footer-con g-container'>        <div class='footer-top f-cb'>            <div class='con-gs'>                <h3>公司介绍</h3>                <p>Company Introduction</p>                <ul>                    <li><i class='icona-ellipse'></i><a href='http://www.21cnjy.com/about/about_about.php' target='_blank'>公司简介</a></li>                    <li><i class='icona-ellipse'></i><a href='/help/copyright' target='_blank'>版权声明</a></li>                    <li><i class='icona-ellipse'></i><a href='http://www.21cnjy.com/about/about_company-news.php' target='_blank'>公司动态</a></li>                    <li><i class='icona-ellipse'></i><a href='http://www.21cnjy.com/about/about_opinion.php' target='_blank'>意见反馈</a></li>                </ul>            </div>            <div class='con-fw'>                <h3>服务介绍</h3>                <p>Service Introduction</p>                <ul class='fw-list'>                    <li><i class='icona-ellipse'></i><a href='/help/zujuan' target='_blank'>组卷通服务</a></li>                    <li><i class='icona-ellipse'></i><a href='/help/vip' target='_blank'>VIP会员服务</a></li>                    <li><i class='icona-ellipse'></i><a href='/help/diy' target='_blank'>学校定制化服务</a></li>                </ul>            </div>            <div class='con-bz'>                <h3>帮助中心</h3>                <p>Help center</p>                <ul>                    <li><i class='icona-ellipse'></i><a href='/help/demo' target='_blank'>视频帮助</a></li>                    <li><i class='icona-ellipse'></i><a href='/help/faq' target='_blank'>常见问题</a></li>                    <li><i class='icona-ellipse'></i><a href='/help/makepaper' target='_blank'>如何组卷</a></li>                                      <li><i class='icona-ellipse'></i><a href='/help/download' target='_blank'>下载试卷</a></li>                    <li><i class='icona-ellipse'></i><a href='/help/test' target='_blank'>在线测试</a></li><li><i class='icona-ellipse'></i><a href='/new' target='_blank'>最新试题</a></li>                </ul>            </div>            <div class='con-ewm'>                <img src='/images/ewm.png' style='width:80px' alt='二一教育APP'/>                <p>二一教育APP</p>            </div>            <div class='con-lx'>                <i class='icona-phone'></i>                <div class='lx-txt'>                    <p class='lx-txt-p1'>400-637-9991</p>                    <p class='lx-txt-p2'>周一至周五  8:30-17:30</p>                </div>                <div class='lx-teacher'>                    <a href='javascript:;' id='jBizQQWPA'><i class='icona-kefu'></i>在线咨询客服</a>                </div>            </div>        </div>        <div class='footer-bottom'>                        <p class='copyright'>2006-2016 二一教育股份有限公司<a href='http://www.miibeian.gov.cn/' class='copy'>粤ICP备11039084号</a> 粤教信息(2013)2号</p>            <p class='footer-adress'>                <span>邮编：518000</span>                <span>地址：深圳市龙岗区横岗街道深峰路3号启航商务大厦5楼5M</span>            </p>        </div>        </div>  </div><style>.system-upgrade  .system-upgrade-wall, .system-upgrade  .pop-wrap { display: block; }.system-upgrade-wall { position: fixed; top:0; right:0; left:0; bottom:0; background:#000; opacity: 0.3; filter:alpha(opacity=30); z-index: 10000; display: none; }.pop-wrap { width: 600px; background-color: #fff;  position: absolute; top: 60px;  left: 50%; margin-left: -300px; z-index: 12000; font-size: 14px; display: none; }.update span:first-child { margin-right: 30px; }.pop-banner img { display: block; }.pop-banner { position: relative; }.produce { position: absolute; top: -58px; left: 87px; }.pop-banner>span { cursor: pointer; display: block; width: 36px; height: 36px; position: absolute; background: url(/images/upgrade/close.png); right: -15px; top: -16px;}.main-wrap { padding: 20px 24px 45px 24px; max-height: 300px; overflow: auto; }.pop-icon { width: 4px; height: 16px; background-color: #52c684; float: left; margin-right: 10px; }.pop-title>h1 { font-size: 16px; float: left; }.pop-detail { clear: both; }#J_NoticeModal .pop-content { margin-top: 20px; text-align: left; margin:0; }.pop-detail li { line-height: 25px; }.pop-detail li a {  color: #52c684; }.pop-detail li a:hover { text-decoration:underline; }.pop-detail { padding-top: 15px;}.pop-title { overflow: hidden; margin-top: 10px; }#J_NoticeModal .pop-content h1 { margin-bottom: 0; font-size: 16px; font-weight: bold; }</style><div class='system-upgrade-wall'></div><div class='pop-wrap' id='J_NoticeModal'>    <div class='pop-banner'>        <img src='/images/upgrade/pop-banner.png' alt='二一组卷2.1.0.1更新公告'>        <img src='/images/upgrade/produce.png' alt='二一组卷2.1.0.1更新公告' class='produce'>        <span class='icon-close'></span>    </div>    <div class='main-wrap'>        <div class='update'>            <span>更新版本：2.1.0.1</span>            <span>更新时间：2017年3月31日</span>        </div>        <div class='pop-content'>            <div class='pop-title'>                <div class='pop-icon'></div>                <h1>一、双向细目表组卷</h1>            </div>            <ul class='pop-detail'>                <li>1、新增双向细目表组卷功能，教师可以按照考试需要，自主创建双向细目表进行组卷。</li>                <li>2、新增系统推荐细目表，教师可选择推荐的细目表，一键出题，方便且精准。</li>                <li>3、新增细目表分析功能，教师在编辑细目表的同时，可以实时查看细目表的详细情况。</li>                <li>                    <a href='http://zujuan.21cnjy.com/help/notice?id=41' target='_blank'>                    点击此处，查看双向细目表组卷使用说明>>                    </a>                </li>            </ul>        </div>        <div class='pop-content'>            <div class='pop-title'>                <div class='pop-icon'></div>                <h1>二、其他</h1>            </div>            <ul class='pop-detail'>                <li>1、新增了当前页面查看试题解析的功能。</li>                <li>2、优化了组卷VIP，购买时需绑定VIP的学科。</li>                <li>3、用户中心增加了下载记录的查询功能。</li>                <li>4、优化了试题与试卷的关键字搜索功能，解决了搜索出来的试题偏少的问题，同时保留了组卷功能。</li>                <li>5、优化了智能组卷的排版，使信息展示更加清楚直观。</li>            </ul>        </div>    </div></div><script>$(function() {    var SystemUpgrade = {        show_notice: function() {            var h = $(window).height(); // 显示器屏幕高度            var h1 = this.$el.height();            if (h >= h1) {                this.$el.css({'top': ((h-h1+60)/2) + 'px', 'position': 'fixed'});            }            else {                this.$el.css({'position': 'absolute'});            }        },        isClosed: function() {            return OT2.LocalData.get('close_system_upgrade_notice');        },        clear_notice: function() {            this.$el.remove();            $('.system-upgrade-wall').remove();            OT2.LocalData.set('close_system_upgrade_notice', 1); // 彻底关闭升级提示        },        init: function() {            var self = this;            this.$el = $('#J_NoticeModal');            this.$doc = $(document.documentElement);            this.$doc.addClass('system-upgrade');            this.$el.on('click', '.icon-close', function() {                self.clear_notice();            });            this.show_notice();            $(window).resize(function() {                self.show_notice();            });        }    };    if (!SystemUpgrade.isClosed()) SystemUpgrade.init();});</script><!--footer结束--><div id='Login_Pal' style='display: none'>    <link rel='stylesheet' href='/css/reg.min.css?v=c8a2fa51d7' />    <style>        .reg-form { z-index: 1110; left: 50%; margin-left: -180px; top: 0;}        .reg-mask { position: fixed; left:0; top:0; bottom:0; right:0; background: #000; opacity: 0.5; filter: alpha(opacity=50); z-index: 1000; }        .reg-form .btn-close { position: absolute; right:16px; top: 16px; display: block; text-indent: -9999px; width: 24px; height: 24px; background: url(/images/close_24.png) no-repeat center center; }        .reg-form .btn-close:hover { opacity: 0.8;}     </style>    <div class='reg-mask'></div>    <div class='reg-form'>        <ul>            <li>                <a href='javascript:;' class='reg-active'>登录</a>            </li>        </ul>        <a class='btn-close J_CloseForm'>x</a>        <form id='J_LoginForm' class='reg-form-detail' action='/login' method='post'><input type='hidden' name='_csrf' value='TnFXWnFPYm99IAcNA2IWXDwHPy4pNzoEPAcBaScOCSh6EAIdRwwYAA=='>        <div class='reg-form-input'>            <label for='account' class='placeholder'>请输入网站账号/手机号码/邮箱</label>            <input type='text' name='LoginForm[username]' id='account' placeholder='请输入网站账号/手机号码/邮箱'>            <div class='fm-warn'><p></p><i></i></div>        </div>        <div class='reg-form-input'>            <label for='password' class='placeholder'>请输入密码</label>            <input type='password' name='LoginForm[password]' id='password' placeholder='请输入密码'>            <div class='fm-warn'><p></p><i></i></div>        </div>        <div class='login-line'>            <div class='login-auto'>                <span class='checkbox checked f-usn' onselectstart='return false'><i class='icona-check'></i><input type='checkbox' checked='' value='1' class='f-dn' name='LoginForm[rememberMe]'>自动登录</span>            </div>            <div class='login-pwd'>                <a href='http://passport.21cnjy.com/site/password-find?jump_url=http://zujuan.21cnjy.com/question/detail/2976565' target='_blank'>忘记密码</a>            </div>        </div>        <div class='reg-btn'><button type='submit'>登录</button></div>        </form>        <div class='login-others'>            <div class='login-others-method'>其它登录方式：                <a href='http://passport.21cnjy.com/site/show-qr-code?jump_url=http://zujuan.21cnjy.com/question/detail/2976565'><i class='icona-wxloginbtn'> </i></a>               <a href='http://21cnjy.com/qqconnect?jump_url=http://zujuan.21cnjy.com/question/detail/2976565'><i class='icona-qqloginbtn'> </i></a>            </div>            <div class='login-reg'><a href='http://passport.21cnjy.com/site/register?jump_url=http://zujuan.21cnjy.com/question/detail/2976565'>免费注册<i class='icona-right-arrow'></i></a></div>        </div>    </div></div><script src='/js/element.min.js?v=52d0a65018'></script><script src='/js/field.min.js?v=e38095a6b6'></script><script src='/js/user.min.js?v=01fdcf63f3'></script>    <script src='/js/global.min.js?v=6cbd974ec0'></script>        <script src='http://wpa.b.qq.com/cgi/wpa.php' type='text/javascript'></script><script>    BizQQWPA.addCustom({aty: '0', a: '0', nameAccount: 4006379991, selector: 'jBizQQWPA'});</script>            <div class = 'data'>            <div class = 'inner-data'>              <script type='text/javascript'>//var cnzz_protocol = (('https:' == document.location.protocol) ? ' https://' : ' http://');document.write(unescape('%3Cspan id='cnzz_stat_icon_1668216'%3E%3C/span%3E%3Cscript src='' + cnzz_protocol + 's95.cnzz.com/stat.php%3Fid%3D1668216%26show%3Dpic1' type='text/javascript'%3E%3C/script%3E'));</script>            </div>        </div>        <script src='/assets/65b5f961/yii.js'></script></body></html>";
        String s = StringUtils.getPattern(src, MockDataTestPaperRegex);
        System.out.println(s);
    }

    /**
     * 生成基础数据
     */
    public static void init() {
        DBUtils.start();
        DBUtils.dropTable(GRADE_TABLE);
        DBUtils.dropTable(SUBJECT_TABLE);
        DBUtils.dropTable(QUESTION_TYPE_TABLE);
        basicInit();
        /*
         * 生成题目表 QuestionWithAnswerVO vo=new QuestionWithAnswerVO(); DBUtils.insert(QUESTION_TABLE,
         * JSON.parseObject(JSON.toJSONString(vo),Map.class),true );
         */
        DBUtils.close();
    }

    public static void main(String[] args) {
        DBUtils.start();
        parseQuestion();
        DBUtils.close();
    }
}
