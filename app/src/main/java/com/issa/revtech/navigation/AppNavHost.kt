package com.issa.revtech.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.issa.revtech.ui.Home.HomeScreen
import com.issa.revtech.ui.history.HistoryScreen
import com.issa.revtech.ui.fuel.FuelScreen
import com.issa.revtech.ui.maintainance.MaintenanceScreen
import com.issa.revtech.ui.auth.LoginScreen
import com.issa.revtech.ui.auth.RegisterScreen
import com.issa.revtech.ui.screens.onboarding.OnBoardingScreen
import com.issa.revtech.ui.screens.onboarding.OnBoardingScreen2
import com.issa.revtech.ui.screens.onboarding.OnBoardingScreen3
import com.issa.revtech.ui.screens.splash.SplashScreen
import com.issa.revtech.ui.profile.ProfileScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUT_SPLASH) {
            SplashScreen(navController)
        }
        composable(ROUT_ONBOARDING) {
            OnBoardingScreen(navController)
        }
        composable(ROUT_ONBOARDING2) {
            OnBoardingScreen2(navController)
        }
        composable(ROUT_ONBOARDING3) {
            OnBoardingScreen3(navController)
        }
        composable(ROUT_LOGIN) {
            LoginScreen(navController)
        }
        composable(ROUT_REGISTER) {
            RegisterScreen(navController)
        }
        composable(ROUT_HOME) {
            HomeScreen(navController)
        }
        composable(ROUT_HISTORY) {
            HistoryScreen(navController)
        }
        composable(ROUT_FUEL) {
            FuelScreen(navController)
        }
        composable(ROUT_MAINTENANCE) {
            MaintenanceScreen(navController)
        }
        composable(ROUT_PROFILE) {
            ProfileScreen(navController)
        }
    }
}