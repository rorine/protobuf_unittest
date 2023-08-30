package com.example.lib.moss.util.test.rest

import com.bapis.example.app.interfaces.v2.Able
import com.example.lib.moss.util.rest.KV
import com.example.lib.moss.util.rest.getEncodedBodyForm
import com.example.lib.moss.util.test.common.initDelegate
import org.junit.Before
import org.junit.Test

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/27
 */

class BodyFormTest {

    @Before
    fun setUp() {
        initDelegate()
    }

    private fun List<KV>?.print(): String? {
        return this?.joinToString(separator = "&") { "${it.first}=${it.second}" }
    }

    private fun assertForm(form: List<KV>?, expect: String) {
        assert(form.print() == expect)
    }

    @Test
    fun testAble1() {
        val able = Able.newBuilder()
        able.addAStrings("Foo")
        able.addAStrings("Bar")
        var form = getEncodedBodyForm(able.build())
        // assertForm(form, "a_strings=Foo&a_strings=Bar")
        assertForm(form, "a_strings=Foo&a_strings=Bar&dog_int=0&dog_enum=0&is_able=false&bytes_instance=&float_value=0.0&double_value=0.0")
    }
}