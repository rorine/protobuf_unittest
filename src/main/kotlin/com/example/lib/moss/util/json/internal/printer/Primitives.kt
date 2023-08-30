package com.example.lib.moss.util.json.internal.printer

import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.json.internal.PrinterImpl
import com.google.protobuf.ByteString

/**
 * 描述：写入原始和String类型.
 *
 * Created by zhangyuling on 2023/6/2
 */

// 字符串值写字符串, 默认初始化为"".
internal fun PrinterImpl.doPrintString(value: String?) {
    if (value == null) {
        // Unlikely.
        writer.value("")
    } else {
        writer.value(value)
    }
}

// long类型值写数值.
internal fun PrinterImpl.doPrintLong(value: Long) {
    writer.value(value)
}

// boolean类型值直接写.
internal fun PrinterImpl.doPrintBoolean(value: Boolean) {
    writer.value(value)
}

// double类型值写数值.
internal fun PrinterImpl.doPrintDouble(value: Double) {
    writer.value(value)
}

// float类型值写数值.
internal fun PrinterImpl.doPrintFloat(value: Float) {
    writer.value(value)
}

// bytestring 类型进行 base64 编码.
internal fun PrinterImpl.doPrintByteString(value: ByteString?) {
    if (value == null) {
        // Unlikely.
        writer.value("")
    } else {
        writer.value(UtilRuntime.base64Encode(value.toByteArray()))
    }
}
