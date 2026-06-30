package com.ds.system.staff.dao;

import com.ds.system.staff.entity.StaffInfoStd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 员工信息标准表 JPA Dao
 *
 * @author ds
 */
@Repository
public interface StaffInfoStdDao
        extends JpaRepository<StaffInfoStd, Integer>, JpaSpecificationExecutor<StaffInfoStd> {

    Optional<StaffInfoStd> findByStaffNo(String staffNo);

    List<StaffInfoStd> findByStaffNameContaining(String staffName);

    List<StaffInfoStd> findByOrgId(String orgId);

    List<StaffInfoStd> findByCtId(String ctId);

    List<StaffInfoStd> findByEnabled(Integer enabled);

    List<StaffInfoStd> findByOrgIdAndEnabled(String orgId, Integer enabled);
}
