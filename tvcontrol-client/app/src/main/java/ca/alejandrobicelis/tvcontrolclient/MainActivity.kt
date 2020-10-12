package ca.alejandrobicelis.tvcontrolclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import ca.alejandrobicelis.tvcontrolclient.communication.Client
import java.net.URI

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = this::class.simpleName
        const val WEB_SOCKET_URL = "ws://192.168.0.101:55555"
        val webSocketURI = URI(WEB_SOCKET_URL)
    }

    private lateinit var powerButton: Button
    private lateinit var volUpButton: Button
    private lateinit var volDownButton: Button
    private lateinit var macro1Button: Button
    private lateinit var macro2Button: Button
    private lateinit var macro3Button: Button
    private lateinit var macro4Button: Button

    private lateinit var client: Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        powerButton = findViewById(R.id.activity_main_power)
        powerButton.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"POWER_TOGGLE","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        volUpButton = findViewById(R.id.activity_main_volume_up)
        volUpButton.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"VOLUME_UP","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        volDownButton = findViewById(R.id.activity_main_volume_down)
        volDownButton.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"VOLUME_DOWN","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        macro1Button = findViewById(R.id.activity_main_macro_1)
        macro1Button.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"MACRO_1","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        macro2Button = findViewById(R.id.activity_main_macro_2)
        macro2Button.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"MACRO_2","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        macro3Button = findViewById(R.id.activity_main_macro_3)
        macro3Button.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"MACRO_3","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }

        macro4Button = findViewById(R.id.activity_main_macro_4)
        macro4Button.setOnClickListener {
            if(client.isOpen) {
                client.send("""{"action":"MACRO_4","value":""}""")
            } else {
                Log.d(TAG, "Client is not open!")
            }
        }



    }

    override fun onResume() {
        super.onResume()
        client = Client(webSocketURI)
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
}