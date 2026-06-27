---
name: project-team-agent
description: >-
  プロジェクトチーム統括 — PM / BA / DevLead の3エージェント連携。顧客要求→BA→PM→DevLead→PM→顧客。矛盾検知・フェーズ管理。Use for プロジェクトチーム, 3エージェント, PM BA DevLead 連携, 要件から見積まで一括.
disable-model-invocation: true
---

# Project Team Agent（纏め / 統括）

Subagent: **project-team-agent**

## チーム

| Agent | 役割 |
|-------|------|
| [pm-agent](../../agents/pm-agent.md) | 計画・進捗・リスク・顧客調整 |
| [ba-agent](../../agents/ba-agent.md) | 要件・フロー・要件定義・変更影響 |
| [devlead-agent](../../agents/devlead-agent.md) | 技術決定・見積・技術リスク・仕様 |

## 標準フロー

`顧客要求 → BA → PM → DevLead → PM → 顧客`

## 呼び方

```
@project-team-agent 新規ECリニューアル。顧客要求は〇〇。まず要件整理から。
```

```
@project-team-agent 続き（次フェーズへ）
```

詳細ルール・応答フォーマット: [../../agents/project-team-agent.md](../../agents/project-team-agent.md)
