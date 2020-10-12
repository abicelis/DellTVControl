package data

import kotlinx.serialization.Serializable

@Serializable
data class ServerResponse(val name: String = "", val value: String = "", val error: String = "")