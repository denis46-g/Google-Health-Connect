package com.example.googlehealthconnect.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.StepsRecord
import com.example.googlehealthconnect.R
import com.example.googlehealthconnect.GoogleHealthConnectTopAppBar
import com.example.googlehealthconnect.data.GoogleHealthConnectRepository
import com.example.googlehealthconnect.ui.navigation.NavigationDestination
import com.example.googlehealthconnect.ui.records.DateInputRow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToDataAdd: () -> Unit,
    modifier: Modifier = Modifier,
    repository: GoogleHealthConnectRepository
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            GoogleHealthConnectTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = {
                        navigateToDataAdd()
                    },
                    modifier = Modifier
                ) {
                    Text(stringResource(R.string.insert_record))
                }

                var key = remember { mutableIntStateOf(0) }
                var from by remember { mutableStateOf("") }
                //var now = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
                var to by remember { mutableStateOf("") }

                var recordsList by remember {mutableStateOf(listOf<StepsRecord>())}

                
                LaunchedEffect(from, to, key.value) {
                    recordsList = repository.readStepsByTimeRange(from, to)
                }

                if(recordsList.isEmpty()){
                    Text(
                        text = "Oops! No any records. Push a button upper to add",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
                else{
                    DateInputRow(context = LocalContext.current, s = "From", from,
                        onValueChange = { newValue -> from = newValue })
                    DateInputRow(context = LocalContext.current, s = "To", to,
                        onValueChange = {newValue -> to = newValue })

                    Spacer(modifier = Modifier.height(16.dp))

                    RecordsList(
                        recordsList = recordsList,
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
                        repository = repository,
                        key = key
                    )
                }
            }
        }
    )
}

@Composable
private fun RecordsList(
    recordsList: List<StepsRecord>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier,
    repository: GoogleHealthConnectRepository,
    key: MutableState<Int>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = recordsList) { stepRecord ->
            Record(stepRecord = stepRecord,
                modifier = Modifier.padding(vertical = 4.dp,
                    horizontal = dimensionResource(id = R.dimen.padding_small)),
                repository = repository,
                key = key)
        }
    }
}

@Composable
private fun Record(
    stepRecord: StepsRecord,
    modifier: Modifier = Modifier,
    repository: GoogleHealthConnectRepository,
    key: MutableState<Int>
) {
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
        //verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        val coroutineScope = rememberCoroutineScope()
        Row(
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = "День " + SimpleDateFormat("dd/MM/yyyy").
                                    format(Date(stepRecord.startTime.epochSecond * 1000))
                        + " => " + stepRecord.count + " шагов",
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(onClick = { deleteConfirmationRequired = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    modifier = Modifier.padding(bottom = 20.dp),
                    tint = Color.Red
                )
            }
            if (deleteConfirmationRequired) {
                DeleteConfirmationDialog(
                    onDeleteConfirm = {
                        deleteConfirmationRequired = false
                        coroutineScope.launch {
                            repository.deleteStep(stepRecord)
                            key.value++
                        }
                    },
                    onDeleteCancel = { deleteConfirmationRequired = false },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes))
            }
        })
}