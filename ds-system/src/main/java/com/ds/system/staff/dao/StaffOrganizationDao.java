package com.ds.system.staff.dao;

import com.ds.system.staff.entity.StaffOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 组织架构 JPA Repository
 *
 * @author ds
 */
@Repository
public interface StaffOrganizationDao
        extends JpaRepository<StaffOrganization, Integer>, JpaSpecificationExecutor<StaffOrganization> {

    /**
     * 根据组织id查询
     */
    Optional<StaffOrganization> findByOrgId(String orgId);

    /**
     * 根据上级组织id查询所有下级组织
     */
    List<StaffOrganization> findByCtId(String ctId);

    /**
     * 根据组织类型查询
     */
    List<StaffOrganization> findByType(Integer type);

    /**
     * 根据状态查询
     */
    List<StaffOrganization> findByStatus(Integer status);

    /**
     * 根据企业名称查询
     */
    List<StaffOrganization> findByCompanyName(String companyName);

    /**
     * 根据上级组织id和组织类型查询
     */
    List<StaffOrganization> findByCtIdAndType(String ctId, Integer type);
}
