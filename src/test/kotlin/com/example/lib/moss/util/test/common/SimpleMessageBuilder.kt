package com.example.lib.moss.util.test.common

import com.bapis.example.app.interfaces.v1.Embedded
import com.bapis.example.app.interfaces.v1.ErrorMessage
import com.bapis.example.app.interfaces.v1.SimpleMessage

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/26
 */

internal fun buildSimpleMessage(): SimpleMessage {

    val root = SimpleMessage.newBuilder()
    root.id = 100
    root.num = 1000;
    root.lang = "zh"

    val embedded = Embedded.newBuilder()
    embedded.boolVal = true
    embedded.int32Val = 1111
    embedded.addRepeatedStringVal("aaa")
    embedded.addRepeatedStringVal("bbb")
    embedded.putMapStringVal("key", "value")

    val error = ErrorMessage.newBuilder()
    error.code = 10000
    embedded.putMapErrorVal("key", error.build())

    root.embedded = embedded.build()
    return root.build()
}