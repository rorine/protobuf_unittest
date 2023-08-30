workspace(name = "protobuf_javalite_util")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load('@bazel_tools//tools/build_defs/repo:git.bzl', 'git_repository')

################################################################
# Kotlin Rules
################################################################

rules_kotlin_version = "1.8"
rules_kotlin_sha = "01293740a16e474669aba5b5a1fe3d368de5832442f164e4fbfc566815a8bc3a"
# rules_kotlin_commit = "7e419d2a007e0731d69bdec966636f156218a85e"
http_archive(
    name = "io_bazel_rules_kotlin",
    urls = ["https://github.com/bazelbuild/rules_kotlin/releases/download/v%s/rules_kotlin_release.tgz" % rules_kotlin_version],
    sha256 = rules_kotlin_sha,
    # https://github.com/bazelbuild/rules_kotlin/archive/7e419d2a007e0731d69bdec966636f156218a85e.zip
    # urls = ["https://github.com/bazelbuild/rules_kotlin/archive/%s.zip" % rules_kotlin_commit],
)

# 这样就可以使用这些rules: kt_jvm_binary, kt_jvm_library as well as kt_jvm_test
# 当然我们也可以使用 kt_android_binary, kt_android_library, kt_android_test

# 注意, kotlin rules并没有定义如何从protobuf 生成kotlin代码.

load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")
kotlin_repositories() # if you want the default. Otherwise see custom kotlinc distribution below

load("@io_bazel_rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")
kt_register_toolchains() # to use the default toolchain, otherwise see toolchains below


################################################################
# rules_jvm_external rules.
################################################################

# # 使用官方版本.

# RULES_JVM_EXTERNAL_TAG = "5.2"
# RULES_JVM_EXTERNAL_SHA ="f86fd42a809e1871ca0aabe89db0d440451219c3ce46c58da240c7dcdc00125f"

# http_archive(
#     name = "rules_jvm_external",
#     strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
#     sha256 = RULES_JVM_EXTERNAL_SHA,
#     url = "https://github.com/bazelbuild/rules_jvm_external/releases/download/%s/rules_jvm_external-%s.tar.gz" % (RULES_JVM_EXTERNAL_TAG, RULES_JVM_EXTERNAL_TAG)
# )


# 使用私有版本, 基于5.2.

# # 直接从gitlab下载 https://git.example.co/zhangyuling/rules_jvm_external/-/archive/1da66b3ef55f7e4eaddd2c527e10fffa2d445f85/rules_jvm_external-1da66b3ef55f7e4eaddd2c527e10fffa2d445f85.tar.gz

# RULES_JVM_EXTERNAL_COMMIT_ID = "1da66b3ef55f7e4eaddd2c527e10fffa2d445f85"
# http_archive(
#    name = "rules_jvm_external",
#    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_COMMIT_ID,
#    url = "https://zhangyuling:glpat-5KAiEwDgUrvbxcQSYkHy@git.example.co/zhangyuling/rules_jvm_external/-/archive/%s/rules_jvm_external-%s.tar.gz" % (RULES_JVM_EXTERNAL_COMMIT_ID, RULES_JVM_EXTERNAL_COMMIT_ID)
# )

git_repository(
    name = "rules_jvm_external",
    remote = "git@git.example.co:zhangyuling/rules_jvm_external.git",
    commit = "bcb4f52cdad11bae464553874bdf7593239ca12a",
)

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")
rules_jvm_external_deps()

load("@rules_jvm_external//:setup.bzl", "rules_jvm_external_setup")
rules_jvm_external_setup()


