package com.ds.system.staff.entity;

import com.ds.system.staff.service.domain.StaffInfoModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 员工信息表 JPA 实体
 *
 * @author ds
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "sys_staff_info")
public class StaffInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int")
    private Integer id;

    /** 工号 */
    @Column(name = "staff_no", length = 64, nullable = false)
    private String staffNo;

    /** 姓名 */
    @Column(name = "staff_name", length = 64, nullable = false)
    private String staffName;

    /** 部门 */
    @Column(length = 128)
    private String department;

    /** 企业 */
    @Column(name = "company_name", length = 64)
    private String companyName;

    /** 部门编码 */
    @Column(name = "depart_code", length = 20)
    private String departCode;

    /** 工作岗位 */
    @Column(name = "job_station", length = 128)
    private String jobStation;

    /** 团队ID */
    @Column(name = "team_id")
    private Integer teamId;

    /** 人员所在组织id */
    @Column(name = "o_id", length = 20)
    private String orgId;

    /** 人员所在上级组织id */
    @Column(name = "c_id", length = 32)
    private String ctId;

    /** 所属团队 */
    @Column(name = "team_name", length = 256)
    private String teamName;

    /** 中心 */
    @Column(length = 128)
    private String center;

    /** 电话 */
    @Column(name = "phone_num", length = 64)
    private String phoneNum;

    /** 邮箱 */
    @Column(length = 64)
    private String email;

    /** 员工在职状态 0-离职 1-在职 */
    @Column(columnDefinition = "tinyint default 1")
    private Integer enabled;

    public StaffInfoModel toModel() {
        StaffInfoModel model = new StaffInfoModel();
        model.setId(this.id);
        model.setStaffNo(this.staffNo);
        model.setStaffName(this.staffName);
        model.setDepartment(this.department);
        model.setCompanyName(this.companyName);
        model.setDepartCode(this.departCode);
        model.setJobStation(this.jobStation);
        model.setTeamId(this.teamId);
        model.setOrgId(this.orgId);
        model.setCtId(this.ctId);
        model.setTeamName(this.teamName);
        model.setCenter(this.center);
        model.setPhoneNum(this.phoneNum);
        model.setEmail(this.email);
        model.setEnabled(this.enabled);
        return model;
    }
}
