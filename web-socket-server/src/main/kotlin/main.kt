import java.net.InetSocketAddress

fun main(args: Array<String>) {
    println("Hello World!")

    val host = "localhost"
    val port = 55555

    val shellHelper = ShellHelper()
    val server = Server(InetSocketAddress(host, port), shellHelper)
    server.run()
}