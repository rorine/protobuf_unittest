package com.example.lib.moss.util.rest.internal.query

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.api.RestReqQueryEncoder
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.exception.ProtoUtilException
import com.example.lib.moss.util.rest.internal.KvGetter
import com.example.lib.moss.util.rest.internal.TAG

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/7
 */

internal class EncodedQueryStringGetter {

    /**
     * 递归遍历 proto message, 跳过skipFields, 生成指定的encoded query.
     */
    fun <T : ProtoMessage> get(req: T, skipFields: List<String>, bizEncoder: RestReqQueryEncoder?): String? {
        return try {
            val kvs = KvGetter().get(req, null, skipFields)
            if (bizEncoder != null) {
                // 为业务开放 query encoder.
                kvs.joinToString(separator = "&") { "${bizEncoder(it.first)}=${bizEncoder(it.second)}" }
            } else {
                kvs.joinToString(separator = "&") { "${UtilRuntime.urlEscape(it.first)}=${UtilRuntime.urlEscape(it.second)}" }
            }

        } catch (e: Exception) {
            UtilRuntime.e(TAG, e)
            throw ProtoUtilException(e.message, e.cause)
        }
    }
}