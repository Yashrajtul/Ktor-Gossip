package com.gossip.plugins

import com.gossip.room.RoomController
import com.gossip.routes.chatSocket
import com.gossip.routes.getAllMessages
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.core.context.GlobalContext.get

fun Application.configureRouting() {
    val roomController = get().get<RoomController>()
    install(Routing) {
        chatSocket(roomController)
        getAllMessages(roomController)
    }
}
