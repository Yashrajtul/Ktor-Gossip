package com.gossip.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Message(
    val messageContent: String,
    val userId: String,
    val roomId: String,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString()
)
