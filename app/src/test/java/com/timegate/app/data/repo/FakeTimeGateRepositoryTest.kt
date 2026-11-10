package com.timegate.app.data.repo

import com.timegate.app.data.local.AppPolicyEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 验证「仓库接口可被 Fake 替换」（R-80）。
 *
 * 注意：本测试引用 Room Entity（带 androidx.room 注解），需待 S04 批准 Room 依赖后
 * 才能编译运行（见 `docs/handoff/S06.md` 变更请求）。
 */
class FakeTimeGateRepositoryTest {

    @Test
    fun `受管应用增删改名与清空`() = runBlocking {
        val repo = FakeTimeGateRepository()

        repo.upsertApp(AppPolicyEntity("com.a", "A", "A", 60, managed = true))
        repo.upsertApp(AppPolicyEntity("com.b", "B", "B", 30, managed = false))

        // 受管列表只含 managed=true
        assertEquals(listOf("com.a"), repo.getManagedApps().map { it.packageName })
        assertEquals(listOf("com.a"), repo.observeManagedApps().first().map { it.packageName })

        // 改名
        repo.renameApp("com.a", "甲")
        assertEquals("甲", repo.getManagedApps().first().displayName)

        // 开关受管
        repo.setAppManaged("com.b", true)
        assertEquals(setOf("com.a", "com.b"), repo.getManagedApps().map { it.packageName }.toSet())

        // 移除
        repo.deleteApp("com.a")
        assertEquals(listOf("com.b"), repo.getManagedApps().map { it.packageName })

        // 清空
        repo.clearAll()
        assertTrue(repo.getManagedApps().isEmpty())
    }
}
