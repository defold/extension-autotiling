#!/usr/bin/env bash

rm -f autotiling/plugins/share/pluginAutoTiling.jar

java -jar bob.jar --debug --platform=x86_64-macos --verbose --build-artifacts=plugins --build-server=http://localhost:9000 clean build

cp build/x86_64-osx/autotiling/pluginAutoTiling.jar autotiling/plugins/share