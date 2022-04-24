package hu.adikaindustries.kotlinflowexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
class MainViewModel: ViewModel() {
    val countDownFlow = flow<Int> {
        val startingValue = 10
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0){
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()


    private val _sharedFlow= MutableSharedFlow<Int>(replay = 5)
    val sharedFlow = _sharedFlow.asSharedFlow()



    fun squareNumber(number:Int){
        viewModelScope.launch {
            _sharedFlow.emit(number*number)
        }
    }
    init {
        collectFlow()
        //reduceFlow()
        squareNumber(3)
        viewModelScope.launch {
            sharedFlow.collect{
                delay(2000L)
                println("FLOW: the reviced number is $it")
            }
        }

        viewModelScope.launch {
            sharedFlow.collect{
                delay(3000L)
                println(" SECOND FLOW: the reviced number is $it")
            }
        }

    }

    fun incrementCounter(){
        _stateFlow.value  += 1
    }

    private fun reduceFlow(){
        viewModelScope.launch { 
            val reduceResult = countDownFlow
                .fold(100){
                    accumulator, value ->
                    accumulator+ value
                }
            println("The reduce is $reduceResult")
        }
    }


    @FlowPreview
    private fun collectFlow() {

      /*  val flow1 = flow {
            emit(1)
            delay(500L)
            emit(2)
        }*/
       // val flow1 = (1..5).asFlow()

        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")

        }
        //conflate : if there are two ore more emits in the scope and they are not finised after it finished it jumps to the latest emit
        //buffer : buffers the emits
        viewModelScope.launch {
            flow.onEach {
                println("FLOW: $it is delivered")

            }

                .collectLatest{
                    println("Flow: now eating $it")
                    delay(1500L)
                    println("Flow: finished eating $it")
                }
        }

        /*viewModelScope.launch {
            flow1.flatMapConcat { value ->
                flow {
                    emit(value + 1)
                    delay(500L)
                    emit(value + 2)
                }

            }.collect { value ->
                println("the value is $value")

            }
        }*/



       /* viewModelScope.launch {
            //simple collect emits all events
            //collect latest collect the last not canceled state
            //simple operators used here
         val count =  countDownFlow
                .filter { time->
                    time % 2 == 0
                }
                .map {
                    time->
                            time * time
                }
                .onEach { time->
                    println(time)
                }
                /*.collect{
                time ->
                        //delay(1500L)
                        println("Return time is $time")
            }*/
                .count {
                    it % 2== 0
                }
            println("coint is $count")
        }*/
    }
}