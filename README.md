# AI-Native CRM (Based on CordysCRM)

[![Java 8](https://img.shields.io/badge/JDK-1.8-green)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.x-blue)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue-3.x-brightgreen)](https://vuejs.org/)

> **新一代开源 AI CRM 系统** > 集信息化、数字化、智能化于一体，构建从线索到回款 (L2C) 的全流程精细化管理闭环。

---

## 📖 项目简介 (Introduction)

本项目是一个企业级 **模块化单体 (Modular Monolith)** CRM 系统。它旨在帮助企业实现**销售运营闭环**，覆盖线索获取、智能分配、客户管理、商机跟进、合同签约及回款执行。

**核心优势：**

* **⚡ 灵活易用**：现代化技术栈，支持模块化配置与 RBAC 权限管控。支持企微/钉钉无缝集成。
* **🔒 安全可控**：私有化部署设计，数据主权完全掌握在企业手中。
* **🤖 AI 加持**：基于 **MCP (Model Context Protocol)** 协议，预留 AI Agent 接口。支持接入标准 OpenAI 接口实现智能跟进、报价。
* **📊 BI 融合**：内建数据分析接口，支持销售漏斗、回款归因分析。

---

## 🏗️ 项目结构与模块说明 (Project Structure)

本项目采用 **Maven 多模块** 构建，遵循严格的分层架构设计。

```text
ai-crm-project
├── crm-common           # [核心基座] 公共工具模块
├── crm-system           # [权限中心] IAM 与 系统管理模块
├── crm-business         # [业务核心] L2C 核心业务流程模块
├── crm-ai               # [智能引擎] AI 与 MCP 协议实现模块
├── crm-ui               # [前端工程] Vue3 + Naive UI
└── pom.xml              # [依赖管理] 父工程 BOM 文件