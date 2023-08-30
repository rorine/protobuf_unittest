package com.example.lib.moss.util.test.rest

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
import com.bapis.example.app.interfaces.v2.Able
import com.bapis.example.app.interfaces.v2.Boy
import com.bapis.example.app.interfaces.v2.Charles
import com.bapis.example.app.interfaces.v2.City
import com.bapis.example.app.interfaces.v2.Country
import com.bapis.example.app.interfaces.v2.Dog
import com.bapis.example.app.interfaces.v2.Province
import com.bapis.example.app.interfaces.v2.Town
import com.example.lib.moss.util.any.pack
import com.example.lib.moss.util.rest.getEncodedQueryString
import com.example.lib.moss.util.test.common.buildSimpleMessage
import com.example.lib.moss.util.test.common.initDelegate
import com.google.protobuf.ByteString
import org.junit.Before
import org.junit.Test
import java.nio.charset.Charset

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/8
 */

class QueryTest {

    @Before
    fun setUp() {
        initDelegate()
    }

    val defaultFullReqValue = "building=0&room=0&is_lockdown=false&name=&lockdown_type=0&name_with_suffix=0&name_with_prefix=0&interface=0&uint32_value=0&sint64_value=0&float_value=0.0&double_value=0.0&bytes_value=&null_string="

    @Test
    fun testOneof() {
        var skipFields = mutableListOf<String>()
        var root = FullReq.newBuilder()

        val join = RoomJoinEvent.getDefaultInstance()
        root.join = join

        var query = getEncodedQueryString(root.build(), skipFields)
        assert(query == "join=" + "&$defaultFullReqValue")
    }

    // Able 默认实现的值
    val defaultAbleValue = "dog_int=0&dog_enum=0&is_able=false&bytes_instance=&float_value=0.0&double_value=0.0"

