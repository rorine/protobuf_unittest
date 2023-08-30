package com.example.lib.moss.util.test.json

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
import com.bapis.example.app.resource.v1.DownloadReply
import com.bapis.example.dagw.component.avatar.v1.plugin.GyroConfig
import com.example.lib.moss.util.any.pack
import com.example.lib.moss.util.common.ProtoKeyStyle
import com.example.lib.moss.util.exception.ProtoUtilException
import com.example.lib.moss.util.json.JsonFormat
import com.example.lib.moss.util.test.common.buildFullReq
import com.example.lib.moss.util.test.common.initDelegate
import com.google.protobuf.ByteString
import org.junit.Before
import org.junit.Test
import java.nio.charset.Charset

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/27
 */

class JsonParserTest {
    @Before
    fun setUp() {
        initDelegate()
    }

    // 单个field为int32.
    @Test
    fun testSingle() {
        val root = FullReq.newBuilder()
        root.building = 2

        val rawReq = root.build()
        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{\"building\":2}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 测试json中 int如果使用string表示，可以正常解析.
    @Test
    fun testSingleNew1() {
        val root = FullReq.newBuilder()
        root.building = 2
        val rawReq = root.build()

        val json = "{\"building\":\"2\"}"
        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)
        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    /*
     * 测试json解析异常.
     */
    @Test
    fun testSingleNew2() {
        val root = FullReq.newBuilder()
        root.building = 2
        val rawReq = root.build()

        val json = "{building}"
        val builder = FullReq.newBuilder()
        try {
            JsonFormat.parser().merge(json, builder)
        } catch (e: Throwable) {
            assert(e is ProtoUtilException)
        }
        val fullReq = builder.build()
        checkNotEqual(rawReq, fullReq)
    }

    // 单个field为int64.
    @Test
    fun testSingle2() {
        val root = FullReq.newBuilder()
        root.room = 402

        val rawReq = root.build()
        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        // assert(json == "{\"room\":\"402\"}")
        assert(json == "{\"room\":402}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 测试json中 long如果使用string表示，可以正常解析.
    @Test
    fun testSingle2New1() {
        val root = FullReq.newBuilder()
        root.room = 402
        val rawReq = root.build()

        val json = "{\"room\":\"402\"}"
        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 单个field为bool.
    @Test
    fun testSingle3() {
        val root = FullReq.newBuilder()
        root.isLockdown = true

        val rawReq = root.build()
        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{\"isLockdown\":true}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 单个field为bool, 默认值.
    @Test
    fun testSingle4() {
        val root = FullReq.newBuilder()
        root.isLockdown = false

        val rawReq = root.build()
        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 单个field为string.
    @Test
    fun testSingle5() {
        val root = FullReq.newBuilder()
        root.name = "hello json !.~*-"

        val rawReq = root.build()
        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{\"name\":\"hello json !.~*-\"}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 单个field为proto message.
    @Test
    fun testSingle6() {
        val root = FullReq.newBuilder()
        val wb = Water.newBuilder()
        wb.count = 100
        wb.tank = Tank.newBuilder().setVolumn(5).build()
        val curretWater = wb.build()
        root.currentWater = curretWater

        val rawReq = root.build()
        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{\"currentWater\":{\"count\":100,\"tank\":{\"volumn\":5}}}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 单个field为enum.
    @Test
    fun testSingle7() {
        val root = FullReq.newBuilder()
        root.lockdownType = LockdownType.LockdownTypePrecaution
        val rawReq = root.build()
        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        // assert(json == "{\"lockdownType\":\"LockdownTypePrecaution\"}")
        assert(json == "{\"lockdownType\":2}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 单个field为oneof proto message.
    @Test
    fun testSingle8() {
        val root = FullReq.newBuilder()

        val join = RoomJoinEvent.getDefaultInstance()
        root.join = join
        val leave = RoomLeaveEvent.getDefaultInstance()
        root.leave = leave

        val rawReq = root.build()
        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{\"leave\":{}}")

        // 直接打印内容为 WgA=，解码为 field 11 内容为""
        // 明确说明这个field被设置了, 只不过内容是空的.

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }


    // 单个field为oneof scalar.
    @Test
    fun testSingle9() {
        val root = FullReq.newBuilder()

        root.fooInt = 22
        root.barInt = 33

        var rawReq = root.build()
        var json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)

        // assert(json == "{\"barInt\":\"33\"}")
        assert(json == "{\"barInt\":33}")

        var builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        var fullReq = builder.build()
        checkEqual(rawReq, fullReq)


        root.barInt = 0L;
        rawReq = root.build()
        json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        // assert(json == "{\"barInt\":\"0\"}")
        assert(json == "{\"barInt\":0}")

        builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 单个field为float double.
    @Test
    fun testSingle10() {
        val root = FullReq.newBuilder()
        root.floatValue = 3121412.1231231F;
        root.doubleValue = 3121412.1231231;

        var rawReq = root.build()
        var json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)

        assert(json == "{\"floatValue\":3121412.0,\"doubleValue\":3121412.1231231}")
        var builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        var fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 单个field为bytes.
    @Test
    fun testSingle11() {
        val root = FullReq.newBuilder()
        root.bytesValue = ByteString.copyFrom("hello world", Charset.forName("UTF-8"))
        var rawReq = root.build()

        var json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{\"bytesValue\":\"aGVsbG8gd29ybGQ=\"}")

        var builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        var fullReq = builder.build()
        checkEqual(rawReq, fullReq)

        json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_SNAKE_CASE).print(rawReq)
        assert(json == "{\"bytes_value\":\"aGVsbG8gd29ybGQ=\"}")

        builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_SNAKE_CASE).merge(json, builder)

        fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // Item类型为int32.
    @Test
    fun testRepeated() {
        val root = FullReq.newBuilder()
        root.addAnotherList(22)
        root.addAnotherList(33)
        var rawReq = root.build()

        var json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{\"anotherList\":[22,33]}")

        var builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        var fullReq = builder.build()
        checkEqual(rawReq, fullReq)

        // 换一个 key style.
        json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_SNAKE_CASE).print(rawReq)
        assert(json == "{\"another_list\":[22,33]}")

        builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_SNAKE_CASE).merge(json, builder)

        fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // Item类型为proto message.
    @Test
    fun testRepeated2() {
        val root = FullReq.newBuilder()
        val bwb = Water.newBuilder()
        val bw1 = bwb.setCount(100000).setTank(Tank.newBuilder().setVolumn(50).build()).build()
        val bw2 = bwb.setCount(200000).setTank(Tank.newBuilder().setVolumn(50).build()).build()
        root.addAllBackupWater(listOf(bw1, bw2))
        val rawReq = root.build()

        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{\"backupWater\":[{\"count\":100000,\"tank\":{\"volumn\":50}},{\"count\":200000,\"tank\":{\"volumn\":50}}]}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // Item类型为enum.
    @Test
    fun testRepeated3() {
        val root = FullReq.newBuilder()
        root.addWeekLockdownTypeList(LockdownType.LockdownTypeLockdown)
        root.addWeekLockdownTypeList(LockdownType.LockdownTypeLockdown)
        root.addWeekLockdownTypeList(LockdownType.LockdownTypeControl)
        root.addWeekLockdownTypeList(LockdownType.LockdownTypeControl)
        root.addWeekLockdownTypeList(LockdownType.LockdownTypePrecaution)

        var rawReq = root.build()

        var json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        // assert(json == "{\"weekLockdownTypeList\":[\"LockdownTypeLockdown\",\"LockdownTypeLockdown\",\"LockdownTypeControl\",\"LockdownTypeControl\",\"LockdownTypePrecaution\"]}")
        assert(json == "{\"weekLockdownTypeList\":[0,0,1,1,2]}")

        var builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        var fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }


    // Item类型为any.
    @Test
    fun testRepeated4() {
        val root = FullReq.newBuilder()
        root.addWeekExtraList(pack(Time.newBuilder().setDate("20220529").build()))
        root.addWeekExtraList(pack(Time.newBuilder().setDate("20220530").build()))
        root.addWeekExtraList(pack(Time.newBuilder().setDate("20220531").build()))
        root.addWeekExtraList(pack(Time.newBuilder().setDate("20220601").build()))
        root.addWeekExtraList(pack(Time.newBuilder().setDate("20220602").build()))
        val rawReq = root.build()

        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{\"weekExtraList\":[{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220530\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220531\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220601\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220602\"}]}");

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 测试 map<string, int32>.
    @Test
    fun testMap() {
        val root = FullReq.newBuilder()
        root.putAnotherMap("22", 22)
        root.putAnotherMap("33", 33)

        val rawReq = root.build()

        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        assert(json == "{\"anotherMap\":{\"22\":22,\"33\":33}}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 测试 map<int32, Fruit>.
    @Test
    fun testMap2() {
        val root = FullReq.newBuilder()
        val fruit = Fruit.newBuilder()
        root.putFruits(603, fruit.setName("apple").setWeight(603603).build())
        root.putFruits(402, fruit.setName("watermelon").setWeight(402402).build())

        val rawReq = root.build()

        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        // assert(json == "{\"fruits\":{\"603\":{\"name\":\"apple\",\"weight\":\"603603\"},\"402\":{\"name\":\"watermelon\",\"weight\":\"402402\"}}}")
        assert(json == "{\"fruits\":{\"603\":{\"name\":\"apple\",\"weight\":603603},\"402\":{\"name\":\"watermelon\",\"weight\":402402}}}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }


    // 测试 map<int32, LockdownType>.
    @Test
    fun testMap3() {
        val root = FullReq.newBuilder()

        root.putWeekLockdownTypeMap(529, LockdownType.LockdownTypeLockdown)
        root.putWeekLockdownTypeMap(530, LockdownType.LockdownTypeLockdown)
        root.putWeekLockdownTypeMap(531, LockdownType.LockdownTypeControl)
        root.putWeekLockdownTypeMap(601, LockdownType.LockdownTypeControl)
        root.putWeekLockdownTypeMap(602, LockdownType.LockdownTypePrecaution)

        val rawReq = root.build()

        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        // assert(json == "{\"weekLockdownTypeMap\":{\"529\":\"LockdownTypeLockdown\",\"530\":\"LockdownTypeLockdown\",\"531\":\"LockdownTypeControl\",\"601\":\"LockdownTypeControl\",\"602\":\"LockdownTypePrecaution\"}}")
        assert(json == "{\"weekLockdownTypeMap\":{\"529\":0,\"530\":0,\"531\":1,\"601\":1,\"602\":2}}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 测试 map<string, google.protobuf.Any>.
    @Test
    fun testMap4() {
        val root = FullReq.newBuilder()

        val clock = Clock.newBuilder().setHour(20).setMiniute(57).setSecond(30).build()
        val time = Time.newBuilder().setDate("20220529").setClock(clock).build()
        root.putExtra("time", pack(time))
        val weather = Weather.newBuilder().setTemperature(21).build()
        root.putExtra("weather", pack(weather))

        val rawReq = root.build()

        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        // 和objc 生成字段顺序不同.
        // XCTAssertEqualObjects(json, @"{\"extra\":{\"weather\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Weather\",\"temperature\":21},\"time\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\",\"clock\":{\"miniute\":57,\"second\":30,\"hour\":20}}}}");
        assert(json == "{\"extra\":{\"time\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\",\"clock\":{\"hour\":20,\"miniute\":57,\"second\":30}},\"weather\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Weather\",\"temperature\":21}}}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    // 单个field中有map.
    @Test
    fun testSingle12() {
        val root = FullReq.newBuilder()

        val fb = Food.newBuilder()
        fb.count = 9999999
        val rfb = RealFood.newBuilder()
        fb.putFoodMap("rice", rfb.setMeals(111111).build())
        fb.putFoodMap("noddle", rfb.setMeals(222222).build())
        root.food = fb.build()

        val rawReq = root.build()

        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        // 和objc 生成字段顺序不同.
        // XCTAssertEqualObjects(json, @"{\"food\":{\"count\":9999999,\"foodMap\":{\"noddle\":{\"meals\":\"222222\"},\"rice\":{\"meals\":\"111111\"}}}}");
        // assert(json == "{\"food\":{\"count\":9999999,\"foodMap\":{\"rice\":{\"meals\":\"111111\"},\"noddle\":{\"meals\":\"222222\"}}}}")
        assert(json == "{\"food\":{\"count\":9999999,\"foodMap\":{\"rice\":{\"meals\":111111},\"noddle\":{\"meals\":222222}}}}")

        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    @Test
    fun fullTest() {
        val rawReq = buildFullReq()

        val json = JsonFormat.printer().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).print(rawReq)
        println(json)
        // assert(json == "{\"leave\":{},\"barInt\":\"33\",\"building\":2,\"room\":\"402\",\"name\":\"hello json !.~*-\",\"currentWater\":{\"count\":100,\"tank\":{\"volumn\":5}},\"backupWater\":[{\"count\":100000,\"tank\":{\"volumn\":50}},{\"count\":200000,\"tank\":{\"volumn\":50}}],\"food\":{\"count\":9999999,\"foodMap\":{\"rice\":{\"meals\":\"111111\"},\"noddle\":{\"meals\":\"222222\"}}},\"fruits\":{\"603\":{\"name\":\"apple\",\"weight\":\"603603\"},\"402\":{\"name\":\"watermelon\",\"weight\":\"402402\"}},\"lockdownType\":\"LockdownTypePrecaution\",\"extra\":{\"time\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\",\"clock\":{\"hour\":20,\"miniute\":57,\"second\":30}},\"weather\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Weather\",\"temperature\":21}},\"floatValue\":3121412.0,\"doubleValue\":3121412.1231231,\"bytesValue\":\"aGVsbG8gd29ybGQ=\",\"weekLockdownTypeList\":[\"LockdownTypeLockdown\",\"LockdownTypeLockdown\",\"LockdownTypeControl\",\"LockdownTypeControl\",\"LockdownTypePrecaution\"],\"weekExtraList\":[{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220530\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220531\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220601\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220602\"}],\"weekLockdownTypeMap\":{\"529\":\"LockdownTypeLockdown\",\"530\":\"LockdownTypeLockdown\",\"531\":\"LockdownTypeControl\",\"601\":\"LockdownTypeControl\",\"602\":\"LockdownTypePrecaution\"}}")

//        assert(json == "{\"leave\":{}," +
//                "\"barInt\":33," +
//                "\"building\":2," +
//                "\"room\":402," +
//                "\"name\":\"hello json !.~*-\"," +
//                "\"currentWater\":{\"count\":100,\"tank\":{\"volumn\":5}}," +
//                "\"backupWater\":[{\"count\":100000,\"tank\":{\"volumn\":50}},{\"count\":200000,\"tank\":{\"volumn\":50}}]," +
//                "\"food\":{\"count\":9999999,\"foodMap\":{\"rice\":{\"meals\":111111},\"noddle\":{\"meals\":222222}}}," +
//                "\"fruits\":{\"603\":{\"name\":\"apple\",\"weight\":603603},\"402\":{\"name\":\"watermelon\",\"weight\":402402}}," +
//                "\"lockdownType\":\"LockdownTypePrecaution\"," +
//                "\"extra\":{\"time\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\",\"clock\":{\"hour\":20,\"miniute\":57,\"second\":30}},\"weather\":{\"@type\":\"type.googleapis.com/example.app.interface.v1.Weather\",\"temperature\":21}}," +
//                "\"floatValue\":3121412.0," +
//                "\"doubleValue\":3121412.1231231," +
//                "\"bytesValue\":\"aGVsbG8gd29ybGQ=\"," +
//                "\"weekLockdownTypeList\":[\"LockdownTypeLockdown\",\"LockdownTypeLockdown\",\"LockdownTypeControl\",\"LockdownTypeControl\",\"LockdownTypePrecaution\"]," +
//                "\"weekExtraList\":[{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220529\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220530\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220531\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220601\"},{\"@type\":\"type.googleapis.com/example.app.interface.v1.Time\",\"date\":\"20220602\"}]," +
//                "\"weekLockdownTypeMap\":{\"529\":\"LockdownTypeLockdown\",\"530\":\"LockdownTypeLockdown\",\"531\":\"LockdownTypeControl\",\"601\":\"LockdownTypeControl\",\"602\":\"LockdownTypePrecaution\"}}")

        assert(json == "{\"leave\":{}," +
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
                "\"weekLockdownTypeMap\":{\"529\":0,\"530\":0,\"531\":1,\"601\":1,\"602\":2}}")
        val builder = FullReq.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_CAMEL_CASE).merge(json, builder)

        val fullReq = builder.build()
        checkEqual(rawReq, fullReq)
    }

    @Test
    fun peakDownloadTest() {
        val json = "{\"ver\":\"5880552901825103232\",\"resource\":[{\"type\":\"egg\",\"list\":[{\"task_id\":\"8b393029d91aaff1c7e2ba53b5c92c7e\",\"file_name\":\"dfaa734ca4ddeccbeb681b25f04fd073229d967f.mp4\",\"type\":\"mov\",\"url\":\"https://i0.hdslb.com/bfs/feed-admin/dfaa734ca4ddeccbeb681b25f04fd073229d967f.mp4\",\"hash\":\"8b393029d91aaff1c7e2ba53b5c92c7e\",\"size\":830828,\"expect_dw\":0,\"effect_time\":1656648000,\"expire_time\":1661961599,\"priority\":0},{\"task_id\":\"c823e352aa5c00a87677e4b4d394ce40\",\"file_name\":\"8c3fd143b3212b858e8a3af884f6b8669581a311.mp4\",\"type\":\"mov\",\"url\":\"https://i0.hdslb.com/bfs/feed-admin/8c3fd143b3212b858e8a3af884f6b8669581a311.mp4\",\"hash\":\"c823e352aa5c00a87677e4b4d394ce40\",\"size\":3649215,\"expect_dw\":0,\"effect_time\":1661014800,\"expire_time\":1661961540,\"priority\":0},{\"task_id\":\"381b98525f1d08d84cc571bea25439e3\",\"file_name\":\"ec3d47b05e260c7079efa2c6135ae4d003336774.mp4\",\"type\":\"mov\",\"url\":\"https://i0.hdslb.com/bfs/feed-admin/ec3d47b05e260c7079efa2c6135ae4d003336774.mp4\",\"hash\":\"381b98525f1d08d84cc571bea25439e3\",\"size\":6323952,\"expect_dw\":0,\"effect_time\":1661788800,\"expire_time\":1662134399,\"priority\":0},{\"task_id\":\"fb84eb82510c3a783bff92f07d7219a0\",\"file_name\":\"18acfc4d93ec56351171f298526a4d993f167f13.mp4\",\"type\":\"mov\",\"url\":\"https://i0.hdslb.com/bfs/feed-admin/18acfc4d93ec56351171f298526a4d993f167f13.mp4\",\"hash\":\"fb84eb82510c3a783bff92f07d7219a0\",\"size\":4840136,\"expect_dw\":0,\"effect_time\":1661832000,\"expire_time\":1662566349,\"priority\":0}],\"extra_value\":\"\"}],\"dwtime\":{\"i0.hdslb.com\":{\"type\":1,\"peak\":[{\"start\":1661655601,\"end\":1661666400},{\"start\":1661677201,\"end\":1661702400},{\"start\":1661616000,\"end\":1661619600},{\"start\":1661742001,\"end\":1661752800},{\"start\":1661763601,\"end\":1661788800},{\"start\":1661702400,\"end\":1661704200}],\"low\":[{\"start\":1661623200,\"end\":1661655600},{\"start\":1661666401,\"end\":1661677200},{\"start\":1661706000,\"end\":1661742000},{\"start\":1661752801,\"end\":1661763600}]},\"i1.hdslb.com\":{\"type\":1,\"peak\":[{\"start\":1661655601,\"end\":1661666400},{\"start\":1661677201,\"end\":1661702400},{\"start\":1661616000,\"end\":1661619600},{\"start\":1661742001,\"end\":1661752800},{\"start\":1661763601,\"end\":1661788800},{\"start\":1661702400,\"end\":1661704200}],\"low\":[{\"start\":1661623200,\"end\":1661655600},{\"start\":1661666401,\"end\":1661677200},{\"start\":1661706000,\"end\":1661742000},{\"start\":1661752801,\"end\":1661763600}]},\"i2.hdslb.com\":{\"type\":1,\"peak\":[{\"start\":1661655601,\"end\":1661666400},{\"start\":1661677201,\"end\":1661702400},{\"start\":1661616000,\"end\":1661619600},{\"start\":1661742001,\"end\":1661752800},{\"start\":1661763601,\"end\":1661788800},{\"start\":1661702400,\"end\":1661704200}],\"low\":[{\"start\":1661623200,\"end\":1661655600},{\"start\":1661666401,\"end\":1661677200},{\"start\":1661706000,\"end\":1661742000},{\"start\":1661752801,\"end\":1661763600}]}}}"

        val builder = DownloadReply.newBuilder()
        JsonFormat.parser().protoKeyStyle(ProtoKeyStyle.LOWER_SNAKE_CASE).merge(json, builder)

        val download = builder.build()
        assert(download.ver == "5880552901825103232")
    }

    @Test
    fun testGyro() {
        val json =
            "{\"gyroscope\":{\"gyroscope\":[{\"display_type\":\"physical_orientation\",\"contents\":[{\"file_url\":\"http://i0.hdslb.com/bfs/baselabs/aedde29464555d0bb30505faa2e88a94ddf08a4e.png\",\"scale\":1,\"physical_orientation\":[{\"type\":\"gamma\",\"angle\":[-45,0],\"animations\":[{\"type\":\"opacity\",\"value\":[0,1],\"bezier\":\"cubic-bezier#0,0,1,1\"}]},{\"type\":\"gamma\",\"angle\":[0,45],\"animations\":[{\"type\":\"opacity\",\"value\":[1,0],\"bezier\":\"cubic-bezier#0,0,1,1\"}]}]}]},{\"display_type\":\"physical_orientation\",\"contents\":[{\"file_url\":\"http://i0.hdslb.com/bfs/baselabs/bf59c670dfedf7246dccc8dade6cb25c65e4bba0.png\",\"scale\":1,\"physical_orientation\":[{\"type\":\"gamma\",\"angle\":[-45,0],\"animations\":[{\"type\":\"opacity\",\"value\":[1,0],\"bezier\":\"cubic-bezier#0,0,1,1\"}]},{\"type\":\"gamma\",\"angle\":[0,45],\"animations\":[{\"type\":\"opacity\",\"value\":[0,1],\"bezier\":\"cubic-bezier#0,0,1,1\"}]}]}]}]}}"
        val builder = GyroConfig.newBuilder()
        JsonFormat.parser().merge(json, builder)
        val gyro = builder.build()
    }

}