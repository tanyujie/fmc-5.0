package com.paradisecloud.fcm.fme.model.response.layout;

import java.util.ArrayList;

import com.paradisecloud.fcm.fme.model.cms.LayoutTemplate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ActiveLayoutTemplatesResponse
{
    private Integer total;
    private ArrayList<LayoutTemplate> layoutTemplate;
}
