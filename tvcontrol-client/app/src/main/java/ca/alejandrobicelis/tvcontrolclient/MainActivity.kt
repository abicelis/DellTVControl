package ca.alejandrobicelis.tvcontrolclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import ca.alejandrobicelis.tvcontrolclient.communication.Client
import ca.alejandrobicelis.tvcontrolclient.communication.ServerMessageProcessor
import ca.alejandrobicelis.tvcontrolclient.databinding.ActivityMainBinding
import java.net.URI

class MainActivity : AppCompatActivity(), ServerMessageProcessor.ViewDelegate {
    companion object {
        val TAG = this::class.simpleName
        const val WEB_SOCKET_URL = "ws://192.168.0.101:55555"
        val webSocketURI = URI(WEB_SOCKET_URL)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var client: Client
    private lateinit var serverMessageProcessor: ServerMessageProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Message processor
        serverMessageProcessor = ServerMessageProcessor(this)

        binding.activityMainPower.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"POWER_TOGGLE","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        binding.activityMainVolumeUp.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"VOLUME_UP","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        binding.activityMainVolumeDown.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"VOLUME_DOWN","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        binding.activityMainMacro1.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"MACRO_1","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        binding.activityMainMacro2.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"MACRO_2","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        binding.activityMainMacro3.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"MACRO_3","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        binding.activityMainMacro4.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"MACRO_4","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        client = Client(webSocketURI, serverMessageProcessor)
        client.connect();
    }

    override fun onPause() {
        super.onPause()
        client.close()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if(client.isOpen) {
                    client.send("""{"action":"VOLUME_UP","value":""}""")
                    return true
                } else {
                    Log.d(TAG, "Client is not open!")
                }
            }

            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if(client.isOpen) {
                    client.send("""{"action":"VOLUME_DOWN","value":""}""")
                    return true
                } else {
                    Log.d(TAG, "Client is not open!")
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    //MessageProcessor.
    override fun updateVolume(volume: String) {
        runOnUiThread {
            binding.activityMainVolumeStatus.text = "Volume is $volume"
        }
    }

    override fun updatePowerStatus(powerStatus: Boolean) {
        runOnUiThread {
            binding.activityMainPowerStatus.text = "Power is " + if (powerStatus) "on" else "off"
        }
    }
}