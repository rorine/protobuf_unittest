package com.example.lib.moss.util.rest.internal.body

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.exception.ProtoUtilException
import com.example.lib.moss.util.rest.KV
import com.example.lib.moss.util.rest.internal.KvGetter
import com.example.lib.moss.util.rest.internal.TAG

/**
 * 描述：proto message 转form.
 *
 * Created by zhangyuling on 2023/6/26
 */

internal class EncodedBodyForm {

    @Throws(ProtoUtilException::class)
    fun <T : ProtoMessage> get(req: T, typeUrl: String? = null): List<KV> {
        return try {
            val kvs = KvGetter().get(req, typeUrl, emptyList())
            kvs.map { KV(UtilRuntime.urlEscape(it.first), UtilRuntime.urlEscape(it.second)) }

        } catch (e: Exception) {
            UtilRuntime.e(TAG, e)
            throw ProtoUtilException(e.message, e.cause)
        }
    }
}