# 列出项目需要的依赖.
load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "com.google.code.gson:gson:2.8.6",
        "com.google.protobuf:protobuf-javalite:3.17.3",
        # "org.jetbrains.kotlin:kotlin-stdlib:1.2.71",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31",

        # Test.
        "com.google.truth:truth:1.0",
        "junit:junit:4.12",
        "com.google.guava:guava:31.1-jre",
    ],
    repositories = [
        # see https://stackoverflow.com/questions/63600582/bazel-test-returns-checksum-mismatch-error-for-private-github-repos
        # 之前不能使用公司的私有repos
        "http://nexus.example.co/content/groups/carbon/",
        # "https://repo1.maven.org/maven2",
    ]
)

################################################################
# bazel-skylib 常用的构建规则和工具函数
################################################################

http_archive(
    name = "bazel_skylib",
    urls = [
        "https://github.com/bazelbuild/bazel-skylib/releases/download/1.2.1/bazel-skylib-1.2.1.tar.gz",
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/1.2.1/bazel-skylib-1.2.1.tar.gz",
    ],
    sha256 = "f7be3474d42aae265405a592bb7da8e171919d74c16f082a5457840f06054728",
)
load("@bazel_skylib//:workspace.bzl", "bazel_skylib_workspace")
bazel_skylib_workspace()


################################################################
# Java Rules
################################################################

# 这样我们可以使用 java_binary, java_import, java_library,
# java_lite_proto_library, java_proto_library, java_test, java_plugin, java_runtime, java_toolchain

# 这里只是生成代码, 原始proto的解析要使用 Protocol Buffer Rules.

http_archive(
    # name = "rules_java",
    # sha256 = "ccf00372878d141f7d5568cedc4c42ad4811ba367ea3e26bc7c43445bbc52895",
    # strip_prefix = "rules_java-d7bf804c8731edd232cb061cb2a9fe003a85d8ee",
    # urls = [
    #     "https://mirror.bazel.build/github.com/bazelbuild/rules_java/archive/d7bf804c8731edd232cb061cb2a9fe003a85d8ee.tar.gz",
    #     "https://github.com/bazelbuild/rules_java/archive/d7bf804c8731edd232cb061cb2a9fe003a85d8ee.tar.gz",
    # ],
    name = "rules_java",
    urls = [
        "https://github.com/bazelbuild/rules_java/releases/download/6.1.0/rules_java-6.1.0.tar.gz",
    ],
    sha256 = "78e3c24f05cffed529bfcafd1f7a8d1a7b97b4a411f25d8d3b4d47d9bb980394",
)

load("@rules_java//java:repositories.bzl", "rules_java_dependencies", "rules_java_toolchains")
rules_java_dependencies()
rules_java_toolchains()


################################################################
# Protocol Buffer Rules
################################################################

# 这样我们可以使用 proto_lang_toolchain 和 proto_library
# 其中 proto_lang_toolchain 定义LANG_proto_library rule (例如java_proto_library) 怎么调用 proto-compiler

http_archive(
    # name = "rules_proto",
    # sha256 = "2490dca4f249b8a9a3ab07bd1ba6eca085aaf8e45a734af92aad0c42d9dc7aaf",
    # strip_prefix = "rules_proto-218ffa7dfa5408492dc86c01ee637614f8695c45",
    # urls = [
    #     "https://mirror.bazel.build/github.com/bazelbuild/rules_proto/archive/218ffa7dfa5408492dc86c01ee637614f8695c45.tar.gz",
    #     "https://github.com/bazelbuild/rules_proto/archive/218ffa7dfa5408492dc86c01ee637614f8695c45.tar.gz",
    # ],
    name = "rules_proto",
    sha256 = "dc3fb206a2cb3441b485eb1e423165b231235a1ea9b031b4433cf7bc1fa460dd",
    strip_prefix = "rules_proto-5.3.0-21.7",
    urls = [
        "https://github.com/bazelbuild/rules_proto/archive/refs/tags/5.3.0-21.7.tar.gz",
    ],
)

load("@rules_proto//proto:repositories.bzl", "rules_proto_dependencies", "rules_proto_toolchains")
rules_proto_dependencies()
rules_proto_toolchains()

