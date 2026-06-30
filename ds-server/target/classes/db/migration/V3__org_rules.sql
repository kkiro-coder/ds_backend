CREATE TABLE `sys_staff_org_rules` (
    `id`          int auto_increment comment '主键'
        primary key,
    `rule_id`     varchar(64)  NOT NULL COMMENT '实体唯一标识符 (对应根对象 id)',
    `name`        varchar(255) NOT NULL COMMENT '实体名称 (对应根对象 name，如 "华泰联合")',
    `type`        varchar(50)  DEFAULT NULL COMMENT '实体类型 (对应根对象 type，如 "manual")',
    `depts`       text         DEFAULT NULL COMMENT '部门列表数据。JSON结构示例：[{"id":"string", "name":"string", "co_id":"string", "co_name":"string"}]',
    `centers`     text         DEFAULT NULL COMMENT '中心与团队组织数据。JSON结构示例：[{"leaders":[], "teams":[{"uid":"string", "name":"string", "leaders":[{"badge":"string","name":"string"}], "members":[{"badge":"string","name":"string","email":"string","resid":"string","co_name":"string","dept_name":"string","status":"string","active":true|false,"teams":[]}]}]}]',
    `scope`       text         DEFAULT NULL COMMENT '组织维度范围数据。JSON结构示例：[{"id":"string","name":"string","type":"dept|center","co_id":"string","co_name":"string","dept_id":null|"string","dept_name":null|"string","or_id":null|"string"}]',
    `created_time`  datetime     DEFAULT NULL COMMENT '创建时间',
    `updated_time`  datetime     DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业组织架构与范围配置表';
