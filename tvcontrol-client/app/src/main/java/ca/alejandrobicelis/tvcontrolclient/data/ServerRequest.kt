package ca.alejandrobicelis.tvcontrolclient.data

/**
 * Created by abicelis on 2020-10-11
 */

import kotlinx.serialization.Serializable

@Serializable
data class ServerRequest(val action: String, val value: String)