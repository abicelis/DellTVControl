package ca.alejandrobicelis.tvcontrolclient.communication

import ca.alejandrobicelis.tvcontrolclient.data.ServerResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception

class ServerMessageProcessor(viewDelegate: ViewDelegate) {
    var viewDelegate = viewDelegate


    fun onMessage(message: String?) {
        if(message != null) {
            try {
                val response = Json.decodeFromString<ServerResponse>(message)

                Action.valueOf(response.name).go(response, viewDelegate)


            } catch (e: Exception) {
                println("Could not parse $message")
            }
        }
    }


    interface ViewDelegate {
        fun updateVolume(volume: String)
        fun updatePowerStatus(powerStatus: Boolean)
    }
}
