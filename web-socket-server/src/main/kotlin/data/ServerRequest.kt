package data

import kotlinx.serialization.Serializable

@Serializable
data class ServerRequest(val action: String, val value: String)