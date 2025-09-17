package com.paradisecloud.fcm.web.controller.smc3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.paradisecloud.fcm.dao.model.BusiPicker;
import com.paradisecloud.fcm.web.model.smc.BusiPickerListVo;
import com.paradisecloud.smc3.service.interfaces.IBusiPickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 【请填写功能名称】Controller
 * 
 * @author lilinhai
 * @date 2024-06-13
 */
@RestController
@RequestMapping("/smc3/picker")
@Tag(name = "【请填写功能名称】")
public class BusiPickerController extends BaseController
{
    @Autowired
    private IBusiPickerService busiPickerService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询【请填写功能名称】列表")
    public RestResponse list(BusiPicker busiPicker)
    {
        List<BusiPicker> list = busiPickerService.selectBusiPickerList(busiPicker);
        List<Integer> apiDepts = new ArrayList<>();
        List<Integer> accessDepts = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for (BusiPicker picker : list) {
                if(Objects.equals("API",picker.getDeptType())){
                    apiDepts.add(picker.getDeptId());
                }else {
                    accessDepts.add(picker.getDeptId());
                }


            }
        }
        BusiPickerListVo resut = new BusiPickerListVo();
        resut.setApiDepts(apiDepts);
        resut.setAccessDepts(accessDepts);
        return RestResponse.success(resut);
    }

    /**
     * 修改【请填写功能名称】
     */
    @PutMapping
    @Operation(summary = "修改【请填写功能名称】")
    public RestResponse edit(@RequestBody BusiPickerListVo busiPickerListVo)
    {

        List<Integer> apiDepts = busiPickerListVo.getApiDepts();
        List<Integer> accessDepts = busiPickerListVo.getAccessDepts();

        return toAjax(busiPickerService.updateBusiPickerDeptIds(apiDepts,accessDepts));
    }


}
