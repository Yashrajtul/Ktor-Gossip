package com.gossip.data

import com.gossip.data.model.Message

interface MessageDataSource {

    suspend fun getAllMessages(): List<Message>

    suspend fun getMessages(roomId: String): List<Message>

    suspend fun insertMessage(message: Message)
}