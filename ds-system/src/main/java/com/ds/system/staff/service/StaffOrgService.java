package com.ds.system.staff.service;

import com.ds.system.staff.dao.StaffInfoDao;
import com.ds.system.staff.dao.StaffOrganizationDao;
import com.ds.system.staff.entity.StaffInfo;
import com.ds.system.staff.entity.StaffOrganization;
import com.ds.system.staff.service.domain.StaffInfoModel;
import com.ds.system.staff.service.domain.StaffOrganizationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组织架构与员工信息服务
 *
 * @author ds
 */
@Service
@RequiredArgsConstructor
public class StaffOrgService {

    private final StaffOrganizationDao staffOrganizationDao;
    private final StaffInfoDao staffInfoDao;

    /**
     * 构建组织架构树（含子组织及员工），递归构建多级 children
     *
     * @return 顶层组织列表
     */
    public List<StaffOrganizationModel> buildOrgTree() {
        List<StaffOrganization> allOrgs = staffOrganizationDao.findAll();
        List<StaffInfo> allStaff = staffInfoDao.findAll();

        // 员工按 orgId 分组
        Map<String, List<StaffInfoModel>> staffMap = allStaff.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getOrgId() != null ? s.getOrgId() : "",
                        Collectors.mapping(StaffInfo::toModel, Collectors.toList())
                ));

        // 实体转 Model，放入 Map
        Map<String, StaffOrganizationModel> modelMap = allOrgs.stream()
                .map(StaffOrganization::toModel)
                .collect(Collectors.toMap(StaffOrganizationModel::getOrgId, m -> m));

        // 递归挂载 children 和 staffList
        List<StaffOrganizationModel> roots = new ArrayList<>();
        for (StaffOrganizationModel model : modelMap.values()) {
            model.setStaffList(staffMap.getOrDefault(model.getOrgId(), Collections.emptyList()));
            String ctId = model.getCtId();
            if (ctId == null) {
                roots.add(model);
            } else {
                StaffOrganizationModel parent = modelMap.get(ctId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(model);
                }
            }
        }
        return roots;
    }

    /**
     * 根据组织架构重新计算全量员工的 ctId、teamName、center 字段并更新入库
     */
    @Transactional
    public void refreshStaffOrgInfo() {
        List<StaffOrganization> allOrgs = staffOrganizationDao.findAll();
        List<StaffInfo> allStaff = staffInfoDao.findAll();

        // orgId → 组织
        Map<String, StaffOrganization> orgMap = allOrgs.stream()
                .collect(Collectors.toMap(StaffOrganization::getOrgId, o -> o));

        for (StaffInfo staff : allStaff) {
            String orgId = staff.getOrgId();
            if (orgId == null) {
                continue;
            }
            StaffOrganization org = orgMap.get(orgId);
            if (org == null) {
                continue;
            }
            // ctId = 所属组织的上级组织id
            staff.setCtId(org.getCtId());
            // teamName = 所属组织的 title
            staff.setTeamName(org.getTitle());
            // center = 上级组织的 title
            String parentTitle = "";
            if (org.getCtId() != null) {
                StaffOrganization parentOrg = orgMap.get(org.getCtId());
                if (parentOrg != null) {
                    parentTitle = parentOrg.getTitle();
                }
            }
            staff.setCenter(parentTitle);
        }
        staffInfoDao.saveAll(allStaff);

        // 重新计算组织的 centerName、centerHead（仅 team 类型需要）
        for (StaffOrganization org : allOrgs) {
            if (org.getType() != null && org.getType() == 4 && org.getCtId() != null) {
                StaffOrganization parentOrg = orgMap.get(org.getCtId());
                if (parentOrg != null) {
                    org.setCenterName(parentOrg.getTitle());
                    org.setCenterHead(parentOrg.getDirector());
                }
            }
        }
        staffOrganizationDao.saveAll(allOrgs);
    }

    
}
