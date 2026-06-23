package com.ds.system.staff.controller;

import com.ds.common.base.R;
import com.ds.system.staff.service.StaffOrgService;
import com.ds.system.staff.service.domain.StaffOrganizationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 组织架构 Controller
 *
 * @author ds
 */
@RestController
@RequestMapping("/api/staff/org")
@RequiredArgsConstructor
public class StaffOrgController {

    private final StaffOrgService staffOrgService;

    /**
     * 查询组织架构树（含人员）
     */
    @GetMapping("/tree")
    public R<List<StaffOrganizationModel>> getOrgTree() {
        return R.ok(staffOrgService.buildOrgTree());
    }

    /**
     * 刷新全量员工的 ctId、teamName、center 字段
     */
    @GetMapping("/refresh")
    public R<Void> refreshStaffOrgInfo() {
        staffOrgService.refreshStaffOrgInfo();
        return R.ok();
    }
}
