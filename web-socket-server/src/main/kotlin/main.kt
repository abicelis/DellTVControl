import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import kotlin.system.exitProcess

fun main() {
    val POWER_POLLING_DELAY = 1000L

    val allowedHosts = "0.0.0.0"
    var running = true;
//    val ports = arrayOf(55555, 55556, 55557);
    val port = 55555;
//    var server: Server? = null


//    for (port in ports) {
//        try {
            val server = Server(InetSocketAddress(allowedHosts, port))
            server.start()
//        } catch (e: Exception) { //TODO: catch the actual exception, port in use.
//            println("Port $port in use")
//            server?.stop()
//        }
//    }

//    val thread = Thread(Runnable {
//        Thread.sleep(500)
//        println("Checking tv stat")
//        server.broadcast("TV is off or whatever!")
//    });

    GlobalScope.launch {
        var power = TVAction.POWER_GET.run().toString().toBoolean()
        println("Power is now " + if (power) "on." else "off.")

        while(running) {
            var powerNow = TVAction.POWER_GET.run().toString().toBoolean()
            println("! Power is now " + if (powerNow) "on." else "off.")

            if(powerNow != power) {
                power = powerNow
                println("Power changed to " + if (powerNow) "on." else "off.")
                server.broadcast("Power is now " + if (power) "on." else "off.")
            }
            delay(POWER_POLLING_DELAY)
        }
    }


    val reader = BufferedReader(InputStreamReader(System.`in`))
    while (true) {
        val line = reader.readLine();
        if(line == "exit") {
            server.stop(1000)
        }
    }

//    println("Could not start server. Exiting...")
//    exitProcess(-1)
}