import data.ServerResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress

var running = true
val allowedHosts = "0.0.0.0"
val port = 55555
val POLLING_DELAY = 1000L

fun main() {
//    Shell.debug = true
    var server: Server? = null

    // Start the server
    try {
        server = Server(InetSocketAddress(allowedHosts, port))
        server.start()
    } catch (e: Exception) {
        stopServer(server)
        throw Exception("Could not start the server. Unrecoverable error. Exiting.", e)
    }


    // Start polling power status to detect and broadcast changes in power status
    GlobalScope.launch(context = Dispatchers.IO){
        var power : ServerResponse
        try {
            // We can afford a non-null asserted call (!!) here, because
            // Action.POWER_GET.go() either returns non-null or throws an Exception
            power = Action.POWER_GET.go(null)!!

        } catch (e: Exception) {
            stopServer(server)
            throw Exception("Could not get initial power status. Unrecoverable error. Exiting.", e)
        }

        println("Initially, POWER_STATUS is ${power.value}")

        while(running) {
            val powerNow = Action.POWER_GET.go(null)!!

            if(powerNow.value != power.value) {
                power = powerNow

                println("POWER_STATUS changed to ${power.value}")
                server.broadcast(Json.encodeToString(power))
            }
            delay(POLLING_DELAY)
        }
    }

    // Read stdIn for commands
    val reader = BufferedReader(InputStreamReader(System.`in`))
    while (running) {
        val line = reader.readLine()
        if (line.isNotEmpty()) {
            if (line == "shutdown") {
                println("Shutting the server down")
                stopServer(server)
            } else {
                println("Type 'shutdown' to shut the server down")
            }
        }
    }
}

fun stopServer(server: Server?) {
    server?.stop(1000)
    running = false
}