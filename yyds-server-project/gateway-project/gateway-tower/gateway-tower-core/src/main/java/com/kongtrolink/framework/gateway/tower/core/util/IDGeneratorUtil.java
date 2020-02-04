package com.kongtrolink.framework.gateway.tower.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by iceze on 2017/9/18.
 */
public class IDGeneratorUtil {
    public static String genPrimaryKey(){
        return UUID.randomUUID().toString();
    }

    //生成5位随机数
    public static String getMessageID(){
//        Random random = new Random();
//        String str = System.currentTimeMillis()+"_"+ random.nextInt(10000);//得到当前时间的毫秒值
        String str = "";
        Random rand = new Random();
        for(int i=0;i<5;i++){
            int num = rand.nextInt(3);
            switch(num){
                case 0:
                    char c1 = (char)(rand.nextInt(26)+'a');//生成随机小写字母
                    str += c1;
                    break;
                case 1:
                    char c2 = (char)(rand.nextInt(26)+'A');//生成随机大写字母
                    str += c2;
                    break;
                case 2:
                    str += rand.nextInt(10);//生成随机数字
            }
        }
        return str;
    }

    //生成2位随机数
    public static String getRandomNumber(){
        Random random = new Random();
        int i = random.nextInt(100);
        return String.format("%02d",i);
    }


}