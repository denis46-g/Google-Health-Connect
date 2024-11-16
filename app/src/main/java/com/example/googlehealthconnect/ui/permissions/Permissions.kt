package com.example.googlehealthconnect.ui.permissions

import androidx.compose.runtime.Composable
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

// Create a set of permissions for required data types
val PERMISSIONS =
    setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        /*HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getWritePermission(WeightRecord::class)*/
    )

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun getPermissionStates(states: MutableList<PermissionState>): Int {
    var permises = 0
    PERMISSIONS.forEach {
        val state = rememberPermissionState(it)
        states.add(state)
        if (state.hasPermission) {
            permises++
        }
    }
    return permises
}

