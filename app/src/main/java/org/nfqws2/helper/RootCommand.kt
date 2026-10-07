package org.nfqws2.helper

import com.topjohnwu.superuser.Shell

data class CommandResult(val exitCode: Int, val stdout: String, val stderr: String) {
    val ok: Boolean get() = exitCode == 0
}

/** Shared libsu root shell. Shell.getShell() caches the main shell process. */
object RootCommand {
    fun run(command: String): CommandResult = try {
        val result = Shell.cmd(command).exec()
        CommandResult(
            exitCode = result.code,
            stdout = result.out.joinToString("\n"),
            stderr = result.err.joinToString("\n")
        )
    } catch (e: Exception) {
        CommandResult(126, "", e.message ?: e.javaClass.simpleName)
    }
}
