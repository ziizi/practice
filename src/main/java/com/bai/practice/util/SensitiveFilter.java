package com.bai.practice.util;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger loger = LoggerFactory.getLogger(SensitiveFilter.class);
    // 定义替换的符号
    private static final String REPLACEMENT = "**";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct // 初始化方法，在构造器以后
    private void init () {

        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 把读到的数据添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            loger.error("加载敏感词文件失败"+e.getMessage());
        }
    }

    private void addKeyword (String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0;i < keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode = subNode;

            if (i == keyword.length() - 1) {
                tempNode.setIdKeywordEnd(true);
            }
        }

    }


    /**
     * 过滤敏感词，参数是需要过滤的词，返回的是过滤后的敏感词
     * @param text
     * @return
     */
    public String filter (String text) {
        if (text == null)
            return null;
        // 指针1 默认是根
        TrieNode tempNode = rootNode;
        int begin = 0; // 指针2
        int position = 0;// 指针3

        StringBuilder sb = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);
            if (isSymbol(c)) {
                // 如果指针1处于根节点，
                if (tempNode == rootNode){
                    sb.append(c);
                    begin ++;
                }
                position ++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }else if (tempNode.isIdKeywordEnd()){
                sb.append(REPLACEMENT);
                begin = ++ position;
                tempNode = rootNode;
            }else {
                position ++;
            }

        }
        // 把最后的不是敏感词
        sb.append(text.substring(begin));
        return sb.toString();
    }


    //判断是否特殊字符
    private boolean isSymbol (char c) {
        // 0x2E80 到 0x9FFF 东亚的文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF); // 判断是不是正常的字符
    }
    // 定义前缀树
    private class TrieNode {
        // 是不是敏感词的结束标志
        private boolean idKeywordEnd = false;

        // 子节点
        private Map<Character,TrieNode> sonNoeds = new HashMap<>();

        public boolean isIdKeywordEnd() {
            return idKeywordEnd;
        }

        public void setIdKeywordEnd(boolean idKeywordEnd) {
            this.idKeywordEnd = idKeywordEnd;
        }

        // 添加子节点方法
        public void addSubNode (Character key,TrieNode node) {
            sonNoeds.put(key,node);
        }
        // 获取字节点方法
        public TrieNode getSubNode (Character key) {
            return sonNoeds.get(key);
        }
    }
}
