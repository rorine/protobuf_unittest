
################################################################
# 单元测试.
################################################################

# 公共代码.

load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

MAIN_DEPS = [
        ":apigw_proto_library",
        "@maven//:com_google_truth_truth",
        "@maven//:junit_junit",
]

kt_jvm_library(
    name = "test_common_library",
    srcs = glob(["kotlin/**/*.kt"], exclude = ["kotlin/**/*Test.kt"]),
    deps = MAIN_DEPS,
    associates = ["//:protobuf_javalite_util",],
)

# kt_jvm_test(
#     name = "JsonFormatTest",
#     srcs = glob(["kotlin/**/*.kt"]),
#     test_class = "com.example.lib.moss.util.test.json.JsonFormatTest",
#     deps = [
#         ":apigw_proto_library",
#         "@maven//:com_google_truth_truth",
#         "@maven//:junit_junit",
#     ],
#     associates = ["//:protobuf_javalite_util",],
# )

load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_test")

kt_jvm_test(
    name = "JsonFormatTest",
    srcs = glob(["kotlin/com/example/lib/moss/util/test/json/JsonFormatTest.kt"]),
    test_class = "com.example.lib.moss.util.test.json.JsonFormatTest",
    associates = [":test_common_library"],
)

kt_jvm_test(
    name = "JsonParserTest",
    srcs = glob(["kotlin/com/example/lib/moss/util/test/json/JsonParserTest.kt"]),
    test_class = "com.example.lib.moss.util.test.json.JsonParserTest",
    associates = [":test_common_library"],
)

kt_jvm_test(
    name = "BodyFormTest",
    srcs = glob(["kotlin/com/example/lib/moss/util/test/rest/BodyFormTest.kt"]),
    test_class = "com.example.lib.moss.util.test.rest.BodyFormTest",
    associates = [":test_common_library"],
)

kt_jvm_test(
    name = "FieldFinderTest",
    srcs = glob(["kotlin/com/example/lib/moss/util/test/rest/FieldFinderTest.kt"]),
    test_class = "com.example.lib.moss.util.test.rest.FieldFinderTest",
    associates = [":test_common_library"],
)

kt_jvm_test(
    name = "PartialBodyFormTest",
    srcs = glob(["kotlin/com/example/lib/moss/util/test/rest/PartialBodyFormTest.kt"]),
    test_class = "com.example.lib.moss.util.test.rest.PartialBodyFormTest",
    associates = [":test_common_library"],
)

kt_jvm_test(
    name = "PartialBodyJsonTest",
    srcs = glob(["kotlin/com/example/lib/moss/util/test/rest/PartialBodyJsonTest.kt"]),
    test_class = "com.example.lib.moss.util.test.rest.PartialBodyJsonTest",
    associates = [":test_common_library"],
)

kt_jvm_test(
    name = "PathTest",
    srcs = glob(["kotlin/com/example/lib/moss/util/test/rest/PathTest.kt"]),
    test_class = "com.example.lib.moss.util.test.rest.PathTest",
    associates = [":test_common_library"],
)

kt_jvm_test(
    name = "QueryTest",
    srcs = glob(["kotlin/com/example/lib/moss/util/test/rest/QueryTest.kt"]),
    test_class = "com.example.lib.moss.util.test.rest.QueryTest",
    associates = [":test_common_library"],
)

kt_jvm_test(
    name = "CompatibilityTest",
    srcs = glob(["kotlin/com/example/lib/moss/util/test/CompatibilityTest.kt"]),
    test_class = "com.example.lib.moss.util.test.CompatibilityTest",
    associates = [":test_common_library"],
)



################################################################
# 编译protobuf 为 javalite.
################################################################

load("@rules_java//java:defs.bzl", "java_lite_proto_library")
load("@rules_proto//proto:defs.bzl", "proto_library")

java_lite_proto_library(
    name = "apigw_proto_library",
    deps = [":apigw_proto"],
)

# 使用默认的protoc版本.
proto_library(
    name = "apigw_proto",
    srcs = glob(["proto/*.proto"]),
    deps = [
        "@com_google_protobuf//:any_proto",
    ],
)

# 从bazel 1.0版本开始，已经没有了 proto:rules.bzl 和 proto_configure.
# 要使用 proto_lang_toolchain 来指定 protoc版本, 例如 3.13.0.
#

# load("@rules_proto//proto:rules.bzl", "proto_configure")
# proto_configure(
#     name = "custom_config",
#     protoc = "@com_google_protobuf//:protoc", #  protoc可执行文件
#     protocol_compiler_version = "3.17.3",  # 指定protoc版本,主要用于生成GRPC代码
# )

# ai上面给的配置语法太老了.
# proto_lang_toolchain(
#     name = "example_protoc",
#     protoc = "@com_google_protobuf//:protoc", # protoc 可执行文件
#     protoc_version = "3.13.0", # 指定版本号
# )



################################################################
# 编译protobuf 为 kotlin.
################################################################

# 注意, kotlin rules并没有定义如何从protobuf 生成kotlin代码.
# 所以需要第三方, 例如 pbandk 来从protobuf 生成kotlin代码.