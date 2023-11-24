package com.gossip

import com.gossip.di.mainModule
import com.gossip.plugins.*
import io.ktor.server.application.*
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    startKoin {
        modules(mainModule)
    }
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}
