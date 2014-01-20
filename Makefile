all:
	rm -rf bin/*
	ant debug

install:
	adb install -r bin/Mindset-debug.apk
