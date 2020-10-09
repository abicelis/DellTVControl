enum class TVAction {
    VOLUME_UP {
        override fun run(): Any? {
            Shell.runCommand("/usr/bin/amixer -M set PCM 5%+",
                "Could not increase the volume.")
            return null;
        }
    },
    VOLUME_GET {
        override fun run(): Any? {
            //amixer cget numid=6 | sed -n -E 's/[[:blank:]]{2}:[[:blank:]]values=(.*),.*$/\1/p' | awk '{printf ("%.0f\n",$1/51*100)}'
            val result = Shell.runCommand(
                "amixer cget numid=6 | sed -n -E 's/[[:blank:]]{2}:[[:blank:]]values=(.*),.*\$/\\1/p' | awk '{printf (\"%.0f\\n\",\$1/51*100)}'",
                "Could not get the volume."
            )
            return result.stdOut;
        }
    },
    POWER_GET {
        override fun run(): Any? {
            return when (Shell.runCommand("gpio -g read 17", "Could not get power status.").stdOut) {
                "1" -> true
                "0" -> false
                else -> false
            }
//            return false;
        }
    },
    POWER_ON {
        override fun run(): Any? {
            if(POWER_GET.run() == "0") {
                Shell.runCommand("gpio -g mode 18 out", "Could not turn power on.")
                Thread.sleep(50)
                Shell.runCommand("gpio -g mode 18 in", "Could not turn power on.")
            }

            return null;
        }
    };

    abstract fun run() : Any?
}




//when (message) {
//    "get-volume" -> conn?.send("Volume is ${shellHelper.getVolume()}")
//    "volume-up" -> shellHelper.volumeUp()
//    "volume-down" -> shellHelper.volumeDown()
//    "set-volume" -> shellHelper.setVolume(50)
//    "get-power" -> conn?.send( if(shellHelper.isOn()) "TV is on" else "TV is off")
//    "turn-on" -> shellHelper.turnOnOff(true)
//    "turn-off" -> shellHelper.turnOnOff(false)
//    "help" -> conn?.send("get-volume volume-up volume-down set-volume get-power turn-on turn-off")
//}