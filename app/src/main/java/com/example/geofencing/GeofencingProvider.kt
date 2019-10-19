package com.example.geofencing

import com.google.android.libraries.places.api.model.Place

/**
 * Provides access to geofencing services
 * We needed to use an interface, so we did (TM)
 */
interface GeofencingProvider {
    fun addFence(place: Place)
    fun removeFence(place:Place)
}