#!/bin/sh

pid=$(ps aux | grep -v grep | grep com.molecode.w2k.W2KApplication | awk '{print $2}')

if [ -n "$pid" ];
then
	echo "Kill process $pid."
	kill $pid
else
	echo "W2K process not found."
fi