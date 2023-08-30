package com.example.lib.moss.util.common.internal.util

import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.exception.ProtoUtilException

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/14
 */

@Throws(ProtoUtilException::class)
internal fun dCheck(check: Boolean) {
    if (UtilRuntime.isDebug()) {
        if (check) {
            // As expected.
        } else {
            throw ProtoUtilException("Debug check failed.", null)
        }
    } else {
        // Ignore in release.
    }
}