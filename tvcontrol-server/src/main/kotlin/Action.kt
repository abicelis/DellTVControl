import data.Pin
import data.ServerRequest
import data.ServerResponse


enum class Action {
    VOLUME_UP {
        override fun go(request: ServerRequest?): ServerResponse? {
            Shell.runCommand("/usr/bin/amixer -M set PCM 5%+",
                "Could not increase the volume.")
            return VOLUME_GET.go(null)
        }
    },
    VOLUME_DOWN {
        override fun go(request: ServerRequest?): ServerResponse? {
            Shell.runCommand("/usr/bin/amixer -M set PCM 5%-",
                "Could not decrease the volume.")
            return VOLUME_GET.go(null)
        }
    },
    VOLUME_SET {
        override fun go(request: ServerRequest?): ServerResponse? {
            if(request != null) {
                Shell.runCommand(
                    "/usr/bin/amixer -M set PCM ${request.value}%",
                    "Could not set the volume."
                )
                return VOLUME_GET.go(null)
            }
            println("VSET called with invalid request")
            return ServerResponse(error="VSET called with invalid request");
        }
    },
    VOLUME_GET {
        override fun go(request: ServerRequest?): ServerResponse? {
            //amixer cget numid=6 | sed -n -E 's/[[:blank:]]{2}:[[:blank:]]values=(.*),.*$/\1/p' | awk '{printf ("%.0f\n",$1/51*100)}'
            ///usr/bin/amixer -M set PCM 0%+ | sed -n -E 's/^.*Left:\ Playback[[:blank:]][[:digit:]]*[[:blank:]]\[(.*)\][[:blank:]]\[\-.*$/\1/p'

            val result = Shell.runCommand(
                "/usr/bin/amixer -M set PCM 0%+",
                "Could not get the volume."
            ).stdOut

            val regex = Regex("""[0-9]{1,3}%""")
            val volume = regex.find(result)?.value?.replace("%","")

            return ServerResponse(this.name, volume.toString())
        }
    },
    POWER_GET {
        override fun go(request: ServerRequest?): ServerResponse? {
            //todo maybe do a try catch, if error send a serverresponse with error= something, so the error gets to the client.
            val result = Shell.runCommand("gpio -g read ${Pin.POWER_SENSE.BCMPin}", "Could not get power status.")
            return ServerResponse(this.name, result.stdOut)
        }
    },
    POWER_TOGGLE {
        override fun go(request: ServerRequest?): ServerResponse? {
            //todo maybe do a try catch, if error send a serverresponse with error= something, so the error gets to the client.
            Shell.togglePin(Pin.POWER, "Could not turn power on.")
            return null
        }
    },
    MACRO_1 {
        override fun go(request: ServerRequest?): ServerResponse? {
            //todo maybe do a try catch, if error send a serverresponse with error= something, so the error gets to the client.
            Shell.togglePin(Pin.MACRO_1, "Could not toggle $name")
            return null
        }
    },
    MACRO_2 {
        override fun go(request: ServerRequest?): ServerResponse? {
            //todo maybe do a try catch, if error send a serverresponse with error= something, so the error gets to the client.
            Shell.togglePin(Pin.MACRO_2, "Could not toggle $name")
            return null
        }
    },
    MACRO_3 {
        override fun go(request: ServerRequest?): ServerResponse? {
            //todo maybe do a try catch, if error send a serverresponse with error= something, so the error gets to the client.
            Shell.togglePin(Pin.MACRO_3, "Could not toggle $name")
            return null
        }
    },
    MACRO_4 {
        override fun go(request: ServerRequest?): ServerResponse? {
            //todo maybe do a try catch, if error send a serverresponse with error= something, so the error gets to the client.
            Shell.togglePin(Pin.MACRO_4, "Could not toggle $name")
            return null
        }
    };

    abstract fun go(request: ServerRequest?) : ServerResponse?
}