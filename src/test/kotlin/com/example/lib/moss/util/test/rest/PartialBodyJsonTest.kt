package com.example.lib.moss.util.test.rest

import com.example.lib.moss.util.rest.getPartialBodyJson
import com.example.lib.moss.util.test.common.buildFullReq
import com.example.lib.moss.util.test.common.initDelegate
import org.junit.Before
import org.junit.Test

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/18
 */

class PartialBodyJsonTest {

    @Before
    fun setUp() {
        initDelegate()
    }

    @Test
    fun fullTest() {
        val message = buildFullReq()

        // Int.
        val buiding = getPartialBodyJson("building", message)
        assert(buiding == "{\"building\":2}")

        // Long.
        val room = getPartialBodyJson("room", message)
        // assert(room == "{\"room\":\"402\"}")
        assert(room == "{\"room\":402}")

        // Boolean.
        val is_lockdown = getPartialBodyJson("is_lockdown", message)
        assert(is_lockdown == "{\"is_lockdown\":false}")

        // String.
        val name = getPartialBodyJson("name", message)
        assert(name == "{\"name\":\"hello json !.~*-\"}")

        // Proto list.
        val backup_water = getPartialBodyJson("backup_water", message)
        assert(backup_water == "{\"backup_water\":[{\"count\":100000,\"tank\":{\"volumn\":50}},{\"count\":200000,\"tank\":{\"volumn\":50}}]}")

        // Proto message.
        val food = getPartialBodyJson("food", message)
        // assert(food == "{\"count\":9999999,\"foodMap\":{\"rice\":{\"meals\":\"111111\"},\"noddle\":{\"meals\":\"222222\"}}}")
        // assert(food == "{\"count\":9999999,\"food_map\":{\"rice\":{\"meals\":\"111111\"},\"noddle\":{\"meals\":\"222222\"}}}")
        assert(food == "{\"count\":9999999,\"food_map\":{\"rice\":{\"meals\":111111},\"noddle\":{\"meals\":222222}}}")

        // ByteString.
        val bytes_value = getPartialBodyJson("bytes_value", message)
        assert(bytes_value == "{\"bytes_value\":\"aGVsbG8gd29ybGQ=\"}")

        // Proto enum.
        val lockdown_type = getPartialBodyJson("lockdown_type", message)
        // assert(lockdown_type == "{\"lockdown_type\":\"LockdownTypePrecaution\"}")
        assert(lockdown_type == "{\"lockdown_type\":2}")

        // Map.
        val extra = getPartialBodyJson("extra", message)
        assert(extra == "{\"extra\":{\"time\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\",\"clock\":{\"hour\":20,\"miniute\":57,\"second\":30}},\"weather\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Weather\",\"temperature\":21}}}")

        // List.
        val week_lockdown_type_list = getPartialBodyJson("week_lockdown_type_list", message)
        // assert(week_lockdown_type_list == "{\"week_lockdown_type_list\":[\"LockdownTypeLockdown\",\"LockdownTypeLockdown\",\"LockdownTypeControl\",\"LockdownTypeControl\",\"LockdownTypePrecaution\"]}")
        assert(week_lockdown_type_list == "{\"week_lockdown_type_list\":[0,0,1,1,2]}")

        // Empty map.
        val another_map = getPartialBodyJson("another_map", message)
        assert(another_map == "{\"another_map\":{}}")

        // Empty list.
        val another_list = getPartialBodyJson("another_list", message)
        assert(another_list == "{\"another_list\":[]}")

        // Oneof, 从接口看来就是 proto message.
        val join = getPartialBodyJson("join", message)
        assert(join == "{}")

        val null_food = getPartialBodyJson("null_food", message)
        assert(null_food == "{}")
    }
}