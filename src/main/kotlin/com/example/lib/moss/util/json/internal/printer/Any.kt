package com.example.lib.moss.util.json.internal.printer

import com.example.lib.moss.api.KotlinAny
import com.example.lib.moss.api.ProtoAny
import com.example.lib.moss.util.any.unpack
import com.example.lib.moss.util.common.internal.ANY_TYPE_URL
import com.example.lib.moss.util.common.internal.util.dCheck
import com.example.lib.moss.util.json.internal.PrinterImpl

/**
 * 描述：解析any类型并按照约定格式写json.
 *
 * Created by zhangyuling on 2023/6/2
 */

internal fun PrinterImpl.doPrintAny(value: KotlinAny?) {
    val any = value as? ProtoAny
    if (any == null) {
        // Unlikely.
        dCheck(false)
        doPrintUnknownObject()
        return
    } else {
        // Continue.
    }
    // 使用 Any.kt 方法解析any.
    val message = unpack(any)
    if (message != null) {
        // 如果能够解出来, 那么打印解出来的message.
        doPrint(message, any.typeUrl)
    } else {
        // 否则打印原始的proto any.
        doPrint(any)
    }
}

internal fun PrinterImpl.doPrintTypeUrl(typeUrl: String) {
    writer.name(ANY_TYPE_URL)
    writer.value(typeUrl)
}
