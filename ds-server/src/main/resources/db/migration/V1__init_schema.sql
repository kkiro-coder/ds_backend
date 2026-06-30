create table sys_staff_organization
(
    id          int auto_increment comment '主键'
        primary key,
    o_id        varchar(32)      null comment '组织id',
    c_id        varchar(32)      null comment '上级组织id',
    title       varchar(128)     not null comment '中心/团队名称',
    type        tinyint          not null comment '组织类型 1-一级部门 2-二级部门 3-中心、4-团队',
    leader1     varchar(64)      default '' null comment '领导人1',
    leader2     varchar(64)      default '' null comment '领导人2',
    center_name varchar(128)     default '' null comment '中心名称(针对类型为团队的组织架构存储)',
    center_head varchar(64)      default '' null comment '中心负责人(针对类型为团队的组织架构存储)',
    dept_name   varchar(64)      default '' null comment '部门名称',
    status      tinyint          default 1 not null comment '状态 0-下线 1-正常',
    director    varchar(20)      default '' null comment '团队所属部门负责人',
    company_name varchar(64)     default '' null comment '企业'
)
    comment '组织架构信息表' charset = utf8mb4;

    create table sys_staff_info
(
    id          int auto_increment comment '主键'
        primary key,
    staff_no    varchar(64)      not null comment '工号',
    staff_name  varchar(64)      not null comment '姓名',
    department  varchar(128)     default '' null comment '部门',
    company_name varchar(64)     default '' null comment '企业',
    depart_code varchar(20)      default '' null comment '部门编码',
    job_station varchar(128)     default '' null comment '工作岗位',
    team_id     int              null comment '团队ID',
    o_id        varchar(20)      null comment '人员所在组织id',
    c_id        varchar(32)      null comment '人员所在上级组织id',
    team_name   varchar(256)     default '' null comment '所属团队',
    center      varchar(128)     default '' null comment '中心',
    phone_num   varchar(64)      default '' null comment '电话',
    email       varchar(64)      default '' null comment '邮箱',
    enabled     tinyint          default 1 null comment '员工在职状态 0-离职 1-在职'
)
    comment '员工信息表' charset = utf8mb4;

    create table sys_staff_org_std
(
    id          int auto_increment comment '主键'
        primary key,
    o_id        varchar(32)      null comment '组织id',
    c_id        varchar(32)      null comment '上级组织id',
    title       varchar(128)     not null comment '中心/团队名称',
    type        tinyint          not null comment '组织类型 1-一级部门  3-中心、4-团队',
    leader1     varchar(64)      default '' null comment '领导人1',
    leader2     varchar(64)      default '' null comment '领导人2',
    center_name varchar(128)     default '' null comment '中心名称(针对类型为团队的组织架构存储)',
    center_head varchar(64)      default '' null comment '中心负责人(针对类型为团队的组织架构存储)',
    dept_name   varchar(64)      default '' null comment '部门名称',
    status      tinyint          default 1 not null comment '状态 0-下线 1-正常',
    director    varchar(20)      default '' null comment '团队所属部门负责人',
    company_name varchar(64)     default '' null comment '企业'
)
    comment '组织架构信息表标准' charset = utf8mb4;

    create table sys_staff_info_std
(
    id          int auto_increment comment '主键'
        primary key,
    staff_no    varchar(64)      not null comment '工号',
    staff_name  varchar(64)      not null comment '姓名',
    department  varchar(128)     default '' null comment '部门',
    company_name varchar(64)     default '' null comment '企业',
    depart_code varchar(20)      default '' null comment '部门编码',
    job_station varchar(128)     default '' null comment '工作岗位',
    team_id     int              null comment '团队ID',
    o_id        varchar(20)      null comment '人员所在组织id',
    c_id        varchar(32)      null comment '人员所在上级组织id',
    team_name   varchar(256)     default '' null comment '所属团队',
    center      varchar(128)     default '' null comment '中心',
    phone_num   varchar(64)      default '' null comment '电话',
    email       varchar(64)      default '' null comment '邮箱',
    enabled     tinyint          default 1 null comment '员工在职状态 0-离职 1-在职'
)
    comment '员工信息表标准' charset = utf8mb4;