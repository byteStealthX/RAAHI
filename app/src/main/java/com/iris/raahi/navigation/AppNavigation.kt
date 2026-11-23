package com.iris.raahi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iris.raahi.RAAHIApplication
import com.iris.raahi.screens.CameraFeatureScreen
import com.iris.raahi.screens.CameraFeature
import com.iris.raahi.screens.DashboardScreen
import com.iris.raahi.screens.EmergencyScreen
import com.iris.raahi.screens.NavigationScreen
import com.iris.raahi.screens.OnboardingScreen
import com.iris.raahi.screens.SettingsScreen
import com.iris.raahi.screens.SmartSpectraScreen
import com.iris.raahi.screens.VoiceAssistantScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val OBSTACLE = "obstacle"
    const val OCR = "ocr"
    const val NAVIGATION = "navigation"
    const val ASSISTANT = "assistant"
    const val EMERGENCY = "emergency"
    const val SETTINGS = "settings"
    const val SMARTSPECTRA = "smartspectra"
}

@Composable
fun AppNavigation(application: RAAHIApplication) {
    val navController = rememberNavController()
    val initial = if (application.settingsRepository.get().onboardingComplete) Routes.HOME else Routes.ONBOARDING

    NavHost(navController = navController, startDestination = initial) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(application) { navController.navigate(Routes.HOME) { popUpTo(0) } }
        }
        composable(Routes.HOME) {
            DashboardScreen(
                onNavigate = { route -> navController.navigate(route) },
                onEmergency = { navController.navigate(Routes.EMERGENCY) }
            )
        }
        composable(Routes.OBSTACLE) { CameraFeatureScreen(application, CameraFeature.OBSTACLE) { navController.popBackStack() } }
        composable(Routes.OCR) { CameraFeatureScreen(application, CameraFeature.OCR) { navController.popBackStack() } }
        composable(Routes.NAVIGATION) { NavigationScreen(application) { navController.popBackStack() } }
        composable(Routes.ASSISTANT) { VoiceAssistantScreen(application) { route -> navController.navigate(route) } }
        composable(Routes.EMERGENCY) { EmergencyScreen(application) { navController.popBackStack() } }
        composable(Routes.SETTINGS) { SettingsScreen(application) { navController.popBackStack() } }
        composable(Routes.SMARTSPECTRA) { SmartSpectraScreen(application) { navController.popBackStack() } }
    }
}
