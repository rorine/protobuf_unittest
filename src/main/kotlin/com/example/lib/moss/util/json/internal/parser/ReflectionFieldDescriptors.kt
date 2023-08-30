package com.example.lib.moss.util.json.internal.parser

import java.lang.reflect.Method

/**
 * 描述：类似 objc中的 field descriptor, 带有SEL.
 *
 * Created by zhangyuling on 2023/6/27
 */

internal open class FieldDescriptor(val method: Method, val valueClass: Class<*>, val enumRawMethod: Method?)

internal class RepeatedFieldDescriptor(method: Method, valueClass: Class<*>, enumRawMethod: Method?) : FieldDescriptor(method, valueClass, enumRawMethod)

internal class MapFieldDescriptor(method: Method, val keyClass: Class<*>, valueClass: Class<*>, enumRawMethod: Method?) : FieldDescriptor(method, valueClass, enumRawMethod)