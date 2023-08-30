package com.example.lib.moss.util.test.json

import com.example.lib.moss.api.ProtoMessage
import java.util.*

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/27
 */

internal fun checkEqual(`this`: ProtoMessage, other: ProtoMessage) {
    assert(`this` == other)

    val thisData = `this`.toByteArray()
    val otherData = other.toByteArray()
    assert(Arrays.equals(thisData, otherData))
}

internal fun checkNotEqual(`this`: ProtoMessage, other: ProtoMessage) {
    assert(`this` != other)

    val thisData = `this`.toByteArray()
    val otherData = other.toByteArray()
    assert(!Arrays.equals(thisData, otherData))
}