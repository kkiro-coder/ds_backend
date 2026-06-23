package com.ds.system.staff.service.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 员工信息 Model
 *
 * @author ds
 */
@Data
@NoArgsConstructor
public class StaffInfoModel implements Serializable {

    private Integer id;

    /** 工号 */
    private String staffNo;

    /** 姓名 */
    private String staffName;

    /** 部门 */
    private String department;

    /** 企业 */
    private String companyName;

    /** 部门编码 */
    private String departCode;

    /** 工作岗位 */
    private String jobStation;

    /** 团队ID */
    private Integer teamId;

    /** 人员所在组织id */
    private String orgId;

    /** 人员所在上级组织id */
    private String ctId;

    /** 所属团队 */
    private String teamName;

    /** 中心 */
    private String center;

    /** 电话 */
    private String phoneNum;

    /** 邮箱 */
    private String email;

    /** 员工在职状态 0-离职 1-在职 */
    private Integer enabled;
}
