package com.gossip.data

import com.gossip.data.model.Message
import com.mongodb.client.model.Filters.eq
import org.litote.kmongo.coroutine.CoroutineDatabase

class MessageDataSourceImpl(
    private val db: CoroutineDatabase
) : MessageDataSource {

    private val messages = db.getCollection<Message>()
    override suspend fun getAllMessages(): List<Message> {
        return messages.find()
            .descendingSort(Message::timestamp)
            .toList()
    }

    override suspend fun getMessages(roomId: String): List<Message> {
//        return messages.find(filter = eq("username",roomId))
//            .descendingSort(Message::timestamp)
//            .toList()
        return messages.find(filter = eq("roomId",roomId))
            .descendingSort(Message::timestamp)
            .toList()

    }

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }
}