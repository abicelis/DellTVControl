package ca.alejandrobicelis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.io.IOUtils

class ShellHelper {
    companion object {
        var debug = false;

        suspend fun runCommand(command: String) {
            withContext(Dispatchers.IO) {
                if(debug)
                    println("Running command=$command")
                val p = Runtime.getRuntime().exec(command);
                val res = p.waitFor()
                val stdOut = IOUtils.toString(p.inputStream, Charsets.UTF_8)
                val stdErr = IOUtils.toString(p.errorStream, Charsets.UTF_8)

                if(debug) {
                    println("Process result: $res")
                    println("stdOut: $stdOut")
                    println("stdErr: $stdErr")
                }
            }
        }
        suspend fun volumeUp() {
            runCommand("/usr/bin/amixer -M set PCM 5%+")
        }

        suspend fun volumeDown() {
            runCommand("/usr/bin/amixer -M set PCM 5%-")
        }

        suspend fun togglePower() {
            runCommand("echo 'powa!'")
        }

        fun getVolume() : String {
//            val p = Runtime.getRuntime().exec("amixer cget numid=6");
            val p = Runtime.getRuntime().exec("echo '100%'");
            val res = p.waitFor()
            val stdOut = IOUtils.toString(p.inputStream, Charsets.UTF_8)
            val stdErr = IOUtils.toString(p.errorStream, Charsets.UTF_8)

            return when (res) {
                0 -> stdOut
                else -> stdErr
            }
        }
    }
}