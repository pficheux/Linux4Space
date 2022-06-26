# Base this image on core-image-minimal
#
include recipes-core/images/core-image-minimal.bb

# MQTT
IMAGE_INSTALL += "mosquitto-clients"

# Simulated sensor
IMAGE_INSTALL += "qemu-sensor"

# haveged necessary for Dunfell ?
#IMAGE_INSTALL += "haveged"