    @Test
    fun testAble1() {
        val able = Able.newBuilder()
        able.addAStrings("Foo")
        able.addAStrings("Bar")
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "a_strings=Foo&a_strings=Bar")
        assert(query == "a_strings=Foo&a_strings=Bar" + "&$defaultAbleValue")
    }

    @Test
    fun testAble2() {
        val able = Able.newBuilder()
        able.addBoys(Boy.getDefaultInstance())
        able.addBoys(Boy.getDefaultInstance())
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "")
        assert(query == "boys.name=&boys.count=0&boys.will_skip=0&boys.dog=0&boys.name=&boys.count=0&boys.will_skip=0&boys.dog=0" + "&$defaultAbleValue")
    }

    /*
     * repeated message set value.
     */
    @Test
    fun testAble2New1() {
        val able = Able.newBuilder()
        val bb = Boy.newBuilder()
        bb.name = "22"
        bb.dog = Dog.DogZero
        able.addBoys(bb.build())

        bb.clear()
        bb.name = "33"
        bb.dog = Dog.DogOne
        able.addBoys(bb.build())
        var query = getEncodedQueryString(able.build(), emptyList())
        // 这里必须严格按照原始模型顺序.
        // assert(query == "boys.name=22&boys.name=33&boys.dog=DogOne")
        // assert(query == "boys.name=22&boys.name=33&boys.dog=1")
        assert(query == "boys.name=22&boys.count=0&boys.will_skip=0&boys.dog=0&boys.name=33&boys.count=0&boys.will_skip=0&boys.dog=1" + "&$defaultAbleValue");
    }

    @Test
    fun testAble3() {
        val able = Able.newBuilder()
        able.addDogs(Dog.DogOne)
        able.addDogs(Dog.DogZero)
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "dogs=DogOne&dogs=DogZero")
        // assert(query == "dogs=1&dogs=0")
        assert(query == "dogs=1&dogs=0" + "&$defaultAbleValue")
    }

    @Test
    fun testAble4() {
        val able = Able.newBuilder()
        able.addAInt64S(2233L)
        able.addAInt64S(9999L)
        // 注意这里的命名，proto中是 a_int64s， java是 aInt64S_，虽然java内部是ok的，但是java获得proto就不一致了.
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "a_int64s=2233&a_int64s=9999")
        // assert(query == "a_int64_s=2233&a_int64_s=9999")
        assert(query == "a_int64_s=2233&a_int64_s=9999" + "&$defaultAbleValue")
    }

    @Test
    fun testAble5() {
        val able = Able.newBuilder()
        able.addBytesArray(ByteString.copyFrom("中国", Charset.forName("UTF-8")))
        able.addBytesArray(ByteString.copyFrom("上海", Charset.forName("UTF-8")))
        able.addBytesArray(ByteString.copyFrom("浦东", Charset.forName("UTF-8")))
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "bytes_array=5Lit5Zu9&bytes_array=5LiK5rW3&bytes_array=5rWm5Lic")
        assert(query == "bytes_array=5Lit5Zu9&bytes_array=5LiK5rW3&bytes_array=5rWm5Lic" + "&$defaultAbleValue")
    }

    @Test
    fun testAble6() {
        val able = Able.newBuilder()
        val time = Time.newBuilder().setDate("20220609").build()
        val weather = Weather.newBuilder().setTemperature(22).build()
        able.addAnyList(pack(time))
        able.addAnyList(pack(weather))
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "")
        // assert(query == "any_list.%40type=type.googleapis.com%2Fexample.app.interface.v1.Time&any_list.date=20220609&any_list.%40type=type.googleapis.com%2Fexample.app.interface.v1.Weather&any_list.temperature=22")
        assert(query == "any_list.%40type=type.googleapis.com%2Fexample.app.interface.v1.Time&any_list.date=20220609&any_list.%40type=type.googleapis.com%2Fexample.app.interface.v1.Weather&any_list.temperature=22" + "&$defaultAbleValue")
    }

    @Test
    fun testAble7() {
        val able = Able.newBuilder()
        able.putStringLongMap("one", 1L)
        able.putStringLongMap("two", 2L)
        var query = getEncodedQueryString(able.build(), emptyList())
        assert(query == "string_long_map%5Bone%5D=1&string_long_map%5Btwo%5D=2" + "&$defaultAbleValue")
    }

    /*
     * 测试map
     */
    @Test
    fun testAble7New1() {
        val able = Able.newBuilder()
        able.putMapStringDog("zero", Dog.DogZero)
        able.putMapStringDog("one", Dog.DogOne)
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "map_string_dog%5Bzero%5D=DogZero&map_string_dog%5Bone%5D=DogOne")
        assert(query == "$defaultAbleValue&" + "map_string_dog%5Bzero%5D=0&map_string_dog%5Bone%5D=1")
    }

    @Test
    fun testAble8() {
        val able = Able.newBuilder()
        val boy = Boy.newBuilder().setName("boy and dog").setCount(1500).setWillSkip(2000).setDog(Dog.DogOne).build()
        able.setBoy(boy).build()
        val skipFields = mutableListOf("boy.will_skip")
        var query = getEncodedQueryString(able.build(), skipFields)
        // assert(query == "boy.name=boy%20and%20dog&boy.count=1500&boy.dog=DogOne")
        assert(query == "boy.name=boy%20and%20dog&boy.count=1500&boy.dog=1" + "&$defaultAbleValue")

        skipFields.clear()
        query = getEncodedQueryString(able.build(), skipFields)
        // assert(query == "boy.name=boy%20and%20dog&boy.count=1500&boy.will_skip=2000&boy.dog=DogOne")
        assert(query == "boy.name=boy%20and%20dog&boy.count=1500&boy.will_skip=2000&boy.dog=1" + "&$defaultAbleValue")
    }

    @Test
    fun testAble9() {
        val able = Able.newBuilder()
        val time = Time.newBuilder().setDate("20220609").build()
        able.bizObject = pack(time)
        var query = getEncodedQueryString(able.build(), emptyList())
        assert(query == "biz_object.%40type=type.googleapis.com%2Fexample.app.interface.v1.Time&biz_object.date=20220609" + "&$defaultAbleValue")
    }

    @Test
    fun testAble10() {
        val able = Able.newBuilder()
        val query = getEncodedQueryString(able.build(), emptyList())
        assert(query == defaultAbleValue)
    }

    @Test
    fun testAble11() {
        val able = Able.newBuilder()
        able.charlesOneof = Charles.getDefaultInstance()
        val query = getEncodedQueryString(able.build(), emptyList())
        // 注意，这个时候变成空的了, 因为charles里面没有字段.
        assert(query == "charles_oneof=" + "&$defaultAbleValue")
    }

    @Test
    fun testAble12() {
        val able = Able.newBuilder()
        able.boyOneof = Boy.getDefaultInstance()
        val query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "boy_oneof=")
        assert(query == "boy_oneof.name=&boy_oneof.count=0&boy_oneof.will_skip=0&boy_oneof.dog=0" + "&$defaultAbleValue")
    }

    @Test
    fun testAble13() {
        val able = Able.newBuilder()
        able.boyOneof = Boy.newBuilder().setCount(2233).setDog(Dog.DogOne).build()
        val query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "boy_oneof.count=2233&boy_oneof.dog=DogOne")
        // assert(query == "boy_oneof.count=2233&boy_oneof.dog=1")
        assert(query == "boy_oneof.name=&boy_oneof.count=2233&boy_oneof.will_skip=0&boy_oneof.dog=1" + "&$defaultAbleValue")
    }

    @Test
    fun testAble14() {
        val able = Able.newBuilder()
        able.dogInt = 1
        able.dogEnum = Dog.DogOne
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "dog_int=1&dog_enum=DogOne")
        // assert(query == "dog_int=1&dog_enum=1")
        assert(query == "dog_int=1&dog_enum=1&is_able=false&bytes_instance=&float_value=0.0&double_value=0.0")
    }

    @Test
    fun testAble15() {
        val able = Able.newBuilder()
        able.isAble = false
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "")
        assert(query == defaultAbleValue)

        able.isAble = true
        query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "is_able=true")
        assert(query == "dog_int=0&dog_enum=0&is_able=true&bytes_instance=&float_value=0.0&double_value=0.0")
    }

    @Test
    fun testAble16() {
        val able = Able.newBuilder()
        able.bytesInstance = ByteString.EMPTY
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "")
        assert(query == defaultAbleValue)

        able.bytesInstance = ByteString.copyFrom("お元気(げんき)ですか", "UTF-8")
        query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "bytes_instance=44GK5YWD5rCXKOOBkuOCk%2BOBjSnjgafjgZnjgYs%3D")
        assert(query == "dog_int=0&dog_enum=0&is_able=false&bytes_instance=44GK5YWD5rCXKOOBkuOCk%2BOBjSnjgafjgZnjgYs%3D&float_value=0.0&double_value=0.0")
    }

    @Test
    fun testAble17() {
        val able = Able.newBuilder()
        able.intBoy = 22
        able.intCharles = 0L
        var query = getEncodedQueryString(able.build(), emptyList())
        assert(query == "int_charles=0" + "&$defaultAbleValue")
    }

    @Test
    fun testAble18() {
        val able = Able.newBuilder()

        // 精度丢失.
        able.floatValue = 3121412.1231231F
        able.doubleValue = 3121412.1231231
        var query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "float_value=3121412.0&double_value=3121412.1231231")
        assert(query == "dog_int=0&dog_enum=0&is_able=false&bytes_instance=&float_value=3121412.0&double_value=3121412.1231231")

        able.floatValue = 33.22f
        able.doubleValue = 3121412.1231231
        query = getEncodedQueryString(able.build(), emptyList())
        // assert(query == "float_value=33.22&double_value=3121412.1231231")
        assert(query == "dog_int=0&dog_enum=0&is_able=false&bytes_instance=&float_value=33.22&double_value=3121412.1231231")
    }

    @Test
    fun testSkipField() {
        var skipFields = mutableListOf("province.city.town")

        val country = Country.newBuilder()
        val province = Province.newBuilder()
        val city = City.newBuilder()


        country.cName = "中国"
        country.cPopulation = 10000000
        var query = getEncodedQueryString(country.build(), skipFields)
        assert(query == "c_name=%E4%B8%AD%E5%9B%BD&c_population=10000000")

        val tb = Town.newBuilder()
        tb.addStreets("Shangfeng")
        tb.addStreets("Huadong")
        tb.addStreets("Lujiazui")

        city.town = tb.build()
        province.city = city.build()
        country.province = province.build()
        query = getEncodedQueryString(country.build(), skipFields)
        // assert(query == "c_name=%E4%B8%AD%E5%9B%BD&c_population=10000000")
        assert(query == "c_name=%E4%B8%AD%E5%9B%BD&c_population=10000000&province.p_name=&province.p_population=0&province.city.c_name=&province.city.c_population=0")

        skipFields.clear()
        query = getEncodedQueryString(country.build(), skipFields)
        // assert(query == "c_name=%E4%B8%AD%E5%9B%BD&c_population=10000000&province.city.town.streets=Shangfeng&province.city.town.streets=Huadong&province.city.town.streets=Lujiazui")
        assert(query == "c_name=%E4%B8%AD%E5%9B%BD&c_population=10000000&province.p_name=&province.p_population=0&province.city.c_name=&province.city.c_population=0&province.city.town.streets=Shangfeng&province.city.town.streets=Huadong&province.city.town.streets=Lujiazui")
    }

    // TODO 更新fullReq + query/form.
    // @Test
    fun fullTest() {
        var expect = ""

        var skipFields = mutableListOf<String>()

        var root = FullReq.newBuilder()
        root.building = 2

        var query = getEncodedQueryString(root.build(), skipFields)
        expect += "building=2"
        assert(query == expect)

        root.room = 402
        query = getEncodedQueryString(root.build(), skipFields)
        expect += "&room=402"
        assert(query == expect)

        root.isLockdown = true
        query = getEncodedQueryString(root.build(), skipFields)
        expect += "&is_lockdown=true"
        assert(query == expect)

        root.name = "hello json !.~*-"
        query = getEncodedQueryString(root.build(), skipFields)
        expect += "&name=hello%20json%20%21.~%2A-"
        assert(query == expect)

        val wb = Water.newBuilder()
        wb.count = 100
        wb.tank = Tank.newBuilder().setVolumn(5).build()
        val curretWater = wb.build()
        root.currentWater = curretWater

        query = getEncodedQueryString(root.build(), skipFields)
        expect += "&current_water.count=100&current_water.tank.volumn=5"
        assert(query == expect)

        val bwb = Water.newBuilder()
        val bw1 = bwb.setCount(100000).setTank(Tank.newBuilder().setVolumn(50).build()).build()
        val bw2 = bwb.setCount(200000).setTank(Tank.newBuilder().setVolumn(50).build()).build()
        root.addAllBackupWater(listOf(bw1, bw2))
        query = getEncodedQueryString(root.build(), skipFields)
        // assert(query == "building=2&room=402&is_lockdown=true&name=hello%20json%20%21.~%2A-&current_water.count=100&current_water.tank.volumn=5")
        // assert(query == "building=2&room=402&is_lockdown=true&name=hello%20json%20%21.~%2A-&current_water.count=100&current_water.tank.volumn=5&backup_water.count=100000&backup_water.tank.volumn=50&backup_water.count=200000&backup_water.tank.volumn=50")
        expect += "&backup_water.count=100000&backup_water.tank.volumn=50&backup_water.count=200000&backup_water.tank.volumn=50"
        assert(query == expect)

        val fb = Food.newBuilder()
        fb.count = 9999999

        val rfb = RealFood.newBuilder()
        fb.putFoodMap("rice", rfb.setMeals(111111).build())
        fb.putFoodMap("noddle", rfb.setMeals(222222).build())
        root.food = fb.build()

        query = getEncodedQueryString(root.build(), skipFields)
        // assert(query == "building=2&room=402&is_lockdown=true&name=hello%20json%20%21.~%2A-&current_water.count=100&current_water.tank.volumn=5&food.count=9999999")
        // assert(query == "building=2&room=402&is_lockdown=true&name=hello%20json%20%21.~%2A-&current_water.count=100&current_water.tank.volumn=5&backup_water.count=100000&backup_water.tank.volumn=50&backup_water.count=200000&backup_water.tank.volumn=50&food.count=9999999")
        expect += "&food.count=9999999&food.food_map%5Brice%5D.meals=111111&food.food_map%5Bnoddle%5D.meals=222222"
        assert(query == expect)

        val fruit = Fruit.newBuilder()
        root.putFruits(603, fruit.setName("apple").setWeight(603603).build())
        root.putFruits(402, fruit.setName("watermelon").setWeight(402402).build())

        query = getEncodedQueryString(root.build(), skipFields)
        expect += "&fruits%5B603%5D.name=apple&fruits%5B603%5D.weight=603603&fruits%5B402%5D.name=watermelon&fruits%5B402%5D.weight=402402"
        assert(query == expect)

        root.lockdownType = LockdownType.LockdownTypePrecaution
        query = getEncodedQueryString(root.build(), skipFields)
        // expect += "&lockdown_type=LockdownTypePrecaution"
        expect += "&lockdown_type=2"
        assert(query == expect)

        val join = RoomJoinEvent.getDefaultInstance()
        root.join = join
        query = getEncodedQueryString(root.build(), skipFields)
        expect = "join=&" + expect
        assert(query == expect)

        val leave = RoomLeaveEvent.getDefaultInstance()
        root.leave = leave
        skipFields.add("leave")
        query = getEncodedQueryString(root.build(), skipFields)
        expect = expect.replace("join=&", "")
        assert(query == expect)

        root.fooInt = 22
        root.barInt = 33
        query = getEncodedQueryString(root.build(), skipFields)
        expect = "bar_int=33&" + expect
        assert(query == expect)


        root.floatValue = 3121412.1231231F
        root.doubleValue = 3121412.1231231
        query = getEncodedQueryString(root.build(), skipFields)
        expect += "&float_value=3121412.0&double_value=3121412.1231231"
        assert(query == expect)

    }

    @Test
    fun fullSimpleMessage() {
        var skipFields = mutableListOf<String>()
        val message = buildSimpleMessage()
        var query = getEncodedQueryString(message, skipFields)
        // assert(query == "id=100&num=1000&lang=zh&embedded.bool_val=true&embedded.int32_val=1111&embedded.repeated_string_val=aaa&embedded.repeated_string_val=bbb&embedded.map_string_val%5Bkey%5D=value&embedded.map_error_val%5Bkey%5D.code=10000")
        assert(query == "id=100&num=1000&lang=zh&embedded.bool_val=true&embedded.int32_val=1111&embedded.int64_val=0&embedded.float_val=0.0&embedded.double_val=0.0&embedded.string_val=&embedded.repeated_string_val=aaa&embedded.repeated_string_val=bbb&embedded.map_string_val%5Bkey%5D=value&embedded.map_error_val%5Bkey%5D.code=10000&embedded.map_error_val%5Bkey%5D.reason=&embedded.map_error_val%5Bkey%5D.message=")
    }
}