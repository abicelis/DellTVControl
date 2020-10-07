import org.apache.commons.io.IOUtils

class ShellHelper {
//    companion object {
        private val debug = false;

        private fun runCommand(command: String) : CommandResult {
            if(debug)
                println("Running command: '$command'")

            val p = Runtime.getRuntime().exec(command)
            val exitVal = p.waitFor()
            val result = CommandResult(exitVal,
                    IOUtils.toString(p.inputStream, Charsets.UTF_8),
                    IOUtils.toString(p.errorStream, Charsets.UTF_8))

            if(debug) {
                println("Process exit val: ${result.exitVal}")
                println("stdOut: ${result.stdOut}")
                println("stdErr: ${result.stdErr}")
            }

            if(exitVal != 0)
                throw Exception("Task ended with non-zero result. Stderr: '${result.stdErr}'")

            return result;
        }

//        suspend fun runCommandAsync(command: String) {
//            withContext(Dispatchers.IO) {
//                if(debug)
//                    println("Running command async: '$command'")
//                val p = Runtime.getRuntime().exec(command);
//                val exitVal = p.waitFor()
//
//                val result = CommandResult(exitVal,
//                        IOUtils.toString(p.inputStream, Charsets.UTF_8),
//                        IOUtils.toString(p.errorStream, Charsets.UTF_8))
//
//                if(debug) {
//                    println("Process exit val: ${result.exitVal}")
//                    println("stdOut: ${result.stdOut}")
//                    println("stdErr: ${result.stdErr}")
//                }
//            }
//        }

        fun getVolume() : String {
            //amixer cget numid=6 | sed -n -E 's/[[:blank:]]{2}:[[:blank:]]values=(.*),.*$/\1/p' | awk '{printf ("%.0f\n",$1/51*100)}'
            try {
                val result = runCommand("amixer cget numid=6 | sed -n -E 's/[[:blank:]]{2}:[[:blank:]]values=(.*),.*\$/\\1/p' | awk '{printf (\"%.0f\\n\",\$1/51*100)}'")
                return result.stdOut;
            } catch (e : Exception) {
                throw Exception("Could not get the volume.", e);
            }
        }

        fun volumeUp() {
            try {
                runCommand("/usr/bin/amixer -M set PCM 5%+")
            } catch (e : Exception) {
                throw Exception("Could not increase the volume.", e);
            }
        }

        fun volumeDown() {
            try {
                runCommand("/usr/bin/amixer -M set PCM 5%-")
            } catch (e : Exception) {
                throw Exception("Could not decrease the volume.", e);
            }
        }

        fun setVolume(percent: Int) {
            if(percent > 100 || percent < 0)
                throw Exception("Invalid volume.");

            try {
                runCommand("/usr/bin/amixer -M set PCM ${percent}%")
            } catch (e : Exception) {
                throw Exception("Could not set the volume to ${percent}%", e);
            }
        }

        fun isOn(): Boolean {
            try {
                return (runCommand("gpio -g read 17").stdOut.toBoolean())
            } catch (e : Exception) {
                throw Exception("Could not get power status.", e);
            }
        }

        fun turnOnOff(turnOn: Boolean) {
            try {
                if(turnOn != isOn()) {
                    runCommand("gpio -g mode 18 out")
                    Thread.sleep(50)
                    runCommand("gpio -g mode 18 in")
                }
            } catch (e : Exception) {
                throw Exception("Could not turn " + (if(turnOn) "on" else "off") + ".", e);
            }
        }
//    }
}