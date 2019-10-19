package com.example.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    // ...
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.e("GB_R", "Geofence error: " + geofencingEvent.errorCode)

            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            GeofenceActionProvider.enteredSilentZone()
        }else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            GeofenceActionProvider.exitedSilentZone()
        }

    }
}
