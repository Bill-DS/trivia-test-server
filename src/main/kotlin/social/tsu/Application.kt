package social.tsu

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import social.tsu.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        println("-- server starting")
        configureRouting()
        configureSerialization()
        configureSockets()

        routing {
            get ("/test") {
                println("responding to /test endpoint")
                call.respond("Hello")
            }
        }
    }.start(wait = true)
}
