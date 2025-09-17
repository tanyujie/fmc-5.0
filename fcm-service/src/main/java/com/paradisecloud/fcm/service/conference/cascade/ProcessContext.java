package com.paradisecloud.fcm.service.conference.cascade;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/8/2 9:37
 */

public class ProcessContext {

    private ProcessCascadeEngine processCascadeEngine;
    private String mainConferenceId;
    private List<CascadeTemplate> cascadeTemplateList;
    private AbstractConference mainConference;
    private String mainUri;

    private String upUri;

    private String needUri;

    private String mainMcu;

    private Map<Long,CascadeTemplate> map=new ConcurrentHashMap<>();

    private Map<Long,String> uriMap=new ConcurrentHashMap<>();

    private String upTemplate;

    private AbstractConference upConference;

    public synchronized void  putTemplateMap(Long id,CascadeTemplate template){
        if(id==null){
            return;
        }

        this.map.put(id,template);
    }

    public List<CascadeTemplate> getCascadeTemplateList() {
        return cascadeTemplateList;
    }

    public void setCascadeTemplateList(List<CascadeTemplate> cascadeTemplateList) {
        this.cascadeTemplateList = cascadeTemplateList;
    }

    public String getMainUri() {
        return mainUri;
    }

    public void setMainUri(String mainUri) {
        this.mainUri = mainUri;
    }

    public String getUpUri() {
        return upUri;
    }

    public void setUpUri(String upUri) {
        this.upUri = upUri;
    }

    public String getNeedUri() {
        return needUri;
    }

    public void setNeedUri(String needUri) {
        this.needUri = needUri;
    }

    public String getMainMcu() {
        return mainMcu;
    }

    public void setMainMcu(String mainMcu) {
        this.mainMcu = mainMcu;
    }

    public Map<Long, CascadeTemplate> getMap() {
        return map;
    }

    public void setMap(Map<Long, CascadeTemplate> map) {
        this.map = map;
    }

    public Map<Long, String> getUriMap() {
        return uriMap;
    }

    public void setUriMap(Map<Long, String> uriMap) {
        this.uriMap = uriMap;
    }

    public String getUpTemplate() {
        return upTemplate;
    }

    public void setUpTemplate(String upTemplate) {
        this.upTemplate = upTemplate;
    }

    public AbstractConference getUpConference() {
        return upConference;
    }

    public void setUpConference(AbstractConference upConference) {
        this.upConference = upConference;
    }

    public AbstractConference getMainConference() {
        return mainConference;
    }

    public void setMainConference(AbstractConference mainConference) {
        this.mainConference = mainConference;
    }

    public String getMainConferenceId() {
        return mainConferenceId;
    }

    public void setMainConferenceId(String mainConferenceId) {
        this.mainConferenceId = mainConferenceId;
    }

    public ProcessCascadeEngine getProcessCascadeEngine() {
        return processCascadeEngine;
    }

    public void setProcessCascadeEngine(ProcessCascadeEngine processCascadeEngine) {
        this.processCascadeEngine = processCascadeEngine;
    }
}
