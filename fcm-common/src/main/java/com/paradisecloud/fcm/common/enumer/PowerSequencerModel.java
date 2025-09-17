package com.paradisecloud.fcm.common.enumer;

import com.paradisecloud.fcm.common.utils.Converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PowerSequencerModel {
    HT_3188L("HT-3188L", 1, "HTDZ", "海天电子", 8),
    ;

    /**
     * 代码
     */
    private String code;

    /**
     * 类型 1：串口
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


    private static final Map<String, PowerSequencerModel> MAP = new HashMap<>();
    private static final Map<Integer, List<PowerSequencerModel>> TYPE_MAP = new HashMap<>();
    static {
        for (PowerSequencerModel powerSequencerModel : values()) {
            MAP.put(powerSequencerModel.getCode(), powerSequencerModel);
            List<PowerSequencerModel> powerSequencerModelList = TYPE_MAP.get(powerSequencerModel.getType());
            if (powerSequencerModelList == null) {
                powerSequencerModelList = new ArrayList<>();
                TYPE_MAP.put(powerSequencerModel.getType(), powerSequencerModelList);
            }
            powerSequencerModelList.add(powerSequencerModel);
        }
    }

    PowerSequencerModel(String code, int type, String brand, String brandAlias, int channels) {
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

    public String getPowerOnCmd(int channel) {
        return getPowerOnCmd(0, channel);
    }

    public String getPowerOnCmd(int equipmentId, int channel) {
        String cmd = "";
        if (HT_3188L.getCode().equals(code)) {
            if (channel <= channels) {
                cmd += "A5A5" + to2HexString(equipmentId) + "00" + to2HexString(channel) + "AA";
            }
        }
        return cmd;
    }

    public String getPowerOffCmd(int channel) {
        return getPowerOffCmd(0, channel);
    }

    public String getPowerOffCmd(int equipmentId, int channel) {
        String cmd = "";
        if (HT_3188L.getCode().equals(code)) {
            if (channel <= channel) {
                cmd += "A5A5" + to2HexString(equipmentId) + "01" + to2HexString(channel) + "AA";
            }
        }
        return cmd;
    }

    public String getCmdTopic(String topicPrefix, int lotChannel) {
        return topicPrefix + "/" + lotChannel + "/cmd";
    }

    public static PowerSequencerModel convert(String code) {
        return MAP.get(code);
    }

    public static List<Map<String, Object>> getPowerSequencerModelList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PowerSequencerModel powerSequencerModel : PowerSequencerModel.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", powerSequencerModel.getCode());
            map.put("type", powerSequencerModel.getType());
            map.put("brand", powerSequencerModel.getBrand());
            map.put("brandAlias", powerSequencerModel.getBrandAlias());
            map.put("channels", powerSequencerModel.getChannels());
            list.add(map);
        }
        return list;
    }

    public static List<Map<String, Object>> getPowerSequencerModelList(int type) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<PowerSequencerModel> powerSequencerModelList = TYPE_MAP.get(type);
        if (powerSequencerModelList != null) {
            for (PowerSequencerModel powerSequencerModel : powerSequencerModelList) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", powerSequencerModel.getCode());
                map.put("type", powerSequencerModel.getType());
                map.put("brand", powerSequencerModel.getBrand());
                map.put("brandAlias", powerSequencerModel.getBrandAlias());
                map.put("channels", powerSequencerModel.getChannels());
                list.add(map);
            }
        }
        return list;
    }

    private String to2HexString(int i) {
        String s =  Converter.byteToHex(Integer.valueOf(i).byteValue());
        if (s.length() > 2) {
            return s.substring(s.length() - 2);
        }
        return s;
    }
}
