package com.example.lib.moss.util.rest.internal.body

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.exception.ProtoUtilException
import com.example.lib.moss.util.json.JsonFormat

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/29
 */

internal class BodyJson {

    @Throws(ProtoUtilException::class)
    fun <T : ProtoMessage> get(req: T): String {
        return JsonFormat.printer().print(req)
    }
}