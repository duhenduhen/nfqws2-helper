package org.nfqws2.helper

import android.graphics.drawable.Icon
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import java.util.concurrent.Executors

class Nfqws2TileService : TileService() {
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())
    @Volatile private var commandInFlight = false

    override fun onStartListening() {
        super.onStartListening()
        refreshTile()
    }

    override fun onClick() {
        super.onClick()
        if (commandInFlight) return
        // onStartListening() has already synchronized the tile with json-status.
        // do not spawn another root status process here
        val shouldStop = qsTile.state == Tile.STATE_ACTIVE
        commandInFlight = true
        qsTile.state = Tile.STATE_UNAVAILABLE
        qsTile.label = getString(if (shouldStop) R.string.tile_stopping else R.string.tile_starting)
        qsTile.updateTile()
        executor.execute {
            if (shouldStop) ModuleController.stop() else ModuleController.start()
            val updated = Nfqws2Status.read(this)
            NotificationController.show(this, updated)
            mainHandler.post {
                commandInFlight = false
                applyState(updated)
            }
        }
    }

    private fun refreshTile() {
        executor.execute {
            val status = Nfqws2Status.read(this)
            NotificationController.show(this, status)
            mainHandler.post { applyState(status) }
        }
    }

    private fun applyState(status: Nfqws2Status) {
        qsTile.icon = Icon.createWithResource(this, R.drawable.ic_nfqws2)
        qsTile.label = when {
            status.error != null -> getString(R.string.tile_error)
            status.isPaused -> getString(R.string.tile_paused)
            status.running -> getString(R.string.tile_active)
            else -> getString(R.string.tile_inactive)
        }
        qsTile.state = when {
            status.error != null -> Tile.STATE_UNAVAILABLE
            status.running -> Tile.STATE_ACTIVE
            else -> Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
