package com.example.lib.moss.util.json.internal.parser

import com.example.lib.moss.util.UtilRuntime
import com.google.gson.JsonElement
import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import java.math.BigDecimal

/**
 * 描述：From protobuf-java.
 *
 * Created by zhangyuling on 2023/6/28
 */

@Throws(InvalidProtocolBufferException::class)
internal fun parseBool(json: JsonElement): Boolean {
    if (json.asString == "true") {
        return true
    }
    if (json.asString == "false") {
        return false
    }
    throw InvalidProtocolBufferException("Invalid bool value: $json")
}

@Throws(InvalidProtocolBufferException::class)
internal fun parseInt(json: JsonElement): Int {
    try {
        return json.asString.toInt()
    } catch (e: Exception) {
        // Fall through.
    }
    // JSON doesn't distinguish between integer values and floating point values so "1" and
    // "1.000" are treated as equal in JSON. For this reason we accept floating point values for
    // integer fields as well as long as it actually is an integer (i.e., round(value) == value).
    return try {
        val value = BigDecimal(json.asString)
        value.intValueExact()
    } catch (e: Exception) {
        throw InvalidProtocolBufferException("Not an int32 value: $json")
    }
}

@Throws(InvalidProtocolBufferException::class)
internal fun parseLong(json: JsonElement): Long {
    try {
        return json.asString.toLong()
    } catch (e: java.lang.Exception) {
        // Fall through.
    }
    // JSON doesn't distinguish between integer values and floating point values so "1" and
    // "1.000" are treated as equal in JSON. For this reason we accept floating point values for
    // integer fields as well as long as it actually is an integer (i.e., round(value) == value).
    return try {
        val value = BigDecimal(json.asString)
        value.longValueExact()
    } catch (e: java.lang.Exception) {
        throw InvalidProtocolBufferException("Not an int64 value: $json")
    }
}


private const val EPSILON = 1e-6

@Throws(InvalidProtocolBufferException::class)
internal fun parseFloat(json: JsonElement): Float {
    if (json.asString == "NaN") {
        return Float.NaN
    } else if (json.asString == "Infinity") {
        return Float.POSITIVE_INFINITY
    } else if (json.asString == "-Infinity") {
        return Float.NEGATIVE_INFINITY
    }
    return try {
        // We don't use Float.parseFloat() here because that function simply
        // accepts all double values. Here we parse the value into a Double
        // and do explicit range check on it.
        val value = json.asString.toDouble()
        // When a float value is printed, the printed value might be a little
        // larger or smaller due to precision loss. Here we need to add a bit
        // of tolerance when checking whether the float value is in range.
        if (value > Float.MAX_VALUE * (1.0 + EPSILON)
            || value < -Float.MAX_VALUE * (1.0 + EPSILON)
        ) {
            throw InvalidProtocolBufferException("Out of range float value: $json")
        }
        value.toFloat()
    } catch (e: InvalidProtocolBufferException) {
        throw e
    } catch (e: java.lang.Exception) {
        throw InvalidProtocolBufferException("Not a float value: $json")
    }
}

private val MORE_THAN_ONE = BigDecimal((1.0 + EPSILON).toString())

// When a float value is printed, the printed value might be a little
// larger or smaller due to precision loss. Here we need to add a bit
// of tolerance when checking whether the float value is in range.
private val MAX_DOUBLE = BigDecimal(Double.MAX_VALUE.toString()).multiply(MORE_THAN_ONE)
private val MIN_DOUBLE: BigDecimal = BigDecimal(Double.MAX_VALUE.toString()).multiply(MORE_THAN_ONE).unaryMinus()

@Throws(InvalidProtocolBufferException::class)
internal fun parseDouble(json: JsonElement): Double {
    if (json.asString == "NaN") {
        return Double.NaN
    } else if (json.asString == "Infinity") {
        return Double.POSITIVE_INFINITY
    } else if (json.asString == "-Infinity") {
        return Double.NEGATIVE_INFINITY
    }
    return try {
        // We don't use Double.parseDouble() here because that function simply
        // accepts all values. Here we parse the value into a BigDecimal and do
        // explicit range check on it.
        val value = BigDecimal(json.asString)
        if (value.compareTo(MAX_DOUBLE) > 0 || value.compareTo(MIN_DOUBLE) < 0) {
            throw InvalidProtocolBufferException("Out of range double value: $json")
        }
        value.toDouble()
    } catch (e: InvalidProtocolBufferException) {
        throw e
    } catch (e: java.lang.Exception) {
        throw InvalidProtocolBufferException("Not an double value: $json")
    }
}

internal fun parseString(json: JsonElement): String {
    return json.asString
}

@Throws(InvalidProtocolBufferException::class)
internal fun parseBytes(json: JsonElement): ByteString {
    return try {
        ByteString.copyFrom(UtilRuntime.base64Decode(json.asString))
    } catch (e: IllegalArgumentException) {
        ByteString.copyFrom(UtilRuntime.base64Decode(json.asString))
    }
}

