package com.gossip.routes

import com.gossip.room.MemberAlreadyExistsException
import com.gossip.room.RoomController
import com.gossip.session.ChatSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.chatSocket(roomController: RoomController) {
    webSocket ("/chat-socket"){
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
            return@webSocket
        }
        try {
            roomController.onJoin(
                userId = session.userId,
                sessionId = session.sessionId,
                socket = this
            )
            incoming.consumeEach {frame->
                if (frame is Frame.Text) {
                    roomController.sendMessage(
                        senderUserId = session.userId,
                        message = frame.readText()
                    )
                }
            }
        }catch (e: MemberAlreadyExistsException){
            call.respond(HttpStatusCode.Conflict)
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            roomController.tryDisconnect(session.userId)
        }

    }
    webSocket ("/ws/{roomId}"){
        val roomId = call.parameters["roomId"]
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
            return@webSocket
        }
        try {
            roomController.onJoin(
                userId = session.userId,
                sessionId = session.sessionId,
                socket = this
            )
            incoming.consumeEach {frame->
                if (frame is Frame.Text) {
                    roomController.sendMessage(
                        senderUserId = session.userId,
                        message = frame.readText(),
                        roomId = roomId!!
                    )
                }
            }
        }catch (e: MemberAlreadyExistsException){
            call.respond(HttpStatusCode.Conflict)
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            roomController.tryDisconnect(session.userId)
        }

    }

}

fun Route.getAllMessages(roomController: RoomController) {
    get("/messages") {
        call.respond(
            HttpStatusCode.OK,
            roomController.getAllMessages()
        )
    }
    get("/messages/{roomId}" , body = {
        val roomId = call.parameters["roomId"]
        call.respond(
            HttpStatusCode.OK,
            roomController.getMessages(roomId!!)
        )
    })
}
