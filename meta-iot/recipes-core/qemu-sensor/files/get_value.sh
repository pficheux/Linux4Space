#!/bin/sh

#MQTT_SERVER=test.mosquitto.org
# Host IP address (tap0)
MQTT_SERVER=192.168.7.1
TOPIC=yocto-iot-demo

while [ 1 ]
do
    mosquitto_pub -h $MQTT_SERVER -t $TOPIC -m "$(qemu-sensor)"
    sleep 5
done
