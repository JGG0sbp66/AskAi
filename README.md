# AskAI - Minecraft 智能对话插件

[![GitHub license](https://img.shields.io/github/license/JGG0sbp66/AskAi?style=flat-square)](https://github.com/JGG0sbp66/AskAi) [![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21-blue?style=flat-square)](https://minecraft.net)

## 📺 演示视频
[![演示视频封面](https://i0.hdslb.com/bfs/archive/0db87b772c4f893423abac14fe6ac01fcc44ddb0.jpg@672w_378h_1c.avif)](https://www.bilibili.com/video/BV1iUPyeXES3)

## 🌟 项目简介
一个基于大语言模型的 Minecraft 插件，为游戏内添加智能对话功能：
- 🎮 **游戏内原生集成** - 通过命令直接与 AI 交互
- ⚡ **流式响应** - 支持 BossBar 和 ActionBar 两种实时显示模式
- 🧠 **上下文记忆** - 为每个玩家保存独立对话历史
- 🔧 **低延迟架构** - 异步请求不阻塞主线程

## 📜 使用说明
### 指令语法
```mc
/askai [显示模式] [可见范围] <问题内容>
```

### 参数详解
| 参数类型 | 可选值 | 默认值 | 说明 |
|---------|--------|--------|------|
| 显示模式 | `bossbar`/`actionbar` | `actionbar` | 选择BossBar进度条或ActionBar显示 |
| 可见范围 | `all`/`self` | `self` | 设置消息全服可见或仅自己可见 |
| 问题内容 | 任意文本 | 必填 | 需要询问AI的内容 |

### 使用示例
```mc
/askai bossbar all 如何建造下界传送门？
/askai actionbar self 告诉我钻石的生成规律
/askai 你是一只猫娘  # 使用默认参数
```

### 智能补全
- 输入时按`Tab`键自动补全参数
- 所有参数支持任意顺序组合
- 未指定参数时将使用默认配置

## 📥 安装指南
1. 下载最新版本 `AskAI.jar`
2. 放入服务器 plugins 文件夹
3. 配置 API 端点：
```yaml
# config.yml
AskAiAPI:
  BASE_URL: "https://your-ai-api.com"
  model: "gpt-3.5-turbo"
```

## 🌐 项目链接
- [GitHub 仓库](https://github.com/JGG0sbp66/AskAi)
- [问题反馈](https://github.com/JGG0sbp66/AskAi/issues)
- [视频教程](https://www.bilibili.com/video/BV1iUPyeXES3)
