package com.rahul.jettip

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rahul.jettip.components.InputField
import com.rahul.jettip.ui.theme.JetTipTheme
import com.rahul.jettip.utils.Utils
import com.rahul.jettip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainContent()
                }

            }
        }
    }
}

@Composable
fun MyApp(content: @Composable (paddingValues: PaddingValues) -> Unit) {
    JetTipTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
            content(paddingValues)
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(12.dp)
            .clip(shape = RoundedCornerShape(12.dp)), color = Color(0xFFE9D7F7)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = stringResource(R.string.total_expenses), modifier = Modifier.wrapContentWidth(), style = MaterialTheme.typography.headlineMedium)
            Text(text = "$$total", modifier = Modifier.wrapContentWidth(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        }

    }
}


@Preview
@Composable
fun MainContent() {
    val splitByState = remember {
        mutableIntStateOf(1)
    }
    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }

    val totalPerPerson = remember {
        mutableDoubleStateOf(0.0)
    }
    BillForm(splitByState = splitByState, tipAmountState = tipAmountState, totalPerPerson = totalPerPerson) { totalBill ->
        println(totalBill)
    }
}


@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPerson: MutableState<Double>,
    onValueChange: (String) -> Unit = {}
) {
    val context: Context = LocalContext.current
    val totalBillState = remember {
        mutableStateOf("")
    }

    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }


    val tipPercentage = (sliderPositionState.floatValue * 100).div(100).toInt()

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    TopHeader(totalPerPerson.value)
    Surface(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {

        //Input Field
        Column(modifier = modifier.padding(6.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
            InputField(
                valueState = totalBillState,
                labelId = stringResource(R.string.enter_amount),
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions

                    keyboardController?.hide()
                    onValueChange.invoke(totalBillState.value.trim())

                },
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done
            )
            //----------------------------
            //Split Row
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(3.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Split", style = MaterialTheme.typography.bodyLarge, modifier = modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )

                Row(modifier = modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {

                    RoundIconButton({
                        splitByState.value = if (splitByState.value > 1) splitByState.value - 1 else 1
                        Toast.makeText(context, "Minus Clicked ${splitByState.value}", Toast.LENGTH_SHORT).show()
                    }, icon = Icons.Default.Remove)

                    Text("${splitByState.value}", style = MaterialTheme.typography.bodyLarge, modifier = modifier.padding(horizontal = 8.dp))

                    RoundIconButton({
                        if (splitByState.value < range.last) splitByState.value += 1
                        Toast.makeText(context, "Add Clicked ${splitByState.value}", Toast.LENGTH_SHORT).show()
                    }, icon = Icons.Default.Add)
                }
            }
            //-------------------------------
            //Tip Row
            Row(
                modifier = modifier
                    .padding(3.dp)
                    .fillMaxWidth()
                    .align(alignment = Alignment.CenterHorizontally)
            ) {

                Text(
                    text = "Tip", style = MaterialTheme.typography.bodyLarge, modifier = modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )

                Box(modifier = modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    Text(text = "$${tipAmountState.value}", style = MaterialTheme.typography.bodyLarge, modifier = modifier.padding(end = 8.dp))
                }

            }
            //-------------------------------
            // Slider Row

            Column(
                modifier = modifier
                    .padding(3.dp)
                    .fillMaxWidth()
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "$tipPercentage%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, modifier = modifier
                        .padding(start = 8.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                )
                Spacer(modifier = modifier.height(14.dp))
                //Slider
                Slider(
                    value = sliderPositionState.floatValue,
                    onValueChange = {
                        sliderPositionState.floatValue = it
                        tipAmountState.value = Utils.calculateTotalTip(totalBill = totalBillState.value.toDoubleOrNull(), tipPercentage = tipPercentage)
                        totalPerPerson.value = Utils.calculateTotalPerPerson(totalBill = totalBillState.value.toDoubleOrNull(), splitBy = splitByState.value, tipPercentage = tipPercentage)
                    },
                    valueRange = 0f..100f,
                    steps = 4,
                    modifier = modifier.padding(horizontal = 16.dp)
                )


            }


        }

    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetTipTheme {
        MyApp { paddingValues ->
            Column {
                MainContent()
            }
        }
    }
}