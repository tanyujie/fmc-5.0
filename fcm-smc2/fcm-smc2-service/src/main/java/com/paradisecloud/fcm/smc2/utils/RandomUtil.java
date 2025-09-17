package com.paradisecloud.fcm.smc2.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author nj
 * @date 2023/5/26 17:07
 */
public class RandomUtil {
    public static String getRandom(){
       // SimpleDateFormat simpleDateFormat;
      //  simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
       // String date=simpleDateFormat.format(new Date());
        Random random=new Random();
        int rannum= (int)(random.nextDouble()*(99999-10000 + 1))+ 10000;
        String rand=rannum+"";
        return rand;
    }
}
