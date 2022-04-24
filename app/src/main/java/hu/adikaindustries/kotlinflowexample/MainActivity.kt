package hu.adikaindustries.kotlinflowexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle.State.*
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import hu.adikaindustries.kotlinflowexample.ui.theme.KotlinFlowExampleTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    //Stateflow : used to keep values for example on screen rotations
    //SharedFlow: One time events like login or hide keyboard ...
    private val viewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //XML project impelemtetion
        lifecycleScope.launch {
            repeatOnLifecycle(STARTED){
                viewModel.stateFlow.collectLatest{number ->
                    println(number)
                }
            }

        }
        //other solution for the same
        collectLatestLifecycleFlow(viewModel.stateFlow){number->
            println(number)

        }

        setContent {
            KotlinFlowExampleTheme {
                // A surface container using the 'background' color from the theme
                val viewModel = viewModel<MainViewModel>()
                val time = viewModel.countDownFlow.collectAsState(initial = 10)
                val count = viewModel.stateFlow.collectAsState(initial = 10)

                Box(){
                    Button(onClick = {viewModel.incrementCounter()}) {
                        Text(text = "Counter: ${count.value}")
                    }

                    /*Text(
                        text = time.value.toString(),
                        fontSize = 30.sp,
                    )*/
                }
            }
        }
    }
}


fun <T> ComponentActivity.collectLatestLifecycleFlow(flow: Flow<T>, collect:suspend (T) -> Unit){
    lifecycleScope.launch {
        repeatOnLifecycle(STARTED){
            flow.collectLatest(collect)
        }
    }
}





@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KotlinFlowExampleTheme {
        Greeting("Android")
    }
}