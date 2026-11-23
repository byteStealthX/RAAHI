package com.iris.raahi.navigation

import java.net.URLEncoder

object NavigationLogic {
    fun routeUri(destination: String, originLatitude: Double? = null, originLongitude: Double? = null): String? {
        val cleanDestination = destination.trim()
        if (cleanDestination.isBlank()) return null
        val origin = if (originLatitude != null && originLongitude != null)
            "&origin=$originLatitude,$originLongitude" else ""
        val encoded = URLEncoder.encode(cleanDestination, Charsets.UTF_8.name()).replace("+", "%20")
        return "https://www.google.com/maps/dir/?api=1$origin&destination=$encoded&travelmode=walking"
    }
}
