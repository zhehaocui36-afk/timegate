# 需求追踪矩阵（S02）

> **版本** v1.0 · **状态** 已冻结（G1 出口） · **更新** 2026-11-09
> 建立「需求 R-xx → 测试用例 ID → 验收方式」映射。S12 按本表补齐测试；S14 发布前据此核对 P0 覆盖率。
>
> 测试用例明细见 `docs/TEST-PLAN.md`。

## 验收方式编码

| 编码 | 含义 | 执行者 |
|---|---|---|
| UNIT | 单元测试（JVM） | S06/S07/S08/S09/S12 |
| INST | 仪器测试（Compose / Room，模拟器或设备） | S12 |
| DEVICE | 真机走查（Find X8 / ColorOS 17） | S11 |
| A11Y | 无障碍测试 | S12 |
| SEC | 安全 / 代码审计 | S12 |
| MANUAL | 人工检查 | 对应工包自检 + S12 复核 |

## 追踪表

| 需求 | 优先级 | 测试用例 | 验收方式 |
|---|---|---|---|
| R-01 | P0 | MT-05 | MANUAL |
| R-02 | P0 | ST-01 | SEC |
| R-03 | P0 | MT-06 | MANUAL |
| R-04 | P1 | README 安装节审计 | MANUAL |
| R-10 | P0 | IT-02 · AT-01 | INST + A11Y |
| R-11 | P0 | MT-01 | MANUAL |
| R-12 | P0 | UT-05 · MT-02 | UNIT + MANUAL |
| R-13 | P0 | IT-02 | INST |
| R-20 | P0 | MT-01 | MANUAL |
| R-21 | P0 | MT-01 | MANUAL |
| R-22 | P0 | UT-05 · AT-02 | UNIT + A11Y |
| R-23 | P0 | MT-07 | MANUAL |
| R-24 | P0 | AT-03 · ST-03 | A11Y + SEC |
| R-30 | P0 | DT-01 | DEVICE |
| R-31 | P1 | DT-01 | DEVICE |
| R-32 | P0 | UT-06 · DT-02 | UNIT + DEVICE |
| R-33 | P0 | UT-07 | UNIT |
| R-34 | P0 | UT-08 | UNIT |
| R-40 | P0 | UT-04 · DT-06 | UNIT + DEVICE |
| R-41 | P0 | IT-03 | INST |
| R-42 | P0 | IT-04 | INST |
| R-43 | P0 | IT-05 · UT-09 | INST + UNIT |
| R-44 | P0 | UT-09 · MT-08 | UNIT + MANUAL |
| R-45 | P0 | UT-04 | UNIT |
| R-46 | P0 | DT-06 · ST-02 | DEVICE + SEC |
| R-50 | P0 | DT-05 | DEVICE |
| R-51 | P0 | MT-03 | MANUAL |
| R-52 | P0 | UT-02 | UNIT |
| R-53 | P0 | UT-03 | UNIT |
| R-54 | P0 | DT-03 · UT-02 | DEVICE + UNIT |
| R-55 | P0 | DT-04 | DEVICE |
| R-56 | P0 | DT-09 | DEVICE |
| R-60 | P0 | DT-07 | DEVICE |
| R-61 | P0 | DT-07 · AT-02 | DEVICE + A11Y |
| R-62 | P0 | IT-06 | INST |
| R-63 | P0 | IT-07 | INST |
| R-70 | P0 | UT-01 | UNIT |
| R-71 | P0 | UT-01 | UNIT |
| R-80 | P0 | IT-01 | INST |
| R-81 | P0 | ST-04 | SEC |
| R-82 | P1 | IT-01（关联）· 人工清空走查 | INST + MANUAL |
| R-83 | P1 | ST-01（关联） | SEC |
| R-90 | P1 | IT-11 | INST |
| R-91 | P1 | IT-07 | INST |
| R-100 | P0 | IT-08 | INST |
| R-101 | P1 | IT-09 | INST |
| R-102 | P1 | IT-09 | INST |
| R-103 | P1 | IT-10 | INST |
| R-110 | P1 | ST-06 | SEC |
| R-111 | P0 | ST-05 | SEC |
| R-120 | P0 | DT-08 · MT-04 | DEVICE + MANUAL |

## P0 覆盖率核对

- P0 需求总数：**41 条**。
- 已映射测试的 P0：**41 条**，覆盖率 **100%**。
- 特殊独立需求 ID 已确认：闸门状态机（R-40）、凌晨 4:00 换日（R-70）、权限降级（R-22）、到时三出口（R-60/R-61/R-62/R-63）。
