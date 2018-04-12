package com.syx.yuqingmanage.utils;

/**
 * @author zg
 */
public class MessagePost {
    /**
     * 判断内容是否有包含关系
     * @param impWord
     * @param wordContext
     * @param wordTitle
     * @return
     */
    public static Boolean judgeWord(String impWord, String wordContext, String wordTitle) {
        Boolean flagWord = true;
        //匹配关键词
        String[] impWords = impWord.split("\\|");
        int impWordsLen = impWords.length;
        int i;
        for (i = 0; i < impWordsLen; i++) {
            if (wordContext.indexOf(impWords[i]) != -1 || wordTitle.indexOf(impWords[i]) != -1) {
                //包含了匹配关键词
                flagWord = true;
                break;
            }
        }
        if (i == impWordsLen) {
            flagWord = false;
        }
        return flagWord;
    }

    /**
     * 判断地址是否有包含关系
     * @param impLink
     * @param wordLink
     * @return
     */
    public static Boolean judgeLink(String impLink, String wordLink) {
        Boolean flagLink = true;
        //需要判断的地址
        String[] impLinks = impLink.split("\\|");
        int impLinksLen = impLinks.length;
        int i;
        for (i = 0; i < impLinksLen; i++) {
            if (wordLink.indexOf(impLinks[i]) != -1) {
                //包含了匹配地址
                flagLink = true;
                break;
            }
        }
        if (i == impLinksLen) {
            flagLink = false;
        }
        return flagLink;
    }
}
