package com.example.lib.moss.util.common.types

import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.internal.NEW_BUILDER_METHOD
import com.example.lib.moss.util.common.internal.TAG
import com.example.lib.moss.util.common.internal.getMethodNoArg
import com.example.lib.moss.util.common.naming.ProtoMessageClassName

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/13
 */

/**
 * 创建一个builder 对象. 考虑兼容性, 不直接调用私有的构造方法, 而是找到原始类, 然后调用newBuilder方法.
 */
fun newBuilder(builder: Class<*>): Any? {
    return try {
        val messageName = ProtoMessageClassName.fromBuilderName(builder.canonicalName)
        val message = Class.forName(messageName)

        newBuilderByMessage(message)
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        return null
    }
}

/**
 * 创建一个builder instance.
 */
fun newBuilderByMessage(message: Class<*>): Any? {
    return try {
        val method = getMethodNoArg(NEW_BUILDER_METHOD, message)

        method?.invoke(null)
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}