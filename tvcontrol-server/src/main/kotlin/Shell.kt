import data.CommandResult
import data.Pin
import org.apache.commons.io.IOUtils

class Shell {
    companion object {
        var debug = false

        fun runCommand(command: String, exceptionMessage: String) : CommandResult {
            try {
                if(debug)
                    println("Running command: '$command'")

                val p = Runtime.getRuntime().exec(command)
                val exitVal = p.waitFor()
                val result = CommandResult(exitVal,
                    IOUtils.toString(p.inputStream, Charsets.UTF_8).trim(),
                    IOUtils.toString(p.errorStream, Charsets.UTF_8).trim())

                if(debug) {
                    println("Process exit val: ${result.exitVal}")
                    println("stdOut: ${result.stdOut}")
                    println("stdErr: ${result.stdErr}")
                }

                if(exitVal != 0)
                    throw Exception("Task ended with non-zero result. Stderr: '${result.stdErr}'")

                return result
            } catch (e : Exception) {
                throw Exception(exceptionMessage, e);
            }
        }

        fun togglePin(pin: Pin, exceptionMessage: String) {
            runCommand("gpio -g mode ${pin.BCMPin} in", exceptionMessage)
            Thread.sleep(100)
            runCommand("gpio -g mode ${pin.BCMPin} out", exceptionMessage)
            Thread.sleep(100)
            runCommand("gpio -g mode ${pin.BCMPin} in", exceptionMessage)
        }
    }
}