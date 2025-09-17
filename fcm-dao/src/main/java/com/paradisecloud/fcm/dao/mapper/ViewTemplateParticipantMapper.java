package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.ViewTemplateParticipant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ViewTemplateParticipantMapper {

    /**
     * 查询会议模板的参会者
     *
     * @param id 会议模板的参会者ID
     * @return 会议模板的参会者
     */
    ViewTemplateParticipant selectViewTemplateParticipantById(@Param("mcuType") String mcuType, @Param("id") Long id);

    /**
     * 查询会议模板的参会者列表
     *
     * @param viewTemplateParticipant 会议模板的参会者
     * @return 会议模板的参会者集合
     */
    List<ViewTemplateParticipant> selectViewTemplateParticipantList(ViewTemplateParticipant viewTemplateParticipant);
}
