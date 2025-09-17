package com.paradisecloud.fcm.web.controller.dept;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysDeptService;
import com.paradisecloud.system.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping({"/busi/dept"})
public class BusiDeptController extends BaseController {
    @Resource
    private ISysDeptService deptService;

    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping({"/list"})
    public RestResponse list(SysDept dept) {
        if (dept.getDeptId() == null) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            dept.setDeptId(loginUser.getUser().getDeptId());
        }

        List<SysDept> depts = this.deptService.selectDeptList(dept);
        List<SysDept> deptList = new ArrayList<>();
        for (SysDept sysDept : depts) {
            if (sysDept.getDeptId() <= 1 || sysDept.getDeptId() >= 100) {
                deptList.add(sysDept);
            }
        }
        return RestResponse.success(deptList);
    }

    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping({"/list/exclude/{deptId}"})
    public RestResponse excludeChild(@PathVariable(value = "deptId",required = false) Long deptId) {
        List<SysDept> depts = this.deptService.selectDeptList(new SysDept());
        Iterator it = depts.iterator();

        while(true) {
            SysDept d;
            do {
                if (!it.hasNext()) {
                    return RestResponse.success(depts);
                }

                d = (SysDept)it.next();
            }
            while((long)d.getDeptId().intValue() != deptId && !ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId.toString()));

            it.remove();
        }
    }

    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping({"/{deptId}"})
    public RestResponse getInfo(@PathVariable Long deptId) {
        return RestResponse.success(this.deptService.selectDeptById(deptId));
    }

    @GetMapping({"/treeselect"})
    public RestResponse treeselect(SysDept dept) {
        if (dept.getDeptId() == null) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            dept.setDeptId(loginUser.getUser().getDeptId());
        }

        List<SysDept> depts = this.deptService.selectDeptList(dept);
        List<SysDept> deptList = new ArrayList<>();
        for (SysDept sysDept : depts) {
            if (sysDept.getDeptId() <= 1 || sysDept.getDeptId() >= 100) {
                deptList.add(sysDept);
            }
        }
        return RestResponse.success(this.deptService.buildDeptTreeSelect(deptList));
    }

    @GetMapping({"/roleDeptTreeselect/{roleId}"})
    public RestResponse roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysDept sd = new SysDept();
        sd.setDeptId(loginUser.getUser().getDeptId());
        List<SysDept> depts = this.deptService.selectDeptList(sd);
        Map<String, Object> data = new HashMap();
        data.put("checkedKeys", this.deptService.selectDeptListByRoleId(roleId));
        data.put("depts", this.deptService.buildDeptTreeSelect(depts));
        return RestResponse.success(data);
    }

    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(
            title = "部门管理",
            businessType = BusinessType.INSERT
    )
    @PostMapping
    @Operation(summary = "", description = "新增部门")
    public RestResponse add(@Validated @RequestBody SysDept dept) {
        if ("1".equals(this.deptService.checkDeptNameUnique(dept))) {
            return RestResponse.fail("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        } else {
            dept.setCreateBy(SecurityUtils.getUsername());
            for (int k = 0; k < 100; k++) {
                int i = this.deptService.insertDept(dept);
                if (i > 0) {
                    if (dept.getDeptId() > 1 && dept.getDeptId() < 100) {
                        deptService.deleteDeptById(dept.getDeptId());
                        continue;
                    } else {
                        return RestResponse.success(dept);
                    }
                } else {
                    return this.toAjax(i);
                }
            }
            return this.toAjax(0);
        }
    }

    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(
            title = "部门管理",
            businessType = BusinessType.UPDATE
    )
    @PutMapping
    @Operation(summary = "", description = "修改部门")
    public RestResponse edit(@Validated @RequestBody SysDept dept) {
        if (dept.getDeptId() >= 1 && dept.getDeptId() <= 100) {
            return RestResponse.fail("该部门为系统保留部门，不可修改！");
        }
        if ("1".equals(this.deptService.checkDeptNameUnique(dept))) {
            return RestResponse.fail("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        } else if (dept.getParentId().equals(dept.getDeptId())) {
            return RestResponse.fail("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StringUtils.equals("1", dept.getStatus()) && this.deptService.selectNormalChildrenDeptById(dept.getDeptId()) > 0) {
            return RestResponse.fail("该部门包含未停用的子部门！");
        } else {
            dept.setUpdateBy(SecurityUtils.getUsername());
            return this.toAjax(this.deptService.updateDept(dept));
        }
    }

    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(
            title = "部门管理",
            businessType = BusinessType.DELETE
    )
    @DeleteMapping({"/{deptId}"})
    @Operation(summary = "", description = "删除部门")
    public RestResponse remove(@PathVariable Long deptId) {
        if (deptId >= 1 && deptId <= 100) {
            return RestResponse.fail("该部门为系统保留部门，不可删除！");
        }
        if (this.deptService.hasChildByDeptId(deptId)) {
            return RestResponse.fail("存在下级部门,不允许删除");
        } else {
            return this.deptService.checkDeptExistUser(deptId) ? RestResponse.fail("部门存在用户,不允许删除") : this.toAjax(this.deptService.deleteDeptById(deptId));
        }
    }
}
