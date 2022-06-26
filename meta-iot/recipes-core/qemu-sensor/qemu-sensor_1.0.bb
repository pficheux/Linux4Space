DESCRIPTION = "Simulated IoT sensor"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "http://pficheux.free.fr/pub/tmp/qemu-sensor-1.0.tar.gz file://init file://get_value.sh"

inherit cmake update-rc.d

SRC_URI[md5sum] = "4de05eaab461e23f8d352a7ff1a69cbc"

INITSCRIPT_NAME = "qemu-sensor"
INITSCRIPT_PARAMS = "defaults 99"

do_install_append() {
        install -m 0755 ${WORKDIR}/get_value.sh ${D}${bindir}
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/init  ${D}${sysconfdir}/init.d/qemu-sensor
}

