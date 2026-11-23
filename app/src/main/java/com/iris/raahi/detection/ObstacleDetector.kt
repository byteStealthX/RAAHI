package com.iris.raahi.detection

interface ObstacleDetector {
    val status: String
    fun close()
}

class PlaceholderObstacleDetector : ObstacleDetector {
    override val status: String =
        "No trained obstacle model is configured. Camera preview is active, but detections are not available."
    override fun close() = Unit
}
