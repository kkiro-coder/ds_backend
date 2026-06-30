package com.ds.system.staff.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 组织架构标准表 JPA 实体
 *
 * @author ds
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "sys_staff_org_std")
public class StaffOrgStd implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int")
    private Integer id;

    /** 组织id */
    @Column(name = "o_id", length = 32)
    private String orgId;

    /** 上级组织id */
    @Column(name = "c_id", length = 32)
    private String ctId;

    /** 中心/团队名称 */
    @Column(length = 128, nullable = false)
    private String title;

    /** 组织类型 1-一级部门 3-中心 4-团队 */
    @Column(nullable = false, columnDefinition = "tinyint")
    private Integer type;

    /** 领导人1（staff_no） */
    @Column(length = 64)
    private String leader1;

    /** 领导人2（staff_no） */
    @Column(length = 64)
    private String leader2;

    /** 中心名称（针对类型为团队的组织架构存储） */
    @Column(name = "center_name", length = 128)
    private String centerName;

    /** 中心负责人（staff_no，针对类型为团队的组织架构存储） */
    @Column(name = "center_head", length = 64)
    private String centerHead;

    /** 部门名称 */
    @Column(name = "dept_name", length = 64)
    private String deptName;

    /** 状态 0-下线 1-正常 */
    @Column(columnDefinition = "tinyint default 1")
    private Integer status;

    /** 团队所属部门负责人（staff_no） */
    @Column(length = 20)
    private String director;

    /** 企业 */
    @Column(name = "company_name", length = 64)
    private String companyName;
}
