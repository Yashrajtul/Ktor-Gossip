package com.gossip.room

import io.ktor.websocket.*

data class Member(
    val userId: String,
    val sessionId: String,
    val socket: WebSocketSession
)
