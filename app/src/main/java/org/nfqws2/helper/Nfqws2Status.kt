package org.nfqws2.helper

import android.content.Context
import org.json.JSONObject

data class Nfqws2Status(
    val running: Boolean,
    val pid: String,
    val uptime: Long,
    val strategy: String,
    val mode: String,
    val paused: String,
    val qdrop: Long,
    val version: String,
    val error: String? = null
) {
    val isPaused: Boolean get() = paused.isNotBlank()

    companion object {
        fun read(context: Context): Nfqws2Status {
            val result = RootCommand.run(
                "sh /data/adb/modules/nfqws2-android/bin/nfqws2-ctl json-status"
            )
            if (!result.ok) return unavailable(context, result.stderr.ifBlank { result.stdout })
            return try {
                val json = JSONObject(result.stdout.trim())
                Nfqws2Status(
                    running = json.optBoolean("running"),
                    pid = json.optString("pid"),
                    uptime = json.optLong("uptime"),
                    strategy = json.optString("strategy", "default"),
                    mode = json.optString("mode"),
                    paused = json.optString("paused"),
                    qdrop = json.optLong("qdrop"),
                    version = json.optString("version")
                )
            } catch (e: Exception) {
                unavailable(context, context.getString(R.string.status_json_error, e.message ?: "unknown"))
            }
        }

        private fun unavailable(context: Context, message: String) = Nfqws2Status(
            running = false, pid = "", uptime = 0, strategy = "—", mode = "—",
            paused = "", qdrop = 0, version = "", error = message.ifBlank { context.getString(R.string.status_root_error) }
        )
    }
}
