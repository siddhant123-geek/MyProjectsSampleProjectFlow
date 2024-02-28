import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.MutableState
import androidx.test.core.app.ActivityScenario.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
fun main() {
    val myFun = { name: String -> "Hello $name" }
    val message = myFun.invoke("Sid")
    println(message)

    val higherOrderFun = { name: String -> println(name)}

    higherOrderFun("Sid")

    val intChannel = Channel<Int> (10)

    runBlocking {
        for(i in 1..5) {
            intChannel.send(i)
        }
        intChannel.close()
    }

    runBlocking {
        for(data in intChannel) {
            println("data received in the channel $data")
        }
    }

    println(intChannel.isClosedForSend)
    println(intChannel.isClosedForReceive)

    val person = Person("Sid")
    println(person.name)

    with(person) {
        this.name = "Sid1"
    }
    println(person.name)

    person.let {
        it.name = "Sid2"
    }

    println(person.name)

    person.also {
        it.name = "Sid3"
        println(it.name)
    }
}

private fun changeTheCounterValue(counter: MutableStateFlow<Int>) {
    counter.value = 5
    counter.value = 6
}

class Person(var name: String) {
    private var myName: String = name
}