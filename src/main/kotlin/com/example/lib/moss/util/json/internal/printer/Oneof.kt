package com.example.lib.moss.util.json.internal.printer

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.internal.protocol.parseOneof
import com.example.lib.moss.util.common.naming.JavaFieldName
import com.example.lib.moss.util.json.internal.PrinterImpl
import com.example.lib.moss.util.json.internal.TAG
import java.lang.reflect.Field

/**
 * 描述：写入oneof类型.
 *
 * Created by zhangyuling on 2023/6/2
 */

internal fun PrinterImpl.doPrintOneof(f: Field, message: ProtoMessage) {
    try {
        val parsed = parseOneof(f, message)

        if (parsed == null) {
            // Unlikely.
        } else {
            writer.name(JavaFieldName.toOneofKeyName(parsed.enumValue, protoKeyStyle))
            doPrintValueWoField(parsed.realType, parsed.realValue)
        }
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
    }
}