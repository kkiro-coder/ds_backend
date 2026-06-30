package com.ds.system.staff.entity;

import com.ds.common.utils.JsonUtil;
import com.ds.common.utils.TimeUtil;
import com.ds.system.staff.service.domain.StaffOrgRulesModel;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 企业组织架构与范围配置表 JPA 实体
 *
 * @author ds
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "sys_staff_org_rules")
public class StaffOrgRules implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int")
    private Integer id;

    /** 实体唯一标识符 */
    @Column(name = "rule_id", length = 64, nullable = false)
    private String ruleId;

    /** 实体名称 (如 "华泰联合") */
    @Column(length = 255, nullable = false)
    private String name;

    /** 实体类型 (如 "manual") */
    @Column(length = 50)
    private String type;

    /** 部门列表数据 (JSON) */
    @Column(columnDefinition = "text")
    private String depts;

    /** 中心与团队组织数据 (JSON) */
    @Column(columnDefinition = "text")
    private String centers;

    /** 组织维度范围数据 (JSON) */
    @Column(columnDefinition = "text")
    private String scope;

    /** 创建时间 */
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /** 更新时间 */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    public StaffOrgRulesModel toModel() {
        StaffOrgRulesModel model = new StaffOrgRulesModel();
        model.setId(this.id);
        model.setRuleId(this.ruleId);
        model.setName(this.name);
        model.setType(this.type);

        model.setDepts(JsonUtil.parseArray(this.depts, new TypeReference<List<StaffOrgRulesModel.Dept>>() {}));
        model.setCenters(JsonUtil.parseArray(this.centers, new TypeReference<List<StaffOrgRulesModel.Center>>() {}));
        model.setScopes(JsonUtil.parseArray(this.scope, new TypeReference<List<StaffOrgRulesModel.ScopeItem>>() {}));

        model.setCreatedTime(TimeUtil.format(this.createdTime));
        model.setUpdatedTime(TimeUtil.format(this.updatedTime));
        return model;
    }
}
