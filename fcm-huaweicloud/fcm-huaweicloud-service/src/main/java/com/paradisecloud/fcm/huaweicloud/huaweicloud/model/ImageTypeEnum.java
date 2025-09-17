package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

/**
 * @author nj
 * @date 2024/3/18 15:52
 */
public enum ImageTypeEnum {
    Single("Single",1,1),
    Two("Two",2,1),
    Three("Three",3,1),
    Three2("Three-2",3,2),
    Three3("Three-3",3,3),
    Three4("Three-4",3,4),
    Four("Four",4,1),
    Four2("Four-2",4,2),
    Four3("Four-3",4,3),
    Five("Five",5,1),
    Five2("Five-2",5,2),
    Six("Six",6,1),
    Six2("Six-2",6,2),
    Six3("Six-3",6,3),
    Six4("Six-4",6,4),
    Six5("Six-5",6,5),
    Seven("Seven",7,1),
    Seven2("Seven-2",7,2),
    Seven3("Seven-3",7,3),
    Seven4("Seven-4",7,4),
    Eight("Eight",8,1),
    Eight2("Eight-2",8,2),
    Eight3("Eight-3",8,3),
    Eight4("Eight-4",8,4),
    Nine("Nine",9,1),
    Ten("Ten",10,1),
    Ten2("Ten-2",10,2),
    Ten3("Ten-3",10,3),
    Ten4("Ten-4",10,4),
    Ten5("Ten-5",10,5),
    Ten6("Ten-6",10,6),
    Thirteen("Thirteen",13,1),
    Thirteen2("Thirteen-2",13,2),
    Thirteen3("Thirteen-3",13,3),
    Thirteen4("Thirteen-4",13,4),
    Thirteen5("Thirteen-5",13,5),
    Sixteen("Sixteen",16,1),
    Seventeen("Seventeen",17,1),
    TwentyFive("Twenty-Five",25,1),
    ;

    private String name;
    private int number;
    private int mode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    ImageTypeEnum(String name, int number, int mode) {
        this.name = name;
        this.number = number;
        this.mode = mode;
    }

    public static ImageTypeEnum getByNumberAndMode(int number, int mode){
        for (ImageTypeEnum value : ImageTypeEnum.values()) {
            if(value.getNumber()==number&&value.getMode()==mode){
                return value;
            }
        }
        return Single;
    }
}
