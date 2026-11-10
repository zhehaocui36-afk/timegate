package com.timegate.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

/**
 * Launcher Activity 占位（S04）。
 *
 * 仅渲染一段占位文本，用于验证「干净克隆能构建并跑起来一个空 Compose App」。
 * 不包含任何业务 / 页面 / 导航逻辑；导航壳与首页由 S10 接入。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    Text(text = "闸 · TimeGate")
                }
            }
        }
    }
}
