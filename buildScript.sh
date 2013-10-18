#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"

if [ ! -f ./local.properties ]
then
    android update project --path .
fi

ant debug && adb install -r ./bin/Mindset-debug.apk
