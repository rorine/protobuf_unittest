package com.example.lib.moss.api

import com.google.protobuf.GeneratedMessageLite

/**
 * 描述：
 *
 * Created by zhangyuling on 2023-07-04
 */
typealias ProtoMessage = GeneratedMessageLite<*, *>
typealias ProtoMessageBuilder = GeneratedMessageLite.Builder<*, *>

typealias ProtoAny = com.google.protobuf.Any
typealias ProtoAnyBuilder = com.google.protobuf.Any.Builder
typealias KotlinAny = Any

// rest query encoder.
typealias RestReqQueryEncoder = (String) -> String
