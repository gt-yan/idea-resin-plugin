# Resin (Community) — IntelliJ IDEA Plugin

为 IntelliJ IDEA 提供 [Resin](https://caucho.com/) 应用服务器集成，可在 IDE 内配置 Resin、部署 Artifact、启动/停止服务器并调试 Java 与 JSP 代码。

JetBrains 官方 Resin 插件（Marketplace ID 14598，plugin id `com.intellij.resin`）在 **IDEA 2023.1** 之后停止更新。本项目基于其最后版本迁移而来，让插件继续运行在新版 IDEA 上。

| 项 | 值 |
|---|---|
| 插件 ID | `io.github.gt-yan.resin` |
| 插件名称 | Resin (Community) |
| 兼容范围 | **IntelliJ IDEA Ultimate 2023.1 ~ 2026.3** |
| since-build / until-build | `232` / `263.*` |
| 编译 JDK | JDK 17 |
| 开源协议 | Apache-2.0 |

> 依赖 IDEA Ultimate 内置的 Jakarta EE / Application Servers 模块，Community 版不可用。

---

## 功能

- 在 **Settings → Application Servers** 中添加 Resin 安装目录
- 创建本地 / 远程 Resin 运行配置
- Web 应用 Artifact 的部署与取消部署
- 服务器启动、停止与状态监控
- Java 与 JSP 断点调试（含 JMX 远程调试）
- 自动探测 Resin 2.x / 3.x / 4.x / 5.x 并生成对应配置

---

## 安装

### 从 JetBrains Marketplace 安装

在 IDEA 中打开 **Settings → Plugins → Marketplace**，搜索 **Resin (Community)**，点击 Install。

### 从本地构建包安装

1. 构建插件（见下节），得到 `build/distributions/idea-resin-plugin-<version>.zip`
2. **Settings → Plugins → ⚙️ → Install Plugin from Disk...**，选择该 zip
3. 重启 IDEA

验证：**Settings → Build, Execution, Deployment → Application Servers → +** 列表中出现 Resin。

---

## 构建

环境：JDK 17+、IntelliJ IDEA Ultimate 2023.3.1（编译基准）。Gradle 通过 Wrapper 自动获取，无需单独安装。

```bash
# Windows
gradlew.bat buildPlugin

# Linux / macOS
./gradlew buildPlugin
```

产物：`build/distributions/idea-resin-plugin-1.0.5.zip`

> [build.gradle.kts](build.gradle.kts) 默认通过 `local(...)` 使用本机安装的
> IDEA 2023.3.1 作为编译基准。若网络可访问 JetBrains CDN，可改为
> `intellijIdeaUltimate("2026.2.3")` 从远端解析平台依赖。

### 发布相关任务

```bash
gradlew.bat verifyPlugin      # 用 Plugin Verifier 校验兼容性
gradlew.bat signPlugin        # 代码签名（需要证书环境变量）
gradlew.bat publishPlugin     # 上传到 JetBrains Marketplace
```

详见 [doc/PUBLISHING.md](../doc/PUBLISHING.md)。

---

## 项目结构

```
idea-resin-plugin/
├── build.gradle.kts          # IntelliJ Platform Gradle Plugin 2.19.0
├── settings.gradle.kts       # 项目设置（JDK 工具链解析）
├── gradle.properties         # Gradle 与本地 JDK 路径
├── gradlew / gradlew.bat     # Gradle Wrapper
├── LICENSE                   # Apache-2.0
├── NOTICE                    # 来源与商标声明
└── src/main/
    ├── java/org/intellij/j2ee/web/resin/
    │   ├── ResinManager.java              # 服务器集成入口
    │   ├── ResinConfigurationType.java    # 运行配置类型
    │   ├── ResinConfigurationProducer.java# 配置生产者
    │   ├── ResinModel / ResinRemoteModel  # 本地 / 远程运行模型
    │   ├── ResinServerInstance.java       # 服务器进程管理
    │   ├── ResinStartupPolicy.java        # 启动命令行构建
    │   ├── ui/                            # 配置编辑器与对话框
    │   └── resin/                         # 安装校验、版本探测、配置策略、JMX
    └── resources/
        ├── META-INF/plugin.xml            # 插件描述符
        ├── icons/                         # 插件图标
        ├── messages/ResinBundle.properties
        └── org/.../resin/                 # 启动参数、版本映射、配置模板
```

### 扩展点

| 扩展点 | 实现类 | 作用 |
|---|---|---|
| `configurationType` | `ResinConfigurationType` | 注册 Resin 运行配置类型 |
| `configurationProducer` | `ResinConfigurationProducer` | 从项目上下文生成配置 |
| `javaee.app.servers.integration.appServerIntegration` | `ResinManager` | Resin 服务器集成入口 |

---

## 开源说明

本项目以 **Apache License 2.0** 发布，完整文本见 [LICENSE](LICENSE)。

代码来源：JetBrains 官方 Resin 插件（Apache-2.0）最后版本 `232.6734.9` 的反编译源码，经以下改动后重新发布：

- 迁移到 IntelliJ Platform Gradle Plugin 2.x 工程结构
- 提取嵌套模型数据类、清除反编译生成的合成成员
- 加固 Resin 版本号解析，兼容 4.x / 5.x 及后续版本
- 扩展支持 IDE 构建范围至 2026.x

详细来源与商标声明见 [NOTICE](NOTICE)。本项目与 JetBrains s.r.o.、Caucho Technology 无隶属或背书关系。

欢迎提交 Issue 与 Pull Request。

---

## 技术栈

- Gradle 9.7.1 + IntelliJ Platform Gradle Plugin 2.19.0
- Java 17
- Jakarta EE Application Servers 集成 API
