package data

data class CommandResult(val exitVal: Int = -1, val stdOut: String = "", val stdErr: String = "")