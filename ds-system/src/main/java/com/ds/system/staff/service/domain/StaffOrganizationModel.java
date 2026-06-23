package com.ds.system.staff.service.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 组织架构 Model（含子组织和员工列表，用于构建树形结构）
 *
 * @author ds
 */
@Data
@NoArgsConstructor
public class StaffOrganizationModel implements Serializable {

    private Integer id;

    /** 组织id */
    private String orgId;

    /** 上级组织id */
    private String ctId;

    /** 中心/团队名称 */
    private String title;

    /** 组织类型 1-一级部门 2-二级部门 3-中心 4-团队 */
    private Integer type;

    /** 领导人1（staff_no） */
    private String leader1;

    /** 领导人2（staff_no） */
    private String leader2;

    /** 中心名称（针对类型为团队的组织架构存储） */
    private String centerName;

    /** 中心负责人（staff_no，针对类型为团队的组织架构存储） */
    private String centerHead;

    /** 部门名称 */
    private String deptName;

    /** 状态 0-下线 1-正常 */
    private Integer status;

    /** 团队所属部门负责人（staff_no） */
    private String director;

    /** 企业 */
    private String companyName;

    /** 子组织列表 */
    private List<StaffOrganizationModel> children;

    /** 当前组织下的人员列表 */
    private List<StaffInfoModel> staffList;
}
