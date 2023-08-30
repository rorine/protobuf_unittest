package com.example.lib.moss.util.test.rest

import com.bapis.example.app.interfaces.v2.City
import com.bapis.example.app.interfaces.v2.Country
import com.bapis.example.app.interfaces.v2.Province
import com.bapis.example.app.interfaces.v2.Town
import com.example.lib.moss.util.rest.internal.MapFieldInfo
import com.example.lib.moss.util.rest.internal.RepeatedFieldInfo
import com.example.lib.moss.util.rest.internal.getField
import com.example.lib.moss.util.test.common.initDelegate
import org.junit.Before
import org.junit.Test

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/18
 */

class FieldFinderTest {


    @Before
    fun setUp() {
        initDelegate()
    }

    private fun buildModel(): Country {
        val builder = Country.newBuilder()
        return builder.build()
    }

    @Test
    fun fieldFinderTest() {
        val message = buildModel()

        val province = getField("province", message)
        assert(province!!.clazz == Province::class.java)

        val province_city = getField("province.city", message)
        assert(province_city!!.clazz == City::class.java)

        // 测试 list.
        val province_city_bridges = getField("province.city.bridges", message) as RepeatedFieldInfo
        assert(province_city_bridges.clazz == List::class.java)
        assert(province_city_bridges.itemClazz == String::class.java)

        // 测试 map.
        val province_city_townMap = getField("province.city.town_map", message) as MapFieldInfo
        assert(province_city_townMap.keyClazz == String::class.java)
        assert(province_city_townMap.valueClazz == Town::class.java)

        // 测试 不存在的field.
        val province_city_notExist = getField("province.city.not_exist", message)
        assert(province_city_notExist == null)

        // 测试 4层field.
        val province_city_town_streets = getField("province.city.town.streets", message) as RepeatedFieldInfo
        assert(province_city_town_streets.clazz == List::class.java)
        assert(province_city_town_streets.itemClazz == String::class.java)

        // 测试 list primitive boxing.
        val province_city_rivers = getField("province.city.rivers", message) as RepeatedFieldInfo
        assert(province_city_rivers.itemClazz == Long::class.java)

        // 测试 map primitive boxing
        val province_city_riverMap = getField("province.city.river_map", message) as MapFieldInfo
        assert(province_city_riverMap.keyClazz == Int::class.java)
        assert(province_city_riverMap.valueClazz == Long::class.java)

        val province_city_bridgeMap = getField("province.city.bridge_map", message) as MapFieldInfo
        assert(province_city_bridgeMap.keyClazz == Boolean::class.java)
        assert(province_city_bridgeMap.valueClazz == String::class.java)
    }
}