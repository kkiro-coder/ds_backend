package com.ds.system.staff.dao;

import com.ds.system.staff.entity.StaffOrgStd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 组织架构标准表 JPA Dao
 *
 * @author ds
 */
@Repository
public interface StaffOrgStdDao
        extends JpaRepository<StaffOrgStd, Integer>, JpaSpecificationExecutor<StaffOrgStd> {

    Optional<StaffOrgStd> findByOrgId(String orgId);

    List<StaffOrgStd> findByCtId(String ctId);

    List<StaffOrgStd> findByType(Integer type);

    List<StaffOrgStd> findByCtIdAndType(String ctId, Integer type);
}
