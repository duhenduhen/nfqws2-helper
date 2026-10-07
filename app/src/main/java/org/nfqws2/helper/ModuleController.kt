package org.nfqws2.helper

object ModuleController {
    private const val service = "sh /data/adb/modules/nfqws2-android/service.sh"

    fun start() = RootCommand.run("$service start")
    fun stop() = RootCommand.run("$service stop")
    fun restart() = RootCommand.run("$service restart")
    fun doctor() = RootCommand.run("sh /data/adb/modules/nfqws2-android/bin/nfqws2-ctl doctor")
}
