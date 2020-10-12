import data.ServerRequest
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class Server(inetSocketAddress: InetSocketAddress) : WebSocketServer(inetSocketAddress) {

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
//        conn?.send("Connected to Server!")
//        broadcast("New connection to '" + handshake?.resourceDescriptor + "'")
        println("New connection to '${ if(conn != null) conn.remoteSocketAddress else "NULL"}'")

    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        println("Closed connection to '" + (conn?.remoteSocketAddress ?: "NULL") + "', with exit code '" + code + "'." + if(reason!=null) " Additional info: $reason" else "")
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        println("New message from '" + (conn?.remoteSocketAddress ?: "NULL") + "'. Message: '" + message + "'")
        try {
            handleMessage(conn, message)
        } catch (e: Exception) {
            println("Error handling incoming message '$message' from '" + (conn?.remoteSocketAddress ?: "NULL") + "'")
            e.printStackTrace()

            try {
                conn?.send("Error handling incoming message: '$message'")
            } catch (ex : Exception) {
                println("Error sending error message to client!")
                e.printStackTrace()
                /* Nothing else we can do here */
            }
        }
    }

    private fun handleMessage(conn: WebSocket?, message: String?) {
        var error = true;

        if(message != null) {
            try {
                val request = Json.decodeFromString<ServerRequest>(message)
                val response = Action.valueOf(request.action).go(request)
                if(response != null)
                    broadcast(Json.encodeToString(response))

                error = false;
            } catch (e: Exception) { }
        }

        if(error){
            println("Received an invalid message from '" + (conn?.remoteSocketAddress ?: "NULL") + "'. Message '$message'")
            val exampleRequest = Json.encodeToString(ServerRequest("<action>","<value_if_applicable>"))
            conn?.send("Invalid message. Try sending '$exampleRequest' where <action> is one of: " + Action.values().joinToString { it.name })
        }
    }

    override fun onMessage(conn: WebSocket?, message: ByteBuffer?) {
        super.onMessage(conn, message)
        println("New ByteBuffer from '" + (conn?.remoteSocketAddress ?: "NULL") + "'")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        println("Error occurred on connection '" + (conn?.remoteSocketAddress ?: "NULL") + "'." + if(ex!=null) " Additional info: ${ex.message}" else "")
    }

    override fun onStart() {
        println("Server started successfully. Listening on port ${address.port}" )
    }
}