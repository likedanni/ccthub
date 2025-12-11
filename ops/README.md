# ops 目录说明

该目录用于存放 ops 相关内容。

## AI 配置（新增）

仓库默认 AI 模型配置存放在 `ai-defaults.yaml`（示例路径：`ops/ai-defaults.yaml`）。

- 使用方式：在 CI/容器中将 `AI_DEFAULT_MODEL` 环境变量设置为配置中 `default.model` 的值，例如：

```bash
# 在 bash 中导出环境变量
export AI_DEFAULT_MODEL=claude-haiku-4.5
```

- 在 Kubernetes 中，可使用 ConfigMap/Secret 注入至 Pod 环境：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
	name: ai-config
data:
	AI_DEFAULT_MODEL: "claude-haiku-4.5"
```

- 注意：`claude-haiku-4.5` 为仓库内使用的短标识；运行时请根据实际 SDK/Provider 要求映射为正确的 provider model name 或者使用 provider-specific 配置项。

如果需要，我可以把 CI 配置示例（GitHub Actions）也补充到仓库中。
