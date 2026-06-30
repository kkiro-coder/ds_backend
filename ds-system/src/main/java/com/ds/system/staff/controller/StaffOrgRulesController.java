package com.ds.system.staff.controller;

import com.ds.common.base.R;
import com.ds.system.staff.service.StaffOrgRuleService;
import com.ds.system.staff.service.domain.StaffOrgRulesModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 企业组织架构规则 Controller
 *
 * @author ds
 */
@RestController
@RequestMapping("/api/staff/org/rules")
@RequiredArgsConstructor
public class StaffOrgRulesController {

    private final StaffOrgRuleService staffOrgRuleService;

    /**
     * 查询所有规则
     */
    @GetMapping("/list")
    public R<List<StaffOrgRulesModel>> listAll() {
        return R.ok(staffOrgRuleService.listAll());
    }

    /**
     * 根据 ruleId 查询
     */
    @GetMapping("/get")
    public R<StaffOrgRulesModel> getByRuleId(@RequestParam String ruleId) {
        return R.ok(staffOrgRuleService.getByRuleId(ruleId));
    }

    /**
     * 根据类型查询
     */
    @GetMapping("/listByType")
    public R<List<StaffOrgRulesModel>> listByType(@RequestParam String type) {
        return R.ok(staffOrgRuleService.listByType(type));
    }
}
