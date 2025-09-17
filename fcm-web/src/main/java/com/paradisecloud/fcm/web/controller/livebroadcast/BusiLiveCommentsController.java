package com.paradisecloud.fcm.web.controller.livebroadcast;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiLiveComments;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveCommentsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;

/**
 * 直播回放评论Controller
 * 
 * @author lilinhai
 * @date 2024-05-21
 */
@RestController
@RequestMapping("/busi/live/comments")
@Tag(name = "直播回放评论")
public class BusiLiveCommentsController extends BaseController
{
    @Resource
    private IBusiLiveCommentsService busiLiveCommentsService;

    /**
     * 查询直播回放评论列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询直播回放评论列表")
    public RestResponse list(BusiLiveComments busiLiveComments)
    {
        startPage();
        List<BusiLiveComments> list = busiLiveCommentsService.selectBusiLiveCommentsList(busiLiveComments);
        return getDataTable(list);
    }

    /**
     * 获取直播回放评论详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取直播回放评论详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiLiveCommentsService.selectBusiLiveCommentsById(id));
    }

    /**
     * 新增直播回放评论
     */
    @PreAuthorize("@ss.hasPermi('live:comments:add')")
    @Log(title = "直播回放评论", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增直播回放评论")
    public RestResponse add(@RequestBody BusiLiveComments busiLiveComments)
    {
        return toAjax(busiLiveCommentsService.insertBusiLiveComments(busiLiveComments));
    }

    /**
     * 修改直播回放评论
     */
    @PreAuthorize("@ss.hasPermi('live:comments:edit')")
    @Log(title = "直播回放评论", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改直播回放评论")
    public RestResponse edit(@RequestBody BusiLiveComments busiLiveComments)
    {
        return toAjax(busiLiveCommentsService.updateBusiLiveComments(busiLiveComments));
    }

    /**
     * 删除直播回放评论
     */
    @PreAuthorize("@ss.hasPermi('live:comments:remove')")
    @Log(title = "直播回放评论", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除直播回放评论")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiLiveCommentsService.deleteBusiLiveCommentsByIds(ids));
    }
}
