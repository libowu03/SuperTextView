package com.textutils.textview;

import java.util.ArrayList;
import java.util.List;

public class Test {
    private static String tempStr = "";
    private static List<String> list;

    public static void main(String[] args){
        String a = "<fndmnfd<反倒可能发>fdfn>";
        list = new ArrayList<>();
        tempStr = a;
    }

    public static void runMatch(String startSign,String endSign,String text){
        //如果不存在起始标记或结束标记,则直接结束递归
        int startIndex = text.indexOf(startSign);
        if (startIndex == -1){
            return;
        }
        int endIndex = text.lastIndexOf(endSign);
        if (endIndex == -1){
            return;
        }
        tempStr = text.substring(startIndex,endIndex);
        System.out.println(startIndex+","+endIndex);
        runMatch(startSign,endSign,tempStr);
    }

}
