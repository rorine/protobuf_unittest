package com.example.lib.moss.util.test.json

import com.bapis.example.app.interfaces.v1.Food
import com.bapis.example.app.interfaces.v1.RealFood
import com.example.lib.moss.util.common.ProtoKeyStyle
import com.example.lib.moss.util.json.JsonFormat
import com.example.lib.moss.util.test.common.buildFullReq
import com.example.lib.moss.util.test.common.buildSimpleMessage
import com.example.lib.moss.util.test.common.initDelegate
import org.junit.Before
import org.junit.Test

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/18
 */
class JsonFormatTest {

    @Before
    fun setUp() {
        initDelegate()
    }

    @Test
    fun fieldTest() {
        val fb = Food.newBuilder()
        fb.count = 9999999
        val rfb = RealFood.newBuilder()
        fb.putFoodMap("rice", rfb.setMeals(111111).build())
        fb.putFoodMap("noddle", rfb.setMeals(222222).build())

        val food = fb.build()

        val foodJson = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_SNAKE_CASE).print(food)
        println(foodJson)
    }

    @Test
    fun fullTest() {
        val message = buildFullReq()

        val json3 = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(message)
        println(json3)

        // assert(json3 == "{\"leave\":{},\"barInt\":\"33\",\"building\":2,\"room\":\"402\",\"name\":\"hello json !.~*-\",\"currentWater\":{\"count\":100,\"tank\":{\"volumn\":5}},\"backupWater\":[{\"count\":100000,\"tank\":{\"volumn\":50}},{\"count\":200000,\"tank\":{\"volumn\":50}}],\"food\":{\"count\":9999999,\"foodMap\":{\"rice\":{\"meals\":\"111111\"},\"noddle\":{\"meals\":\"222222\"}}},\"fruits\":{\"603\":{\"name\":\"apple\",\"weight\":\"603603\"},\"402\":{\"name\":\"watermelon\",\"weight\":\"402402\"}},\"lockdownType\":\"LockdownTypePrecaution\",\"extra\":{\"time\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\",\"clock\":{\"hour\":20,\"miniute\":57,\"second\":30}},\"weather\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Weather\",\"temperature\":21}},\"floatValue\":3121412.0,\"doubleValue\":3121412.1231231,\"bytesValue\":\"aGVsbG8gd29ybGQ=\",\"weekLockdownTypeList\":[\"LockdownTypeLockdown\",\"LockdownTypeLockdown\",\"LockdownTypeControl\",\"LockdownTypeControl\",\"LockdownTypePrecaution\"],\"weekExtraList\":[{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220530\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220531\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220601\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220602\"}],\"weekLockdownTypeMap\":{\"529\":\"LockdownTypeLockdown\",\"530\":\"LockdownTypeLockdown\",\"531\":\"LockdownTypeControl\",\"601\":\"LockdownTypeControl\",\"602\":\"LockdownTypePrecaution\"}}")
        // val expect = "{\"leave\":{},\"barInt\":33,\"building\":2,\"room\":402,\"name\":\"hello json !.~*-\",\"currentWater\":{\"count\":100,\"tank\":{\"volumn\":5}},\"backupWater\":[{\"count\":100000,\"tank\":{\"volumn\":50}},{\"count\":200000,\"tank\":{\"volumn\":50}}],\"food\":{\"count\":9999999,\"foodMap\":{\"rice\":{\"meals\":111111},\"noddle\":{\"meals\":222222}}},\"fruits\":{\"603\":{\"name\":\"apple\",\"weight\":603603},\"402\":{\"name\":\"watermelon\",\"weight\":402402}},\"lockdownType\":\"LockdownTypePrecaution\",\"extra\":{\"time\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\",\"clock\":{\"hour\":20,\"miniute\":57,\"second\":30}},\"weather\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Weather\",\"temperature\":21}},\"floatValue\":3121412.0,\"doubleValue\":3121412.1231231,\"bytesValue\":\"aGVsbG8gd29ybGQ=\",\"weekLockdownTypeList\":[\"LockdownTypeLockdown\",\"LockdownTypeLockdown\",\"LockdownTypeControl\",\"LockdownTypeControl\",\"LockdownTypePrecaution\"],\"weekExtraList\":[{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220530\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220531\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220601\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220602\"}],\"weekLockdownTypeMap\":{\"529\":\"LockdownTypeLockdown\",\"530\":\"LockdownTypeLockdown\",\"531\":\"LockdownTypeControl\",\"601\":\"LockdownTypeControl\",\"602\":\"LockdownTypePrecaution\"}}"

        // 根据协议, 枚举转int.
        val expect = "{\"leave\":{}," +
                "\"barInt\":33," +
                "\"building\":2," +
                "\"room\":402," +
                "\"name\":\"hello json !.~*-\"," +
                "\"currentWater\":{\"count\":100,\"tank\":{\"volumn\":5}}," +
                "\"backupWater\":[{\"count\":100000,\"tank\":{\"volumn\":50}},{\"count\":200000,\"tank\":{\"volumn\":50}}]," +
                "\"food\":{\"count\":9999999,\"foodMap\":{\"rice\":{\"meals\":111111},\"noddle\":{\"meals\":222222}}}," +
                "\"fruits\":{\"603\":{\"name\":\"apple\",\"weight\":603603},\"402\":{\"name\":\"watermelon\",\"weight\":402402}}," +
                "\"lockdownType\":2," +
                "\"extra\":{\"time\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\",\"clock\":{\"hour\":20,\"miniute\":57,\"second\":30}},\"weather\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Weather\",\"temperature\":21}}," +
                "\"floatValue\":3121412.0," +
                "\"doubleValue\":3121412.1231231," +
                "\"bytesValue\":\"aGVsbG8gd29ybGQ=\"," +
                "\"weekLockdownTypeList\":[0,0,1,1,2]," +
                "\"weekExtraList\":[{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220530\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220531\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220601\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220602\"}]," +
                "\"weekLockdownTypeMap\":{\"529\":0,\"530\":0,\"531\":1,\"601\":1,\"602\":2}}"

        assert(json3 == expect)
    }

    /*
     *  64位统一处理为数值而不是字符串.
     */
    @Test
    fun fullSimpleMessage(){
        val message = buildSimpleMessage()
        val json = JsonFormat.printer().print(message)
        println(json)
        assert(json == "{\"id\":100,\"num\":1000,\"lang\":\"zh\",\"embedded\":{\"bool_val\":true,\"int32_val\":1111,\"repeated_string_val\":[\"aaa\",\"bbb\"],\"map_string_val\":{\"key\":\"value\"},\"map_error_val\":{\"key\":{\"code\":10000}}}}");
    }
}