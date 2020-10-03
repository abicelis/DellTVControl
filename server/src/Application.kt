package ca.alejandrobicelis

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    ShellHelper.debug = true

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(Authentication) {
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/volume") {
            val volume = ShellHelper.getVolume();

            if(volume != null)
                call.respondText("Volume at $volume", contentType = ContentType.Text.Plain)
            else
                call.respondText("Error getting volume", contentType = ContentType.Text.Plain)
        }
        get("/volup") {
            ShellHelper.volumeUp()
            call.respond(HttpStatusCode.OK)
        }
        get("/voldown") {
            ShellHelper.volumeDown()
            call.respond(HttpStatusCode.OK)
        }

        get("/getpower") {
            val power = ShellHelper.getPower()

            if(power != null)
                call.respondText("TV is " + if (power) "on" else "off", contentType = ContentType.Text.Plain)
            else
                call.respondText("Error getting power status", contentType = ContentType.Text.Plain)
        }

        get("/togglepower") {
            ShellHelper.togglePower()
            call.respond(HttpStatusCode.OK)
        }
        get("/help") {
            call.respond(
                mapOf(
                    "Get the volume"            to "/getvolume",
                    "Turn the volume up"        to "/volup",
                    "Turn the volume down"      to "/voldown",
                    "Get power status"          to "/getpower",
                    "Toggle the power"          to "/togglepower"
                )
            )
        }
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

