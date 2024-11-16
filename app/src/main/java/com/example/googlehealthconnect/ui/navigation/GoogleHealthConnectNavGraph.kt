package com.example.googlehealthconnect.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.googlehealthconnect.data.GoogleHealthConnectRepository
import com.example.googlehealthconnect.ui.home.HomeDestination
import com.example.googlehealthconnect.ui.home.HomeScreen
import com.example.googlehealthconnect.ui.permissions.PERMISSIONS
import com.example.googlehealthconnect.ui.permissions.PermissionsDestination
import com.example.googlehealthconnect.ui.permissions.PermissionsScreen
import com.example.googlehealthconnect.ui.permissions.getPermissionStates
import com.example.googlehealthconnect.ui.records.RecordsAddDestination
import com.example.googlehealthconnect.ui.records.RecordsAddScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

/**
 * Provides Navigation graph for the application.
 */
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun GoogleHealthConnectNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    repository: GoogleHealthConnectRepository
) {
    var permissionStates = mutableListOf<PermissionState>() // состояние разрешения
    val countPermissions = mutableIntStateOf(0) // количество разрешений
    var realPermissions by countPermissions // итоговое реальное количество разрешений
    realPermissions += getPermissionStates(permissionStates)

    NavHost(
        navController = navController,
        startDestination =
            if(realPermissions == PERMISSIONS.size)
                HomeDestination.route
            else
                PermissionsDestination.route,
        modifier = modifier
    ) {
        composable(route = PermissionsDestination.route) {
            PermissionsScreen(
                modifier = modifier,
                states = permissionStates,
                permisesState = countPermissions,
            )
        }
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToDataAdd = {
                    navController.navigate(RecordsAddDestination.route)
                },
                repository = repository
            )
        }
        composable(route = RecordsAddDestination.route) {
            RecordsAddScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                repository = repository
            )
        }
    }
}