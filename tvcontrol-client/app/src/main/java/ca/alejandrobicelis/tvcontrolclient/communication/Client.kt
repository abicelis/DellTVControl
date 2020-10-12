package ca.alejandrobicelis.tvcontrolclient.communication

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

/**
 * Created by abicelis on 2020-10-11
 */
class Client(uri: URI) : WebSocketClient(uri) {
    companion object {
        val TAG = this::class.simpleName
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d(TAG, "onOpen")
    }

    override fun onMessage(message: String?) {
        Log.d(TAG, "onMessage=message")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d(TAG, "onClose")
    }

    override fun onError(ex: Exception?) {
        Log.d(TAG, "onError")
        ex?.printStackTrace()
    }
}