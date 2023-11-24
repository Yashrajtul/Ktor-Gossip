package com.gossip.room

import com.gossip.data.MessageDataSource
import com.gossip.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        userId: String,
        sessionId: String,
        socket: WebSocketSession
    ){
        if (members.containsKey(userId)){
            throw MemberAlreadyExistsException()
        }
        members[userId] = Member(
            userId = userId,
            sessionId = sessionId,
            socket = socket
        )
    }

    suspend fun sendMessage(senderUserId: String, message: String, roomId: String = ""){
        val messageEntry = Message(
            messageContent = message,
            userId = senderUserId,
            roomId = roomId,
            timestamp = System.currentTimeMillis()
        )
        messageDataSource.insertMessage(messageEntry)
        members.values.forEach{member->
            val parsedMessage = Json.encodeToString(messageEntry)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun getMessages(name: String): List<Message> {
        return messageDataSource.getMessages(name)
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) {
            members.remove(username)
        }
    }
}