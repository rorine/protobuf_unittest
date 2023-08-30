package com.example.lib.moss.util.rest

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.api.RestReqQueryEncoder
import com.example.lib.moss.util.exception.ProtoUtilException
import com.example.lib.moss.util.rest.internal.body.BodyJson
import com.example.lib.moss.util.rest.internal.body.EncodedBodyForm
import com.example.lib.moss.util.rest.internal.body.EncodedPartialBodyFormGetter
import com.example.lib.moss.util.rest.internal.body.PartialBodyJsonGetter
import com.example.lib.moss.util.rest.internal.path.PathStringGetter
import com.example.lib.moss.util.rest.internal.query.EncodedQueryStringGetter
import kotlin.jvm.Throws


/**
 * 描述：提供rest相关接口.
 *
 * Created by zhangyuling on 2023/6/3
 */

typealias KV = Pair<String, String>

internal const val TAG = "moss.util.rest"

/**
 * 根据field name获得对应field的字符串值. 未url escaping. 如果field对应类型为message, 那么返回json.
 */
fun <T : ProtoMessage> getPathString(protoField: String, req: T): String? {
    return PathStringGetter().get(protoField, req)
}

/**
 * 整个 message 转json.
 */
@Throws(ProtoUtilException::class)
fun <T : ProtoMessage> getBodyJson(req: T): String {
    return BodyJson().get(req)
}

/**
 * 根据field name 获得对应的json.
 */
@Throws(ProtoUtilException::class)
fun <T : ProtoMessage> getPartialBodyJson(protoField: String, req: T): String? {
    return PartialBodyJsonGetter().get(protoField, req)
}

/**
 * 整个 message 转form, url encoded.
 */
@Throws(ProtoUtilException::class)
fun <T : ProtoMessage> getEncodedBodyForm(req: T): List<KV> {
    return EncodedBodyForm().get(req)
}

/**
 * 根据field name 获得对应的form, url encoded.
 */
@Throws(ProtoUtilException::class)
fun <T : ProtoMessage> getEncodedPartialBodyForm(protoField: String, req: T): List<KV>? {
    return EncodedPartialBodyFormGetter().get(protoField, req)
}

/**
 * 遍历对象生成encoded query字符串.
 */
@Throws(ProtoUtilException::class)
fun <T : ProtoMessage> getEncodedQueryString(req: T, skipFields: List<String>, bizEncoder: RestReqQueryEncoder? = null): String? {
    return EncodedQueryStringGetter().get(req, skipFields, bizEncoder)
}
