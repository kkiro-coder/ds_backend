package com.ds.system.staff.dao;

import com.ds.system.staff.entity.StaffOrgRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 企业组织架构与范围配置 JPA Dao
 *
 * @author ds
 */
@Repository
public interface StaffOrgRulesDao
        extends JpaRepository<StaffOrgRules, Integer>, JpaSpecificationExecutor<StaffOrgRules> {

    Optional<StaffOrgRules> findByRuleId(String ruleId);

    List<StaffOrgRules> findByName(String name);

    List<StaffOrgRules> findByType(String type);
}
