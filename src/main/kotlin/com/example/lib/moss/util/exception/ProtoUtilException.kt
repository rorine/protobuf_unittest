package com.example.lib.moss.util.exception

/**
 * 描述：内部异常, 全部作为 ProtoUtilException 抛出.
 *
 * Created by zhangyuling on 2023/6/29
 */

class ProtoUtilException(message: String?, cause: Throwable?) : Throwable(message, cause)