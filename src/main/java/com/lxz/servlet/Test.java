package com.lxz.servlet;

import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by xiaolezheng on 16/4/14.
 */
public class Test {
    public static void main(String[] args){
        String file = "2016.12.43.log.xml";
        int lastIndex = StringUtils.lastIndexOf(file,".");
        System.out.println(StringUtils.substring(file,lastIndex));

        System.out.println(StringUtils.substringBeforeLast(file, "."));
        System.out.println(StringUtils.substringAfterLast(file, "."));

        System.out.println(StringUtils.substring(file,0,4));

        System.out.println(Files.getFileExtension("123d.dfd.jgp"));

    }
}
