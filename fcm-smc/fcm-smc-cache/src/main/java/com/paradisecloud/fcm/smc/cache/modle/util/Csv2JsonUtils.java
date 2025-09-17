package com.paradisecloud.fcm.smc.cache.modle.util;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author nj
 * @date 2023/3/6 16:15
 */
public class Csv2JsonUtils {
    /**
     * 将csv中的一行数据转换成一个一级json
     * @param keys json的key，顺序需与csv中的value对应
     * @param values csv中数据作为value
     * @return
     */
    public JSONObject csv2JSON(String[] keys, String[] values) throws JSONException {

        JSONObject json = new JSONObject();
        for (int i = 0; i < keys.length; i++) {
            try{
                json.append(keys[i],values[i]);
            }
            catch (ArrayIndexOutOfBoundsException e){
                json.append(keys[i],null);
            }
        }
        return json;
    }

    /**
     * 将csv的每一行数据都转换成一级json，返回json数组
     * @param keys json的key，顺序需与csv中的value对应
     * @param stringsList 读取csv返回的List<String[]>
     * @return
     */
    public JSONObject[] csv2JSON(String[] keys, List<String[]> stringsList) throws JSONException {

        JSONObject[] jsons = new JSONObject[stringsList.size()];
        int index = 0 ;
        for (String[] strings : stringsList
        ) {
            JSONObject json = this.csv2JSON(keys, strings);
            jsons[index] = json;
            index ++ ;
        }
        return jsons;
    }



}
