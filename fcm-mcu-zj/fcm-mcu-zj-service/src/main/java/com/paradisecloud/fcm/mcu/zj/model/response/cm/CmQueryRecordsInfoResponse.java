package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmQueryRecordsInfoResponse extends CommonResponse {

    private String[] owner_uids;
    private Integer[] is_personals;
    private Integer[] file_sizes;
    private Integer[] locked;
    private Integer[] start_dtms;
    private String[] snap_urls;
    private Integer[] widths;
    private Integer[] durations;
    private Integer[] play_bytes;
    private String[] room_names;
    private Integer[] heights;
    private String[] vod_names;
    private String[] uuids;
    private Integer[] fps;
    private String[] download_urls;
    private String[] file_names;
    private String[] nick_names;
    private String[] vod_ids;
    private Integer[] expiration_times;
    private String[] record_uuids;

    public String[] getOwner_uids() {
        return owner_uids;
    }

    public void setOwner_uids(String[] owner_uids) {
        this.owner_uids = owner_uids;
    }

    public Integer[] getIs_personals() {
        return is_personals;
    }

    public void setIs_personals(Integer[] is_personals) {
        this.is_personals = is_personals;
    }

    public Integer[] getFile_sizes() {
        return file_sizes;
    }

    public void setFile_sizes(Integer[] file_sizes) {
        this.file_sizes = file_sizes;
    }

    public Integer[] getLocked() {
        return locked;
    }

    public void setLocked(Integer[] locked) {
        this.locked = locked;
    }

    public Integer[] getStart_dtms() {
        return start_dtms;
    }

    public void setStart_dtms(Integer[] start_dtms) {
        this.start_dtms = start_dtms;
    }

    public String[] getSnap_urls() {
        return snap_urls;
    }

    public void setSnap_urls(String[] snap_urls) {
        this.snap_urls = snap_urls;
    }

    public Integer[] getWidths() {
        return widths;
    }

    public void setWidths(Integer[] widths) {
        this.widths = widths;
    }

    public Integer[] getDurations() {
        return durations;
    }

    public void setDurations(Integer[] durations) {
        this.durations = durations;
    }

    public Integer[] getPlay_bytes() {
        return play_bytes;
    }

    public void setPlay_bytes(Integer[] play_bytes) {
        this.play_bytes = play_bytes;
    }

    public String[] getRoom_names() {
        return room_names;
    }

    public void setRoom_names(String[] room_names) {
        this.room_names = room_names;
    }

    public Integer[] getHeights() {
        return heights;
    }

    public void setHeights(Integer[] heights) {
        this.heights = heights;
    }

    public String[] getVod_names() {
        return vod_names;
    }

    public void setVod_names(String[] vod_names) {
        this.vod_names = vod_names;
    }

    public String[] getUuids() {
        return uuids;
    }

    public void setUuids(String[] uuids) {
        this.uuids = uuids;
    }

    public Integer[] getFps() {
        return fps;
    }

    public void setFps(Integer[] fps) {
        this.fps = fps;
    }

    public String[] getDownload_urls() {
        return download_urls;
    }

    public void setDownload_urls(String[] download_urls) {
        this.download_urls = download_urls;
    }

    public String[] getFile_names() {
        return file_names;
    }

    public void setFile_names(String[] file_names) {
        this.file_names = file_names;
    }

    public String[] getNick_names() {
        return nick_names;
    }

    public void setNick_names(String[] nick_names) {
        this.nick_names = nick_names;
    }

    public String[] getVod_ids() {
        return vod_ids;
    }

    public void setVod_ids(String[] vod_ids) {
        this.vod_ids = vod_ids;
    }

    public Integer[] getExpiration_times() {
        return expiration_times;
    }

    public void setExpiration_times(Integer[] expiration_times) {
        this.expiration_times = expiration_times;
    }

    public String[] getRecord_uuids() {
        return record_uuids;
    }

    public void setRecord_uuids(String[] record_uuids) {
        this.record_uuids = record_uuids;
    }
}
