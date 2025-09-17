package com.paradisecloud.fcm.service.live;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LiveService {
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private boolean enable;
    private String active;
    private Live live;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Live getLive() {
        return live;
    }

    public void setLive(Live live) {
        this.live = live;
    }

    public String generateLiveId() {
        String liveId = String.valueOf(System.currentTimeMillis());
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int nextInt = random.nextInt(9);
            liveId += nextInt;
        }
        return liveId;
    }

    public LiveValue generateUrl(String conferenceNum, LocalDateTime expirationTime) {
        String liveId = generateLiveId();
        return generateUrl(liveId, conferenceNum, expirationTime, 0);
    }

    public LiveValue generateUrl(String liveId, String conferenceNum, LocalDateTime expirationTime) {
        return generateUrl(liveId, conferenceNum, expirationTime, 0);
    }

    public LiveValue generateUrl(String liveId, String conferenceNum, LocalDateTime expirationTime, Integer playbackType) {
        LiveValue liveValue = new LiveValue();
        if (liveId != null && expirationTime != null) {
            if ("tencent".equals(active)) {
                String streamName = liveId + "_" + conferenceNum;
                long txTime = expirationTime.toEpochSecond(ZoneOffset.of("+8"));
                String str = getSafeUrl(live.getAuthKey(), streamName, txTime);
                //不写死播放协议，便于前端条件编译不同平台实现不同平台播放
                String pullUrl = live.getPullDomainName() + "/" + live.getAppName() + "/" + streamName;
                String pushDomainName;
                if (playbackType == 1) {
                    pushDomainName = live.getPlaybackDomainName();
                } else {
                    pushDomainName = live.getDomainName();
                }
                String pushUrl = "rtmp://" + pushDomainName + "/" + live.getAppName() + "/" + streamName + "?" + str;

                liveValue.setPushId(streamName);
                liveValue.setPullUrl(pullUrl);
                liveValue.setPushUrl(pushUrl);
                List<String> pullUrlList = new ArrayList<>();
                {
                    String pushUrlFull = "webrtc://" + pullUrl;
                    pullUrlList.add(pushUrlFull);
                }
                {
                    String pushUrlFull = "rtmp://" + pullUrl;
                    pullUrlList.add(pushUrlFull);
                }
                {
                    String pushUrlFull = "http://" + pullUrl + ".flv";
                    pullUrlList.add(pushUrlFull);
                }
                {
                    String pushUrlFull = "http://" + pullUrl + ".m3u8";
                    pullUrlList.add(pushUrlFull);
                }
                liveValue.setPullUrlList(pullUrlList);
            }
        }
        return liveValue;
    }

    public LiveValue generateUrlFromPushUrl(String pushUrl) {
        LiveValue liveValue = new LiveValue();
        if ("tencent".equals(active)) {
            String streamName = "";
            String pushUrlTemp = pushUrl.replace("rtmp://", "");
            if (pushUrlTemp.indexOf("?") > -1) {
                pushUrlTemp = pushUrlTemp.substring(0, pushUrlTemp.indexOf("?"));
                String[] pushUrlArr = pushUrlTemp.split("/");
                try {
                    streamName = pushUrlArr[2];
                } catch (Exception e) {
                }
            }

            String pullUrl = live.getPullDomainName() + "/" + live.getAppName() + "/" + streamName;
            liveValue.setPushId(streamName);
            liveValue.setPullUrl(pullUrl);
            liveValue.setPushUrl(pushUrl);
            List<String> pullUrlList = new ArrayList<>();
            {
                String pushUrlFull = "webrtc://" + pullUrl;
                pullUrlList.add(pushUrlFull);
            }
            {
                String pushUrlFull = "rtmp://" + pullUrl;
                pullUrlList.add(pushUrlFull);
            }
            {
                String pushUrlFull = "http://" + pullUrl + ".flv";
                pullUrlList.add(pushUrlFull);
            }
            {
                String pushUrlFull = "http://" + pullUrl + ".m3u8";
                pullUrlList.add(pushUrlFull);
            }
            liveValue.setPullUrlList(pullUrlList);
        }
        return liveValue;
    }

    public boolean checkSign(String sign, long t) {
        if ("tencent".equals(active)) {
            String checkSign = null;
            try {
                String checkStr = live.getNoticeSecretKey() + t;
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                checkSign = byteArrayToHexString(messageDigest.digest(checkStr.getBytes("UTF-8")));
                if (checkSign.equals(sign)) {
                    return true;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String getSafeUrl(String key, String streamName, long txTime) {
        String input = new StringBuilder().append(key).append(streamName).append(Long.toHexString(txTime).toUpperCase()).toString();

        String txSecret = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            txSecret = byteArrayToHexString(messageDigest.digest(input.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return txSecret == null ? ""
                : new StringBuilder().append("txSecret=").append(txSecret).append("&").append("txTime=")
                .append(Long.toHexString(txTime).toUpperCase()).toString();
    }

    private String byteArrayToHexString(byte[] data) {
        char[] out = new char[data.length << 1];

        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

}
