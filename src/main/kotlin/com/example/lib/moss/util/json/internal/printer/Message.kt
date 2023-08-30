package com.example.lib.moss.util.json.internal.printer

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.internal.util.dCheck
import com.example.lib.moss.util.json.internal.PrinterImpl
import com.example.lib.moss.util.json.internal.TAG

/**
 * 描述：递归调用写message.
 *
 * Created by zhangyuling on 2023/6/2
 */

internal fun PrinterImpl.doPrintElse(value: Any?) {

    val message = value as? ProtoMessage
    if (message == null) {
        // Unlikely, value 未能转成 proto message类型.
        dCheck(false)
        UtilRuntime.e(TAG, "PrinterImpl.doPrintElse unknown type=" + if (value != null) value::class.java else null)

        doPrintUnknownObject()
    } else {
        doPrint(message)
    }
}

internal fun PrinterImpl.doPrintUnknownObject() {
    writer.beginObject()
    writer.endObject()
}

