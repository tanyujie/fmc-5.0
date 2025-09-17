package com.paradisecloud.com.fcm.smc.modle;

/**
 * @author nj
 * @date 2023/6/25 10:49
 */
public enum VideoResolutionEnum {
    MPI_QCIF(2),
    MPI_CIF(3),
    MPI_4CIF(4),
    MPI_16CIF(5),
    MPI_480P(12),
    MPI_720P(13),
    MPI_720P60(14),
    MPI_1080P(16),
    MPI_1080P60(17),
    MPI_360P(18),
    MPI_SVC(20),
    MPI_VGA(21),
    MPI_4K(30),
    MPI_576P(36),
    INVALID(255);

    private int code;

    VideoResolutionEnum(int code) {
        this.code = code;
    }

    public static VideoResolutionEnum getValueByCode(int code) {
        VideoResolutionEnum[] values = VideoResolutionEnum.values();
        for (VideoResolutionEnum value : values) {
           if(value.code==code) {
               return  value;
           }
        }
        return null;
    }
}
