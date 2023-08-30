package com.example.lib.moss.util.test

import com.bapis.example.app.interfaces.v1.FullReq
import com.bapis.example.app.interfaces.v1.LockdownType
import com.example.lib.moss.util.common.ProtoKeyStyle
import com.example.lib.moss.util.json.JsonFormat
import com.example.lib.moss.util.test.common.initDelegate
import org.junit.Before
import org.junit.Test

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/7/1
 */

class CompatibilityTest {

    @Before
    fun setUp() {
        initDelegate()
    }

    // 枚举兼容性测试.
    @Test
    fun testEnumInJson() {

        val root = FullReq.newBuilder()
        root.lockdownType = LockdownType.LockdownTypeControl
        val rawReq = root.build()

        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        // assert(json == "{\"lockdownType\":\"LockdownTypeControl\"}")
        assert(json == "{\"lockdownType\":1}")

        // val json2 = "{\"lockdownType\":\"LockdownTypeAnotherValueDefinedInNewVersion\"}"
        val json2 = "{\"lockdownType\":2233}"
        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json2, builder)

        val fullReq = builder.build()
        assert(fullReq.lockdownType == LockdownType.UNRECOGNIZED)
    }

    // 枚举兼容性测试.
    @Test
    fun testEnumInJson2() {
        // val json = "{\"weekLockdownTypeList\":[\"LockdownTypeLockdown\",\"LockdownTypeNewEnum1\",\"LockdownTypeControl\",\"LockdownTypeNewEnum2\",\"LockdownTypePrecaution\"]}"
        val json = "{\"weekLockdownTypeList\":[0,22,1,33,2]}"

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)
        val fullReq = builder.build()

        val json2 = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(fullReq)
        // assert(json2 == "{\"weekLockdownTypeList\":[\"LockdownTypeLockdown\",\"UNRECOGNIZED\",\"LockdownTypeControl\",\"UNRECOGNIZED\",\"LockdownTypePrecaution\"]}")
        assert(json2 == "{\"weekLockdownTypeList\":[0,22,1,33,2]}")
    }

    // 枚举兼容性测试.
    @Test
    fun testEnumInJson3() {
        // val json = "{\"weekLockdownTypeMap\":{\"529\":\"LockdownTypeNewEnum1\",\"530\":\"LockdownTypeNewEnum2\",\"531\":\"LockdownTypeNewEnum3\",\"601\":\"LockdownTypeControl\",\"602\":\"LockdownTypeNewEnum4\"}}"
        val json = "{\"weekLockdownTypeMap\":{\"529\":22,\"530\":33,\"531\":44,\"601\":1,\"602\":55}}"

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)
        val fullReq = builder.build()

        val json2 = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(fullReq)
        // assert(json2 == "{\"weekLockdownTypeMap\":{\"529\":\"UNRECOGNIZED\",\"530\":\"UNRECOGNIZED\",\"531\":\"UNRECOGNIZED\",\"601\":\"LockdownTypeControl\",\"602\":\"UNRECOGNIZED\"}}")
        assert(json2 == "{\"weekLockdownTypeMap\":{\"529\":22,\"530\":33,\"531\":44,\"601\":1,\"602\":55}}")
    }
}