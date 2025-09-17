package com.paradisecloud.fcm.common.enumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum LotModel {
    USR_W610("USR-W610", 1, "USR", "有人物联", 1),
    USR_N520("USR-N520", 1, "USR", "有人物联", 2),
    USR_N540("USR-N540", 1, "USR", "有人物联", 4),
    USR_N580("USR-N580", 1, "USR", "有人物联", 8),
    ;

    /**
     * 代码
     */
    private String code;

    /**
     * 类型 1：串口设备
     */
    private int type;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 品牌别名
     */
    private String brandAlias;

    /**
     * 通道数
     */
    private int channels;

    private static final Map<String, LotModel> MAP = new HashMap<>();
    private static final Map<Integer, List<LotModel>> TYPE_MAP = new HashMap<>();
    static {
        for (LotModel lotModel : values()) {
            MAP.put(lotModel.getCode(), lotModel);
            List<LotModel> lotModelList = TYPE_MAP.get(lotModel.getType());
            if (lotModelList == null) {
                lotModelList = new ArrayList<>();
                TYPE_MAP.put(lotModel.getType(), lotModelList);
            }
            lotModelList.add(lotModel);
        }
    }

    LotModel(String code, int type, String brand, String brandAlias, int channels) {
        this.code = code;
        this.type = type;
        this.brand = brand;
        this.brandAlias = brandAlias;
        this.channels = channels;
    }

    public String getCode() {
        return code;
    }

    public int getType() {
        return type;
    }

    public String getBrand() {
        return brand;
    }

    public String getBrandAlias() {
        return brandAlias;
    }

    public int getChannels() {
        return channels;
    }

    public static LotModel convert(String code) {
        return MAP.get(code);
    }

    public static List<Map<String, Object>> getLotModelList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LotModel lotModel : LotModel.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", lotModel.getCode());
            map.put("type", lotModel.getType());
            map.put("brand", lotModel.getBrand());
            map.put("brandAlias", lotModel.getBrandAlias());
            map.put("channels", lotModel.getChannels());
            list.add(map);
        }
        return list;
    }

    public static List<Map<String, Object>> getLotModelList(int type) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<LotModel> lotModels = TYPE_MAP.get(type);
        if (lotModels != null) {
            for (LotModel lotModel : lotModels) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", lotModel.getCode());
                map.put("type", lotModel.getType());
                map.put("brand", lotModel.getBrand());
                map.put("brandAlias", lotModel.getBrandAlias());
                map.put("channels", lotModel.getChannels());
                list.add(map);
            }
        }
        return list;
    }
}
