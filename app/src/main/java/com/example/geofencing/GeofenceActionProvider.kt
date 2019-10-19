package com.example.geofencing

import android.media.AudioManager

class GeofenceActionProvider {
    companion object Statics {
        var audioService:AudioManager? = null
        private var actionsEnabled:Boolean = false

        fun setEnabled(enabled:Boolean) {
            actionsEnabled = enabled
            if (!actionsEnabled) {
                exitedSilentZone()
            }
        }

        fun enteredSilentZone() {
            if (!actionsEnabled) {
                return
            }
            audioService?.ringerMode = AudioManager.RINGER_MODE_SILENT
        }

        fun exitedSilentZone() {
            audioService?.ringerMode = AudioManager.RINGER_MODE_NORMAL
        }
    }
}