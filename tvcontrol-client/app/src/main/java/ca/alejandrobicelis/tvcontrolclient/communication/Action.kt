package ca.alejandrobicelis.tvcontrolclient.communication

/**
 * Created by abicelis on 2020-10-20
 */
import ca.alejandrobicelis.tvcontrolclient.data.ServerResponse

enum class Action {
    VOLUME_GET {
        override fun go(response: ServerResponse, viewDelegate: ServerMessageProcessor.ViewDelegate) {
            viewDelegate.updateVolume(response.value)
        }
    },
    POWER_GET {
        override fun go(response: ServerResponse, viewDelegate: ServerMessageProcessor.ViewDelegate) {
            viewDelegate.updatePowerStatus(response.value.contains("1"))
        }
    };

    abstract fun go(response: ServerResponse, viewDelegate: ServerMessageProcessor.ViewDelegate)
}