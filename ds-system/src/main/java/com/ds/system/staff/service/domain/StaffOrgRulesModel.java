package com.ds.system.staff.service.domain;

import com.ds.common.utils.JsonUtil;
import com.ds.common.utils.TimeUtil;
import com.ds.system.staff.entity.StaffOrgRules;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 企业组织架构与范围配置 Model
 *
 * @author ds
 */
@Getter
@Setter
@NoArgsConstructor
public class StaffOrgRulesModel implements Serializable {

    private Integer id;

    /** 实体唯一标识符 */
    private String ruleId;

    /** 实体名称 */
    private String name;

    /** 实体类型 */
    private String type;

    /** 部门列表数据 */
    private List<Dept> depts;

    /** 中心与团队组织数据 */
    private List<Center> centers;

    /** 组织维度范围数据 */
    private List<ScopeItem> scopes;

    /** 创建时间 */
    private String createdTime;

    /** 更新时间 */
    private String updatedTime;

    // ==================== 内部类 ====================

    /**
     * 部门信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Dept implements Serializable {

        /** 部门id */
        private String id;

        /** 部门名称 */
        private String name;

        /** 企业id */

        private String coId;

        /** 企业名称 */

        private String coName;
    }

    /**
     * 中心信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Center implements Serializable {

        /** 中心唯一标识 */
        private String uid;

        /** 中心名称 */
        private String name;

        /** 中心负责人列表 */
        private List<Leader> leaders;

        /** 下属团队列表 */
        private List<Team> teams;
    }

    /**
     * 团队信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Team implements Serializable {

        /** 团队唯一标识 */
        private String uid;

        /** 团队名称 */
        private String name;

        /** 团队负责人列表 */
        private List<Leader> leaders;

        /** 团队成员列表 */
        private List<Member> members;
    }

    /**
     * 负责人信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Leader implements Serializable {

        /** 工号 */
        private String badge;

        /** 姓名 */
        private String name;
    }

    /**
     * 成员信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Member implements Serializable {

        /** 工号 */
        private String badge;

        /** 姓名 */
        private String name;

        /** 邮箱 */
        private String email;

        /** 所属部门id */
        private String resid;

        /** 企业名称 */

        private String coName;

        /** 部门名称 */

        private String deptName;

        /** 在职状态 */
        private String status;

        /** 是否在职 */
        private Boolean active;

        /** 所属团队列表 */
        private List<String> teams;
    }

    /**
     * 组织维度范围
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ScopeItem implements Serializable {

        /** 组织id */
        private String id;

        /** 组织名称 */
        private String name;

        /** 类型: dept / center */
        private String type;

        /** 企业id */
        private String coId;

        /** 企业名称 */
        private String coName;

        /** 所属部门id */
        private String deptId;

        /** 所属部门名称 */
        private String deptName;

        private Integer originId;
    }

    // ==================== 业务方法 ====================

    /**
     * 拷贝为新增规则用的 Model（去掉 id、ruleId、createdTime、updatedTime）
     */
    public StaffOrgRulesModel toAddRule() {
        StaffOrgRulesModel copy = new StaffOrgRulesModel();
        copy.setName(this.name);
        copy.setType(this.type);
        copy.setDepts(this.depts);
        copy.setCenters(this.centers);
        copy.setScopes(this.scopes);
        return copy;
    }

    /**
     * 转为新增实体（自动生成 ruleId）
     */
    public StaffOrgRules toEntity() {
        StaffOrgRules entity = new StaffOrgRules();
        entity.setRuleId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        entity.setName(this.name);
        entity.setType(this.type);
        entity.setDepts(JsonUtil.toJson(this.depts));
        entity.setCenters(JsonUtil.toJson(this.centers));
        entity.setScope(JsonUtil.toJson(this.scopes));
        entity.setCreatedTime(TimeUtil.now());
        entity.setUpdatedTime(TimeUtil.now());
        return entity;
    }
}
