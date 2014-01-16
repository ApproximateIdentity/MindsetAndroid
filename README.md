MindsetAndroid
==============

How to build project for Linux Mint (probably works for Ubuntu).
---

Need to install java development kit:

$ apt-get install openjdk-7-jdk

Need to install ant (equivalent of make for java):

$ apt-get install ant

Download and extract android sdk to folder "/path/to/sdk/". Run /path/to/sdk/tools/android to get a menu "Android SDK Manager". Install whatever is checked by default as well as "Android 4.3 (API 18)".

Make sure the following environmental variables are set:

export PATH=/path/to/sdk/platform-tools:/path/to/sdk/tools:$PATH
export ANDROID_HOME=/path/to/sdk
