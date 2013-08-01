#!/bin/bash
killall Base
rm -rf ~/Downloads/tfapp.db

adb shell 'run-as com.deange.textfaker cat /data/data/com.deange.textfaker/databases/tfapp.db > /sdcard/tfapp.db'
adb pull /sdcard/tfapp.db ~/Downloads
open ~/Downloads/tfapp.db