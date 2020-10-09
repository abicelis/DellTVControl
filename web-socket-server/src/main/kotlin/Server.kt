import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class Server(inetSocketAddress: InetSocketAddress) : WebSocketServer(inetSocketAddress) {

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        conn?.send("Connected to Server!")
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
        if(message != null) {
            try {
                TVAction.valueOf(message).run()

            } catch (e: IllegalArgumentException) {
                println("Received an invalid message from '" + (conn?.remoteSocketAddress ?: "NULL") + "'. Message '$message'")
                conn?.send("Invalid message. Try sending " + TVAction.values().joinToString { it.name })
            }
        } else {
            println("Received a NULL message from '" + (conn?.remoteSocketAddress ?: "NULL") + "'.")
            conn?.send("Received NULL message. Try sending " + TVAction.values().joinToString { it.name })
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