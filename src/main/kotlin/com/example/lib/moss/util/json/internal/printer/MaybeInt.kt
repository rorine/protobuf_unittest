package com.example.lib.moss.util.json.internal.printer

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.internal.protocol.parseInt
import com.example.lib.moss.util.common.internal.util.dCheck
import com.example.lib.moss.util.common.types.isProtoEnum
import com.example.lib.moss.util.json.internal.PrinterImpl
import com.example.lib.moss.util.json.internal.TAG
import java.lang.reflect.Field

/**
 * 描述：写入int类型.
 *
 * Created by zhangyuling on 2023/6/2
 */

// name 检查是否为protobuf enum.
internal fun PrinterImpl.doPrintMaybeInt(f: Field, message: ProtoMessage) {
    try {
        val parsed = parseInt(f, message)

        if (parsed == null) {
            // Unlikely.
            doPrintInt(f.getInt(message))
            return

        } else {
            if (parsed.isInt) {
                doPrintInt(parsed.intValue)

            } else if (parsed.isProtoEnum) {
//                val enum = parsed.readValue as? Enum<*>
//                if (enum != null) {
//                    doPrintEnum(enum)
//                } else {
//                    // Unlikely.
//                    doPrintInt(parsed.intValue)
//                }
                // 根据协议, 枚举转为int
                doPrintInt(parsed.intValue)

            } else {
                // Unlike since we checked oneof enum before.
                doPrintInt(parsed.intValue)
            }
        }
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        doPrintInt(0)
    }
}

internal fun PrinterImpl.doPrintInt(value: Int) {
    writer.value(value)
}

///**
// * 枚举值写name字符串.
// */
//internal fun PrinterImpl.doPrintEnum(value: Enum<*>) {
//    writer.value(value.name)
//}
//
//internal fun PrinterImpl.doPrintUnrecognizedEnum(){
//    writer.value(UNRECOGNIZED_ENUM_VALUE_NAME)
//}

/**
 * 根据枚举class 和 int值转枚举. 外部message可能没有这个field.
 */
internal fun PrinterImpl.doPrintEnum(value: Int, clazz: Class<*>) {
    dCheck(isProtoEnum(clazz))
//    try {
//        val method = clazz.getMethod(FOR_NUMBER, Int::class.java)
//        method.isAccessible = true
//        val enumValue = method.invoke(null, value)
//        (enumValue as? Enum<*>)?.let {
//            doPrintEnum(it)
//        } ?: doPrintUnrecognizedEnum()
//    } catch (e: Exception) {
//        UtilRuntime.e(TAG, e)
//        doPrintUnrecognizedEnum()
//    }

    // 根据协议, 枚举转为int
    doPrintInt(value)
}