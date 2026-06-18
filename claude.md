DS 后端工程

## 项目简介

DS 是一个数据管理平台，提供定时任务调度、任务执行、Worker 管理等核心功能。

## 工程结构
ds-backend/
├── ds-common/ # 公共模块：工具类、通用配置、基础实体
├── ds-protocol/ # 协议模块：Worker 通信协议定义
├── ds-system/ # 系统模块：用户、角色、权限、组织管理
├── ds-scheduler/ # 调度模块：定时任务管理、任务调度
├── ds-task/ # 任务模块：任务分发、Worker 管理、任务执行监控
├── ds-worker/ # Worker 模块：任务执行节点
├── ds-server/ # 服务启动模块：整合所有模块的启动入口
└── pom.xml # 父工程依赖管理


## 模块职责

| 模块 | 职责 |
|------|------|
| `ds-common` | 提供全局工具类、通用配置、基础实体类（如 BaseEntity、响应封装） |
| `ds-protocol` | 定义 Worker 与 Server 之间的通信协议（如心跳、任务执行、结果回传） |
| `ds-system` | 用户管理、角色管理、权限控制、组织机构管理（基于 Spring Security） |
| `ds-scheduler` | 基于 Quartz 的定时任务管理，包括任务创建、调度、暂停、恢复 |
| `ds-task` | 任务分发策略、Worker 节点管理、任务执行状态监控与日志 |
| `ds-worker` | 任务执行节点，负责拉取任务、执行具体业务逻辑、上报结果 |
| `ds-server` | Spring Boot 启动模块，整合上述模块，包含配置文件和迁移脚本 |

## 服务启动模块 (`ds-server`) 详情

- **启动类**：`DsServerApp`
- **配置文件**：
  - `application.yml`（主配置）
  - `application-dev.yml`（开发环境）
  - `application-prod.yml`（生产环境）
- **数据库迁移**：`db/migration/`（Flyway 脚本）

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21 |
| 核心框架 | Spring Boot | 3.2.x |
| 数据库 | MySQL | 8.0+ |
| ORM | MyBatis Plus | 3.5.x |
| 数据库迁移 | Flyway | (Spring Boot 内置) |
| 定时任务 | Quartz | (Spring Boot 集成) |
| 安全框架 | Spring Security | (Spring Boot 内置) |
| API 文档 | SpringDoc OpenAPI | (springdoc-openapi-starter-webmvc-ui) |

## 环境要求

- JDK 21+
- Maven 3.9+
- MySQL 8.0+

## 注意事项



1.当前位置已经在ds-backend/下，请不要重复生成ds-backend目录
2.请先生成项目结构，并在ds-server下生成启动类DsServerApp
3.yml文件的数据源配置如下
```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.220.4:3306/ds_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: ds_admin
    password: ds_admin
    driver-class-name: com.mysql.cj.jdbc.Driver
```
