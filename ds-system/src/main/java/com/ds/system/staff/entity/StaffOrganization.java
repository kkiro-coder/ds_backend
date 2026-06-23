package com.ds.system.staff.entity;

import com.ds.system.staff.service.domain.StaffOrganizationModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 组织架构信息表 JPA 实体
 *
 * @author ds
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "sys_staff_organization")
public class StaffOrganization implements Serializable {

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

    /** 组织类型 1-一级部门 2-二级部门 3-中心 4-团队 */
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

    public StaffOrganizationModel toModel() {
        StaffOrganizationModel model = new StaffOrganizationModel();
        model.setId(this.id);
        model.setOrgId(this.orgId);
        model.setCtId(this.ctId);
        model.setTitle(this.title);
        model.setType(this.type);
        model.setLeader1(this.leader1);
        model.setLeader2(this.leader2);
        model.setCenterName(this.centerName);
        model.setCenterHead(this.centerHead);
        model.setDeptName(this.deptName);
        model.setStatus(this.status);
        model.setDirector(this.director);
        model.setCompanyName(this.companyName);
        return model;
    }
}
