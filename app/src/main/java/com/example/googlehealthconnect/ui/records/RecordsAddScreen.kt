package com.example.googlehealthconnect.ui.records

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.googlehealthconnect.GoogleHealthConnectTopAppBar
import com.example.googlehealthconnect.R
import com.example.googlehealthconnect.ui.navigation.NavigationDestination
import java.util.Calendar
import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.googlehealthconnect.data.GoogleHealthConnectRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object RecordsAddDestination : NavigationDestination {
    override val route = "record_add"
    override val titleRes = R.string.record_add
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsAddScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    repository: GoogleHealthConnectRepository
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            GoogleHealthConnectTopAppBar(
                title = stringResource(RecordsAddDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
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
                var stepValue by remember { mutableStateOf("") }
                var selectedDate by remember { mutableStateOf("") }
                Button(
                    onClick = {
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val realDate = LocalDate.parse(selectedDate, formatter)

                        // Получаем начало и конец дня (в пределах одной даты)
                        val startOfDay = realDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        val endOfDay = realDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
                        coroutineScope.launch {
                            repository.insertSteps(
                                stepValue.toLong(),
                                startOfDay, endOfDay)
                            navigateBack()
                        }
                    },
                    modifier = Modifier,
                    enabled = stepValue != "" && selectedDate != ""
                ) {
                    Text(stringResource(R.string.save_record))
                }

                DateInputRow(LocalContext.current, "Date", selectedDate,
                    onValueChange = { newValue -> selectedDate = newValue})

                InputRow("Steps", stepValue,
                    onValueChange = { newValue -> stepValue = newValue })
            }
        }
    )
}


@Composable
fun InputRow(s: String, stepValue: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 100.dp, top = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$s: ", style = MaterialTheme.typography.bodyLarge)

        // Поле ввода для целого числа
        TextField(
            value = stepValue,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } &&
                    (newValue.length <= 1 || newValue.length > 1 && newValue[0] != '0' )) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier
                .padding(start = 40.dp)
                .width(200.dp)
        )
    }
}


@Composable
fun DateInputRow(context: Context, s: String, selectedDate: String,
                 onValueChange: (String) -> Unit) {

    val todayCalendar = Calendar.getInstance()
    val todayYear = todayCalendar.get(Calendar.YEAR)
    val todayMonth = todayCalendar.get(Calendar.MONTH)
    val todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 64.dp, top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if(s == "To")
            Text(text = "$s:    ", style = MaterialTheme.typography.bodyLarge)
        else
            Text(text = "$s:", style = MaterialTheme.typography.bodyLarge)

        // Поле для ввода даты
        TextField(
            value = selectedDate,
            onValueChange = {},
            modifier = Modifier
                .padding(start = 40.dp)
                .width(200.dp),
            placeholder = { Text("dd/mm/yyyy") },
            readOnly = true
        )

        // Иконка календаря
        IconButton(onClick = {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->

                    val selectedCalendar = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                        // Сбрасываем часы, минуты и секунды
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    // Сбрасываем часы, минуты и секунды для сегодняшней даты
                    todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    todayCalendar.set(Calendar.MINUTE, 0)
                    todayCalendar.set(Calendar.SECOND, 0)
                    todayCalendar.set(Calendar.MILLISECOND, 0)

                    if (selectedCalendar.time <= todayCalendar.time) {
                        val date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                        onValueChange(date)
                    }
                    else {
                        Toast.makeText(context, "Don't choose future date", Toast.LENGTH_SHORT).show()
                    }
                },
                todayYear, todayMonth, todayDay).apply {
                // Устанавливаем максимальную дату на сегодня
                datePicker.maxDate = todayCalendar.timeInMillis }.show()
        }) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Выбрать дату"
            )
        }
    }
}