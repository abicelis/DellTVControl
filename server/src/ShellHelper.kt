package ca.alejandrobicelis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.apache.commons.io.IOUtils

class ShellHelper {
    companion object {
        var debug = false;

        fun runCommand(command: String) : CommandResult {
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
            return result;
        }

        suspend fun runCommandAsync(command: String) {
            withContext(Dispatchers.IO) {
            if(debug)
                println("Running command async: '$command'")
                val p = Runtime.getRuntime().exec(command);
                val exitVal = p.waitFor()

                val result = CommandResult(exitVal,
                        IOUtils.toString(p.inputStream, Charsets.UTF_8),
                        IOUtils.toString(p.errorStream, Charsets.UTF_8))

                if(debug) {
                    println("Process exit val: ${result.exitVal}")
                    println("stdOut: ${result.stdOut}")
                    println("stdErr: ${result.stdErr}")
                }
            }
        }

        suspend fun volumeUp() {
            runCommandAsync("/usr/bin/amixer -M set PCM 5%+")
        }

        suspend fun volumeDown() {
            runCommandAsync("/usr/bin/amixer -M set PCM 5%-")
        }

        fun getPower() : Boolean? {
            val result = runCommand("gpio -g read 17")

            return when (result.exitVal) {
                0 -> result.stdOut.toBoolean()
                else -> null
            }
        }

        suspend fun togglePower() {
            runCommandAsync("gpio -g mode 18 out")
            delay(50)
            runCommandAsync("gpio -g mode 18 in")
        }

        fun getVolume() : String? {
            //amixer cget numid=6 | sed -n -E 's/[[:blank:]]{2}:[[:blank:]]values=(.*),.*$/\1/p' | awk '{printf ("%.0f\n",$1/51*100)}'
            val result = runCommand("amixer cget numid=6 | sed -n -E 's/[[:blank:]]{2}:[[:blank:]]values=(.*),.*\$/\\1/p' | awk '{printf (\"%.0f\\n\",\$1/51*100)}'")

            return when (result.exitVal) {
                0 -> result.stdOut
                else -> null
            }
        }
    }
}