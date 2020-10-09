import org.apache.commons.io.IOUtils

class Shell {
    companion object {
        private var debug = false

//        fun isDebug(): Boolean {
//            return debug
//        }
//
//        fun setDebug(d: Boolean) {
//            debug = d
//        }

        fun runCommand(command: String, exceptionMessage: String) : CommandResult {
            try {
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

                return result
            } catch (e : Exception) {
                throw Exception(exceptionMessage, e);
            }
        }
    }
}