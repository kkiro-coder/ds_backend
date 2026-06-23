package com.ds.system.staff.dao;

import com.ds.system.staff.entity.StaffInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 员工信息 JPA Repository
 *
 * @author ds
 */
@Repository
public interface StaffInfoDao
        extends JpaRepository<StaffInfo, Integer>, JpaSpecificationExecutor<StaffInfo> {

    /**
     * 根据工号查询
     */
    Optional<StaffInfo> findByStaffNo(String staffNo);

    /**
     * 根据姓名模糊查询
     */
    List<StaffInfo> findByStaffNameContaining(String staffName);

    /**
     * 根据所属组织id查询
     */
    List<StaffInfo> findByOrgId(String orgId);

    /**
     * 根据上级组织id查询
     */
    List<StaffInfo> findByCtId(String ctId);

    /**
     * 根据在职状态查询
     */
    List<StaffInfo> findByEnabled(Integer enabled);

    /**
     * 根据部门查询
     */
    List<StaffInfo> findByDepartment(String department);

    /**
     * 根据团队名称查询
     */
    List<StaffInfo> findByTeamName(String teamName);

    /**
     * 根据中心查询
     */
    List<StaffInfo> findByCenter(String center);

    /**
     * 根据组织id和在职状态查询
     */
    List<StaffInfo> findByOrgIdAndEnabled(String orgId, Integer enabled);

    /**
     * 根据工号或姓名模糊查询
     */
    List<StaffInfo> findByStaffNoContainingOrStaffNameContaining(String staffNo, String staffName);
}
