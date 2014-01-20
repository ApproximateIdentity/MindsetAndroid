all:
	ant debug

install:
	adb install -r bin/Mindset-debug.apk

clean:
	rm -rf bin/*
