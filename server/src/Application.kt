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
            call.respondText("Volume at $volume", contentType = ContentType.Text.Plain)
        }
        get("/volup") {
            ShellHelper.volumeUp()
            call.respond(HttpStatusCode.OK)
        }
        get("/voldown") {
            ShellHelper.volumeDown()
            call.respond(HttpStatusCode.OK)
        }
        get("/power") {
            ShellHelper.togglePower()
            call.respond(HttpStatusCode.OK)
        }
        get("/help") {
            call.respond(
                mapOf(
                    "Turn the volume up"        to "/volup",
                    "Turn the volume down"      to "/voldown",
                    "Toggle the power"          to "/power"
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

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

