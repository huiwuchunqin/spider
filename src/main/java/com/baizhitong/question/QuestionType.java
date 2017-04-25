package com.baizhitong.question;


public enum QuestionType{
    /**精油题库*/
  /*  xuanze("单选",1),
    tiankong("填空",2),
    budingxiang("多选",3),
    panduan("判断",4),
    jianda("材料题",5),
    bainxi("辨析",6),
    pingxi("评析",7),
    chanshujianjie("阐述见解",8),
    caoliaofenxi("解答题",9),
    zaoju("造句",10),
    juxingzhuanhuan("句型转换",11),
    buquanduihua("补全对话",12),
    yuedubiaoda("阅读表达",13),
    gaicuo("改错",14);*/
    xz("单选",2),
    wxtk("完型填空",6),
    ydlj("单选",7),
    xzt("写作题",8),
    other("其他",10),
    jxzh("句型转换",21),
    dcpx("单词拼写",22),
    bcjz("补充句子",23),
    fy("翻译",24),
    gc("改错",25),
    dczj("单词造句",26),
    xctk("选词填空",27);
   
    
    private String name;
    private Integer typeCode;

    private QuestionType(String name,Integer typeCode) {
        this.name = name;
        this.typeCode=typeCode;
    }
}
