package com.ds.system.staff.service;

import com.ds.common.exception.BusinessException;
import com.ds.common.utils.JsonUtil;
import com.ds.common.utils.PerKeyLock;
import com.ds.common.utils.TimeUtil;
import com.ds.system.staff.dao.StaffOrgRulesDao;
import com.ds.system.staff.entity.StaffOrgRules;
import com.ds.system.staff.service.domain.StaffOrgRulesModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业组织架构与范围配置 Service
 *
 * @author ds
 */
@Service
@RequiredArgsConstructor
public class StaffOrgRuleService {

    private final StaffOrgRulesDao staffOrgRulesDao;
    private final PerKeyLock ruleLock = new PerKeyLock();

    /**
     * 根据 ruleId 查询规则
     */
    public StaffOrgRulesModel getByRuleId(String ruleId) {
        return staffOrgRulesDao.findByRuleId(ruleId)
                .map(StaffOrgRules::toModel)
                .orElseThrow(() -> new BusinessException("规则不存在: " + ruleId));
    }

    /**
     * 查询所有规则
     */
    public List<StaffOrgRulesModel> listAll() {
        return staffOrgRulesDao.findAll().stream()
                .map(StaffOrgRules::toModel)
                .collect(Collectors.toList());
    }

    /**
     * 根据类型查询规则
     */
    public List<StaffOrgRulesModel> listByType(String type) {
        return staffOrgRulesDao.findByType(type).stream()
                .map(StaffOrgRules::toModel)
                .collect(Collectors.toList());
    }

    /**
     * 新增规则（自动生成 ruleId，去掉 id/ruleId/createdTime/updatedTime）
     */
    @Transactional
    public StaffOrgRulesModel addRule(StaffOrgRulesModel input) {
        StaffOrgRules entity = input.toEntity();
        StaffOrgRules saved = staffOrgRulesDao.save(entity);
        return saved.toModel();
    }

    /**
     * 编辑规则（仅可修改 name、type、depts、centers、scopes）
     */
    @Transactional(rollbackFor = Exception.class)
    public StaffOrgRulesModel editRule(StaffOrgRulesModel input) {
        return ruleLock.execute(input.getRuleId(), () -> {
            StaffOrgRules entity = staffOrgRulesDao.findByRuleId(input.getRuleId())
                    .orElseThrow(() -> new BusinessException("规则不存在: " + input.getRuleId()));
            entity.setName(input.getName());
            entity.setType(input.getType());
            entity.setDepts(JsonUtil.toJson(input.getDepts()));
            entity.setCenters(JsonUtil.toJson(input.getCenters()));
            entity.setScope(JsonUtil.toJson(input.getScopes()));
            entity.setUpdatedTime(TimeUtil.now());
            StaffOrgRules saved = staffOrgRulesDao.save(entity);
            return saved.toModel();
        });
    }

    /**
     * 根据 id 删除规则
     */
    @Transactional(rollbackFor = Exception.class)
    public void delRule(StaffOrgRulesModel input) {
        if (input == null || input.getId() == null) {
            throw new BusinessException("删除规则时 id 不能为空");
        }
        String lockKey = "DEL_" + input.getId();
        ruleLock.execute(lockKey, () -> {
            staffOrgRulesDao.deleteById(input.getId());
        });
    }
}
