package com.example.geofencing

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.model.Place

class LocalGeofencing:GeofencingProvider {

    var googleAPIClient:GoogleApiClient
    var geofencingClient: GeofencingClient
    var geofenceList:ArrayList<Geofence>
    lateinit var context:Context
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    constructor(context: Context) {
        this.context = context
        geofencingClient = LocationServices.getGeofencingClient(context)
        geofenceList = ArrayList()
        googleAPIClient = GoogleApiClient.Builder(context).addApi(LocationServices.API).build()
        googleAPIClient.connect()
    }

    override fun addFence(place: Place) {
        val fence = Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(place.id)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        place.latLng!!.latitude,
                        place.latLng!!.longitude,
                        100f
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Long.MAX_VALUE)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build()

        val request = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(fence)
        }.build()

        geofencingClient.addGeofences(request, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("LGf","Added fencing")
            }

            addOnFailureListener{
                Log.d("LGf","Failed to add fence")
                it.printStackTrace()
            }
        }
    }

    override fun removeFence(place:Place) {
        val items:List<String> = listOf(place.id!!)
        geofencingClient.removeGeofences(items)
    }
}