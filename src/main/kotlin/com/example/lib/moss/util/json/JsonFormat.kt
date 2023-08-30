package com.example.lib.moss.util.json

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.api.ProtoMessageBuilder
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.ProtoKeyStyle
import com.example.lib.moss.util.exception.ProtoUtilException
import com.example.lib.moss.util.json.internal.ParserImpl
import com.example.lib.moss.util.json.internal.PrinterImpl
import java.io.ByteArrayOutputStream

/**
 * 描述：由protoc-javalite 插件生成 java message 转json.
 *
 * Created by zhangyuling on 2023/6/3
 */

internal const val TAG = "moss.util.json"

class JsonFormat {

    companion object {
        @JvmStatic
        fun printer(): Printer {
            return Printer()
        }

        @JvmStatic
        fun parser(): Parser {
            return Parser()
        }
    }
}

class Printer(val protoKeyStyle: ProtoKeyStyle = ProtoKeyStyle.LOWER_SNAKE_CASE) {


    fun protoKeyStyle(value: ProtoKeyStyle): Printer {
        return Printer(value)
    }

    @Throws(ProtoUtilException::class)
    fun print(message: ProtoMessage, typeUrl: String? = null): String {

        return try {
            val output = ByteArrayOutputStream()
            PrinterImpl(output, protoKeyStyle).print(message, typeUrl)

            output.toString("UTF-8")
        } catch (t: Throwable) {
            UtilRuntime.e(TAG, t)
            throw ProtoUtilException(t.message, t.cause)
        }
    }
}

class Parser(val protoKeyStyle: ProtoKeyStyle = ProtoKeyStyle.LOWER_SNAKE_CASE, val ignoringUnknownFields: Boolean = true) {

    fun protoKeyStyle(value: ProtoKeyStyle): Parser {
        return Parser(value, ignoringUnknownFields)
    }

    fun ignoringUnknownFields(value: Boolean): Parser {
        return Parser(protoKeyStyle, value)
    }

    @Throws(ProtoUtilException::class)
    fun merge(json: String, builder: ProtoMessageBuilder) {

        try {
            ParserImpl(protoKeyStyle, ignoringUnknownFields).merge(json, builder)
        } catch (t: Throwable) {
            UtilRuntime.e(TAG, t)
            throw ProtoUtilException(t.message, t.cause)
        }
    }
}