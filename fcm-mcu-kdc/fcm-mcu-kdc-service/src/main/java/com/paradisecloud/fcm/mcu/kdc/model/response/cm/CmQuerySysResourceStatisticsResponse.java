package com.paradisecloud.fcm.mcu.kdc.model.response.cm;

import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.util.List;

public class CmQuerySysResourceStatisticsResponse extends CommonResponse {

    private String resource_type;
    private List<Resource> resources;

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public class Resource {
        private String name;
        private Integer resolution;
        private Integer max_join_mt;
        private Integer total;
        private Integer used;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getResolution() {
            return resolution;
        }

        public void setResolution(Integer resolution) {
            this.resolution = resolution;
        }

        public Integer getMax_join_mt() {
            return max_join_mt;
        }

        public void setMax_join_mt(Integer max_join_mt) {
            this.max_join_mt = max_join_mt;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getUsed() {
            return used;
        }

        public void setUsed(Integer used) {
            this.used = used;
        }
    }

}
