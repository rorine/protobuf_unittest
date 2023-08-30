package com.example.lib.moss.util.test.common

import com.bapis.example.app.interfaces.v1.Clock
import com.bapis.example.app.interfaces.v1.Food
import com.bapis.example.app.interfaces.v1.Fruit
import com.bapis.example.app.interfaces.v1.FullReq
import com.bapis.example.app.interfaces.v1.LockdownType
import com.bapis.example.app.interfaces.v1.RealFood
import com.bapis.example.app.interfaces.v1.RoomJoinEvent
import com.bapis.example.app.interfaces.v1.RoomLeaveEvent
import com.bapis.example.app.interfaces.v1.Tank
import com.bapis.example.app.interfaces.v1.Time
import com.bapis.example.app.interfaces.v1.Water
import com.bapis.example.app.interfaces.v1.Weather
import com.example.lib.moss.util.any.pack
import com.google.protobuf.ByteString
import java.nio.charset.Charset

/**
 * 描述：值用于ut.
 *
 * Created by zhangyuling on 2023/6/17
 */

internal fun buildFullReq(): FullReq {
    val root = FullReq.newBuilder()
    root.building = 2
    root.room = 402
    root.isLockdown = false
    root.name = "hello json !.~*-"

    val wb = Water.newBuilder()
    wb.count = 100
    wb.tank = Tank.newBuilder().setVolumn(5).build()
    val curretWater = wb.build()
    root.currentWater = curretWater

    val bwb = Water.newBuilder()
    val bw1 = bwb.setCount(100000).setTank(Tank.newBuilder().setVolumn(50).build()).build()
    val bw2 = bwb.setCount(200000).setTank(Tank.newBuilder().setVolumn(50).build()).build()
    root.addAllBackupWater(listOf(bw1, bw2))

    val fb = Food.newBuilder()
    fb.count = 9999999
    val rfb = RealFood.newBuilder()
    fb.putFoodMap("rice", rfb.setMeals(111111).build())
    fb.putFoodMap("noddle", rfb.setMeals(222222).build())
    root.food = fb.build()

    val fruit = Fruit.newBuilder()
    root.putFruits(603, fruit.setName("apple").setWeight(603603).build())
    root.putFruits(402, fruit.setName("watermelon").setWeight(402402).build())

    root.lockdownType = LockdownType.LockdownTypePrecaution
    val ss = LockdownType.LockdownTypePrecaution.name

    val join = RoomJoinEvent.getDefaultInstance()
    root.join = join

    val leave = RoomLeaveEvent.getDefaultInstance()
    root.leave = leave

    val event = root.eventCase

    val clock = Clock.newBuilder().setHour(20).setMiniute(57).setSecond(30).build()
    val time = Time.newBuilder().setDate("20220529").setClock(clock).build()
    root.putExtra("time", pack(time))
    val weather = Weather.newBuilder().setTemperature(21).build()
    root.putExtra("weather", pack(weather))

    root.fooInt = 22
    root.barInt = 33

    root.floatValue = 3121412.1231231F
    root.doubleValue = 3121412.1231231

    root.bytesValue = ByteString.copyFrom("hello world", Charset.forName("UTF-8"))

    root.addWeekLockdownTypeList(LockdownType.LockdownTypeLockdown)
    root.addWeekLockdownTypeList(LockdownType.LockdownTypeLockdown)
    root.addWeekLockdownTypeList(LockdownType.LockdownTypeControl)
    root.addWeekLockdownTypeList(LockdownType.LockdownTypeControl)
    root.addWeekLockdownTypeList(LockdownType.LockdownTypePrecaution)

    root.addWeekExtraList(pack(Time.newBuilder().setDate("20220529").build()))
    root.addWeekExtraList(pack(Time.newBuilder().setDate("20220530").build()))
    root.addWeekExtraList(pack(Time.newBuilder().setDate("20220531").build()))
    root.addWeekExtraList(pack(Time.newBuilder().setDate("20220601").build()))
    root.addWeekExtraList(pack(Time.newBuilder().setDate("20220602").build()))

    root.putWeekLockdownTypeMap(529, LockdownType.LockdownTypeLockdown)
    root.putWeekLockdownTypeMap(530, LockdownType.LockdownTypeLockdown)
    root.putWeekLockdownTypeMap(531, LockdownType.LockdownTypeControl)
    root.putWeekLockdownTypeMap(601, LockdownType.LockdownTypeControl)
    root.putWeekLockdownTypeMap(602, LockdownType.LockdownTypePrecaution)

    return root.build()
}