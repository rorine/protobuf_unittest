package com.example.lib.moss.util.test.rest

import com.example.lib.moss.util.rest.getPathString
import com.example.lib.moss.util.test.common.buildFullReq
import com.example.lib.moss.util.test.common.initDelegate
import org.junit.Before
import org.junit.Test

class PathTest {
    @Before
    fun setUp() {
        initDelegate()
    }

    @Test
    fun fullTest() {
        val message = buildFullReq()

        val buiding = getPathString("building", message)
        assert(buiding == "2")

        val room = getPathString("room", message)
        assert(room == "402")

        val isLockdown = getPathString("is_lockdown", message)
        assert(isLockdown == "false")

        val name = getPathString("name", message)
        assert(name == "hello json !.~*-")

        val currentWater = getPathString("current_water", message)
        assert(currentWater == null)
        val currentWater_count = getPathString("current_water.count", message)
        assert(currentWater_count == "100")
        val currentWater_tank = getPathString("current_water.tank", message)
        assert(currentWater_tank == null)
        val currentWater_tank_volumn = getPathString("current_water.tank.volumn", message)
        assert(currentWater_tank_volumn == "5")

        val backupWater = getPathString("backup_water", message)
        assert(backupWater == null)

        val food = getPathString("food", message)
        assert(food == null)
        val food_count = getPathString("food.count", message)
        assert(food_count == "9999999")
        val food_foodMap = getPathString("food.food_map", message)
        assert(food_foodMap == null)

        val fruits = getPathString("fruits", message)
        assert(fruits == null)

        val lockdown_type = getPathString("lockdown_type", message)
        // assert(lockdown_type == "LockdownTypePrecaution")
        assert(lockdown_type == "2")

        val _name_with_prefix = getPathString("_name_with_prefix", message)
        assert(_name_with_prefix == "0")

        val bytes_value = getPathString("bytes_value", message)
        assert(bytes_value == null)
    }
}