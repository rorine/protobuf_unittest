package com.example.lib.moss.util.test.rest

import com.example.lib.moss.util.rest.KV
import com.example.lib.moss.util.rest.getEncodedPartialBodyForm
import com.example.lib.moss.util.test.common.buildFullReq
import com.example.lib.moss.util.test.common.initDelegate
import org.junit.Before
import org.junit.Test

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/27
 */

class PartialBodyFormTest {

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
    fun testReq1() {
        val message = buildFullReq()
        val current_water = getEncodedPartialBodyForm("current_water", message)
        assertForm(current_water, "current_water.count=100&current_water.tank.volumn=5")
    }

    @Test
    fun testReq2() {
        val message = buildFullReq()
        val building = getEncodedPartialBodyForm("building", message)
        assertForm(building, "building=2")
    }

    @Test
    fun testReq3() {
        val message = buildFullReq()
        val room = getEncodedPartialBodyForm("room", message)
        assertForm(room, "room=402")
    }

    @Test
    fun testReq4() {
        val message = buildFullReq()
        /*
         * 这里和body json 不同的地方是，如果是默认值，要不要传呢？body json是传的，因为没有检查是不是默认值.
         *
         * 更新为, 选择特定field创建form的, 不忽略默认值.
         */
        val is_lockdown = getEncodedPartialBodyForm("is_lockdown", message)
        assertForm(is_lockdown, "is_lockdown=false")
    }

    @Test
    fun testReq5() {
        val message = buildFullReq()
        val name = getEncodedPartialBodyForm("name", message)
        assertForm(name, "name=hello%20json%20%21.~%2A-")
    }

    @Test
    fun testReq6() {
        val message = buildFullReq()
        val backup_water = getEncodedPartialBodyForm("backup_water", message)
        assertForm(backup_water, "backup_water.count=100000&backup_water.tank.volumn=50&backup_water.count=200000&backup_water.tank.volumn=50")
    }

    @Test
    fun testReq7() {
        val message = buildFullReq()
        val food = getEncodedPartialBodyForm("food", message)
        // assert(food == "{\"count\":9999999,\"foodMap\":{\"rice\":{\"meals\":\"111111\"},\"noddle\":{\"meals\":\"222222\"}}}")
        // assert(food == "{\"count\":9999999,\"food_map\":{\"rice\":{\"meals\":\"111111\"},\"noddle\":{\"meals\":\"222222\"}}}")
        assertForm(food, "food.count=9999999&food.food_map%5Brice%5D.meals=111111&food.food_map%5Bnoddle%5D.meals=222222")
    }

    @Test
    fun testReq8() {
        val message = buildFullReq()
        val bytes_value = getEncodedPartialBodyForm("bytes_value", message)
        assertForm(bytes_value, "bytes_value=aGVsbG8gd29ybGQ%3D")
    }

    @Test
    fun testReq9() {
        val message = buildFullReq()
        val lockdown_type = getEncodedPartialBodyForm("lockdown_type", message)
        // assertForm(lockdown_type, "lockdown_type=LockdownTypePrecaution")
        assertForm(lockdown_type, "lockdown_type=2")
    }

    @Test
    fun testReq10() {
        val message = buildFullReq()
        val extra = getEncodedPartialBodyForm("extra", message)
        val expect = listOf(
            "extra%5Btime%5D.%40type=type.googleapis.com%2Fexample.app.interface.v1.Time",
            "extra%5Btime%5D.date=20220529",
            "extra%5Btime%5D.clock.hour=20",
            "extra%5Btime%5D.clock.miniute=57",
            "extra%5Btime%5D.clock.second=30",
            "extra%5Bweather%5D.%40type=type.googleapis.com%2Fexample.app.interface.v1.Weather",
            "extra%5Bweather%5D.temperature=21"
        ).joinToString("&")

        assertForm(extra, expect)
    }

    @Test
    fun testReq11() {
        val message = buildFullReq()
        val week_lockdown_type_list = getEncodedPartialBodyForm("week_lockdown_type_list", message)
//        val expect = "week_lockdown_type_list=LockdownTypeLockdown&" +
//                "week_lockdown_type_list=LockdownTypeLockdown&" +
//                "week_lockdown_type_list=LockdownTypeControl&" +
//                "week_lockdown_type_list=LockdownTypeControl&" +
//                "week_lockdown_type_list=LockdownTypePrecaution"
        val expect = "week_lockdown_type_list=0&" +
                "week_lockdown_type_list=0&" +
                "week_lockdown_type_list=1&" +
                "week_lockdown_type_list=1&" +
                "week_lockdown_type_list=2"
        assertForm(week_lockdown_type_list, expect)
    }

    @Test
    fun testReq12() {
        val message = buildFullReq()
        val another_map = getEncodedPartialBodyForm("another_map", message)
        // 协议并没有定义应该怎么传空值, 但是不应该传一个错误的格式.
        // assertForm(another_map,"another_map=")
        assertForm(another_map, "")
    }

    @Test
    fun testReq13() {
        val message = buildFullReq()
        val another_list = getEncodedPartialBodyForm("another_list", message)
        assertForm(another_list, "")
    }

    /*
     * 注意，表单并不能表达oneof的信息, json是可以的.
     */
    @Test
    fun testReq14() {
        val message = buildFullReq()
        val join = getEncodedPartialBodyForm("join", message)
        assertForm(join, "")
    }

    @Test
    fun testReq15() {
        val message = buildFullReq()
        val null_food = getEncodedPartialBodyForm("null_food", message)
        // assertForm(null_food, "")
        assertForm(null_food, "null_food.count=0")
    }

    @Test
    fun testReq16() {
        val message = buildFullReq()
        val fruits = getEncodedPartialBodyForm("fruits", message)
        val expect = "fruits%5B603%5D.name=apple&fruits%5B603%5D.weight=603603&fruits%5B402%5D.name=watermelon&fruits%5B402%5D.weight=402402"
        assertForm(fruits, expect)
    }

    @Test
    fun testReq17() {
        val message = buildFullReq()
        val bar_int = getEncodedPartialBodyForm("bar_int", message)
        val expect = "bar_int=33"
        assertForm(bar_int, expect)
    }

    @Test
    fun testReq18() {
        val message = buildFullReq()
        val float_value = getEncodedPartialBodyForm("float_value", message)
        val expect = "float_value=3121412.0"
        assertForm(float_value, expect)
    }

    @Test
    fun testReq19() {
        val message = buildFullReq()
        val double_value = getEncodedPartialBodyForm("double_value", message)
        val expect = "double_value=3121412.1231231"
        assertForm(double_value, expect)
    }
}