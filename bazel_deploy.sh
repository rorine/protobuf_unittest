#!/bin/bash

# 发布到本地.m2.
bazel run --define "maven_repo=file://$HOME/.m2/repository"  //:export_protobuf_javalite_util.publish

# 发布到nexus.
# bazel run --define "maven_repo=https://nexus.example.co/content/repositories/releases" //:export_protobuf_javalite_util.publish