
# Linux4Space project

## Description

#### Context

Since a few years, spacecraft electronics is gaining enough capabilities to run complex operating systems such as Linux. As a result, it opens new horizons regarding the payload complexity. \
Linux4Space came as a standardization of a Linux distribution for space applications.
It consists in a [Yocto](https://www.yoctoproject.org) project defining the Linux4Space distribution, coming with features described in the next section.

#### Features

##### SpaceWire

[SpaceWire](https://www.esa.int/Enabling_Support/Space_Engineering_Technology/Onboard_Computers_and_Data_Handling/SpaceWire) is a spacecraft communication network. It is based on the IEEE 1355 standard, covering the physical and data-link OSI levels. \
Its development is coordinated by the ESA, in collaboration with mainly the NASA, the JAXA and the RKA.

The SpaceWire is currently supported by the [Digilent Zyboz7-20 board](https://digilent.com/reference/programmable-logic/zybo-z7/start) board. A userspace demonstration named "spwaxi-demo" gives an example of how to use the SpaceWire. This  demonstration sends data via SpaceWire, and expects to read tha same data as SpaceWire ports are looped back in the FPGA. \
You can find more information about its architecture and usage on the [Spwaxi demonstration project page](https://github.com/linux4space/spwaxi-dma-demo).

This feature is enabled by default in the "linux4space-image-base" image, but is not avalable in the  "linux4space-image-minimal" image. 
Activating the demonstration means adding the "spwaxi" and "spxaxi-demo" packages to the Yocto image using the  `IMAGE_INSTALL` variable.

<pre>
IMAGE_INSTALL_append_zynq7 = " spwaxi spwaxi-demo"
</pre>

The "spwaxi" Yocto package is a kernel module, whereas the "spwaxi-demo" is a userspace application. \
NOTE: adding the "spwaxi-demo" will implicitely add the "spwaxi" kernel module because of the dependency.

##### RMAP

The remote memory access protocol ([RMAP](https://www.esa.int/Enabling_Support/Space_Engineering_Technology/Microelectronics/SpW-RMAP-Astrium)) is a way for a SpaceWire node to read and write  memory of another SpaceWire node. The RMAP protocol standardizes the way in which SpaceWire units are configured and defines the low-level protocol for transfering of data between two SpaceWire nodes  (more information about [RMAP](https://www.fruct.org/publications/fruct6/files/Ole.pdf)).

The RMAP protocol can be tested with the [Demo Space Wire Library](https://github.com/linux4space/demo-SpaceWire-library). This repository contain demonstrations expemples of the Open Source [SpaceWireRMAPLibrary](https://github.com/yuasatakayuki/SpaceWireRMAPLibrary).

Activating the demonstration means adding the "demo-space-wire-library" package using the `IMAGE_INSTALL` variable.

`IMAGE_INSTALL_append = " demo-space-wire-library"`

##### XNG

XNG is the acronym of "XtratuM Next Generation", an hypervisor developped by [fentISS](https://fentiss.com/).
From [their official website](https://fentiss.com/products/hypervisor/): \
"XtratuM is a bare-metal space-qualified hypervisor aimed at safe and efficient embedded real-time systems. It enables applications to share the same (multicore) hardware platform without interfering with one another (time/space isolation) allowing easier reuse of (legacy) certified applications, simpler dynamic software updates and size, weight, power and cost reduction of embedded safety-critical systems."



##### Initramfs

In the case of the space industry, the operating systel is frequently executed in RAM as there is no filesystem support. Using the initramfs (INITial RAM FileSystem) feature 
is a good way to do this in the Linux environment.


## Installation

#### Prerequisites

Using Yocto needs to install some packages on your host machine. You should follow the [instructions](https://docs.yoctoproject.org/singleindex.html#document-brief-yoctoprojectqs/index) from the Yocto documentation. Alternatively, a Dockerfile is provided so you can only install [Docker](www.docker.com). If you chose the Docker method, you should add your login to the  "Docker" group.

In both cases, you must also install [Repo](https://storage.googleapis.com/git-repo-downloads/repo). Repo is a single-file executable, so you should set the right permissions and add it to your PATH.

#### Fetching the sources

You can download the sources the following command:

<pre>
$ repo init -u git@github.com:linux4space/linux4space-manifests.git -m &lt;manifest.xml&gt; -b &lt;yocto-branch&gt;
</pre>

For instance:

<pre>
$ repo init -u git@github.com:linux4space/linux4space-manifests.git -m zynq7-zyboz7-20.xml -b gatesgarth
</pre>


The available manifests are the following:

- `zynq7-zyboz7-20.xml` &rarr; running Linux4Space natively on the [Digilent Zyboz7-20 board](https://digilent.com/reference/programmable-logic/zybo-z7/start)
- `xng-zynq7-zyboz7-20.xml` &rarr; running Linux4Space as a [XNG](https://fentiss.com/products/hypervisor/) guest OS on the [Digilent Zyboz7-20 board](https://digilent.com/reference/programmable-logic/zybo-z7/start) board
- `raspberrypi3.xml` &rarr; running Linux4Space natively on the [Raspberry Pi 3 board](https://www.raspberrypi.com/products/raspberry-pi-3-model-b/)

In case of using XNG, you need to request a XNG license from fentISS so they provide you the XNG sources.
Then you must edit the `sources/meta-xilinx-xng/conf/machine/include/xng-conf-data.inc` file fetched locally to add the path to the XNG sources you get from fentISS.

<pre>
XNG_PATH = "/workdir/sources/xng/"
</pre>

WARNING: Be careful to put the XNG sources into at a path visible by the Docker container. \
Note: you should put the XNG sources in your `sources` directory and name it `xng` (as the example) for better compatibility in the [flashing](#zyboz7-20) step.


Now you can synchronize with the remote repository with:

<pre>
$ repo sync
</pre>

You can run this command at any time to keep your sources synchronized with the remote repository.

If you want to push modifications, you must set the current branch with:

<pre>
$ repo start &lt;branch-name&gt; &lt;repo-list&gt;
</pre>
such as:
<pre>
$ repo start gatesgarth meta-linux4space meta-raspberrypi-linux4space
</pre>


#### Setting up the environment

##### Without Docker

You can natively setup the environment by using the following commands in this order:

<pre>
$ source machine
$ source setupenv
</pre>

The image will be built in the `builds/build-<machine-name>` directory.


##### With Docker

Alternatively, you can setup the environment while using Docker with:

<pre>
$ make
</pre>

The `MACHINE` would be automatically exported in the Docker context. In both cases, the `MACHINE` value would be set regarding the pointed manifest.


## Building

#### Building the image

After sourcing the environment - either natively or through Docker - you can build an image with the following command:

<pre>
bitbake &lt;image-name&gt;
</pre>

For instance:

<pre>
bitbake linux4space-image-minimal
</pre>

The images currently available are:
- linux4space-image-minimal &rarr; a minimal image to boot the Linux4Space
- linux4space-image-base &rarr; an image containing the [Linux4Space features](#features)

Warning: if you are using XNG (if you used a "xng-*" manifest), please read [the prerequisites related](#xng).

#### Building the SDK

You can build the SDK with the command:

<pre>
$ bitbake &lt;image&gt; -c populate_sdk
</pre>

For instance:

<pre>
$ bitbake linux4space-image-minimal -c populate_sdk
</pre>

Then you can install the SDK by running the produced shell script (as root):

<pre>
$ sudo tmp/deploy/sdk/&lt;sdk-script&gt;
</pre>

For instance:

<pre>
$ sudo tmp/deploy/sdk/linux4space-glibc-x86_64-linux4space-image-minimal-cortexa9t2hf-neon-zynq7-zyboz7-20-toolchain-1.0.sh
</pre>

One the  SDK is installed, for instance in `/opt/linux4space`, you can load environment with the following command:

<pre>
$ source /opt/linux4space/1.0/environment-setup-cortexa9t2hf-neon-poky-linux-gnueabi
</pre>

Sourcing the SDK set variables such as `CC`, `ARCH` or `CROSS_COMPILE`:

<pre>
$ echo $CC
arm-poky-linux-gnueabi-gcc  -mthumb -mfpu=neon -mfloat-abi=hard -mcpu=cortex-a9 -fstack-protector-strong  -D_FORTIFY_SOURCE=2 -Wformat -Wformat-security -Werror=format-security --sysroot=/opt/linux4space/1.0/sysroots/cortexa9t2hf-neon-poky-linux-gnueabi
</pre>


## Flashing the reference boards

#### Zyboz7-20

Although it would be possible to flash with JTAG, only the flash via SD card is explained.

First of all, you must prepare the SD card partitions. You must create a boot partition with a minimum size of 50 Mo, formatted in FAT32. 
An optional rootfs partition with a minimum size of 200 Mo and formatted in EXT4 can be created if using a rootfs. \
The following layer describes the partitions with their recommended size and their contents depending on your needs:

| Partition | Size (MB)  | Format | Content                                                                                                                                                                                   |
|-----------|--------|--------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| boot      | 50  | FAT32  | - boot.bin (generated with bootgen, explanations below)<br />- boot.scr (optional, used when using Linux natively. Located in tmp/deploy/images/<my-machine>)                                 |
| rootfs    | 200 | EXT4   | - content of <my-image>-<my-machine>.tar.gz, for instance linux4space-image-base-zynq7-zyboz7-20.tar.gz (optional, used when using a rootfs. Located in tmp/deploy/images/<my-machine>) |



You need the [bootgen Xilinx utility](https://docs.xilinx.com/r/en-US/ug1400-vitis-embedded/Debugging-an-Application-using-the-User-Modified/Custom-FSBL) to create the `boot.bin` file. Bootgen takes a bif file as an argument, describing the contents of the `boot.bin` file. Working examples are provided in [examples/scripts](examples/scripts). You can build a `boot.bin` file with the following commands:

<pre>
$ cd sources/meta-linux4space/examples/scripts
$ mkdir output
$ bootgen -image &lt;bif-file&gt; -w -o output/boot.bin
</pre>

For instance:

<pre>
$ bootgen -image zynq7-zyboz7-20-spwaxi-demo-sd-rootfs.bif -w -o output/boot.bin
</pre>

WARNING: You should put the XNG sources in `sources/xng` (as recommended in the [XNG section](#xng)) as the XNG bif files reference the XNG sources at this path.

Then, you can copy and extract the files and archives to the corresponding SD card partitions, as described in the [Xilinx documentation](https://github.com/Xilinx/meta-xilinx/blob/master/meta-xilinx-bsp/README.booting.md).

#### Raspberry Pi 3

##### Standard mode

To flash the Raspberry Pi 3 SD card :

- find the SD card device with `$ lsblk`
- umount the SD card with `$ umount /dev/mmcblk0p*`
- copy the image file to the SD card :
```
$ sudo dd if=tmp/deploy/images/raspberrypi3/linux4space-image-base-raspberrypi3.wic of=/dev/mmcblk0 bs=1M
```
You can also use `bmaptool`, which is faster !
```
$ sudo bmaptool copy tmp/deploy/images/raspberrypi3/linux4space-image-base-raspberrypi3.wic.bz2 /dev/mmcblk0
```

#####  Initramfs mode 

To start the Raspberry Pi 3 in initramfs mode, you need to add some information in your `local.conf` file which is located in your current build directory (`build-raspberrypi3`).

To enable the initramfs mode, you must add the following lines to your `local.conf` file:
```
EXTRA_IMAGE_FEATURES += "debug-tweaks" 
ENABLE_UART = "1" 
IMAGE_FSTYPES_append_raspberrypi3 = " wic" 

# For Initramfs 
WKS_FILE_raspberrypi3 = "raspberrypi3-ramfs.wks" 
IMAGE_FSTYPES_append_raspberrypi3 = " cpio.gz" 

# Add the command to use initramfs 
RPI_EXTRA_CONFIG = ' \n\ initramfs linux4space-image-base-raspberrypi3.cpio.gz\n\’
```
You must rebuild your image to build the new image files:
`bitbake linux4space-image-base`

You can now flash SD card by performing the following steps:

- find the sd card with `$ lsblk`
- umount the SD card with `$ umount /dev/mmcblk0p*`)
- copy the WIK image file to the SD card :
```
$ sudo dd if=tmp/deploy/images/raspberrypi3/linux4space-image-base-raspberrypi3.wic of=/dev/mmcblk0 bs=1M
```
- eject then insert the SD card (in order to mount the partitions)
- copy the `linux4space-image-base-raspberrypi3.cpio.gz` file to the boot partition of the SD card
```
$ cp tmp/deploy/images/raspberrypi3/linux4space-image-base-raspberrypi3.cpio.gz /media/<user-name>/boot
```
- umount the SD card with `$ umount /dev/mmcblk0p*`


## Booting the reference boards

#### Zyboz7-20

You must set the JP5 jumper to the right boot mode (SD card or JTAG).

<figure align="center">
    <img src=pictures/zyboz7-20-boot-jumper.jpg width="500">
    <figcaption><i>Zyboz7-20 boot jumper</i></figcaption>
</figure>

You can power up the board using the micro USB port.

<figure align="center">
    <img src=pictures/zyboz7-20-micro-usb.jpg width="500">
    <figcaption><i>Zyboz7-20 micro USB</i></figcaption>
</figure>

Now you can start a console session using `/dev/ttyUSB1`, as the micro USB is also used as the UART debug port.

The login is "root" with no password.

#### Raspberrypi3

Turn on the Raspbery Pi and the board will start automatically.
You can use a consolee on `/dev/ttyUSB0`,

<figure align="center">
    <img src=pictures/raspberry-pi-3-UART-pins.png width="500">
    <figcaption><i>Raspberry Pi3 UART pinout</i></figcaption>
</figure>


A SSH dropbear server is also available on the target. The login is "root" with no password.


## Development

#### Adding a new package

An recipe example available from [recipes-linux4space/hello-from-linux4space](recipes-linux4space/hello-from-linux4space) describes how to add an example started by a SysvInit service.

#### Adding a new platform

Adding a new platform implies adding:
- a new Yocto MACHINE (BSP)
- a new Yocto layers stack 
- a new "repo" manifest

The MACHINE is added by creating a file named `setupenv_<machine-name>` in `meta-linux4space/tools/setupenv` containing:

<pre>
export MACHINE=&lt;machine-name&gt;
</pre>

For instance, the file `meta-linux4space/tools/setupenv/setupenv_zynq7-zyboz7-20` contains:

<pre>
export MACHINE=zynq7-zyboz7-20
</pre>

The `MACHINE` file must point to a file `<machine-name>.conf` provided by an external BSP layer. For instance, the `zynq7-zyboz7-20.conf` file is available in `meta-xilinx-xng/conf/machine`.

Adding a new layers stack means creating a `bblayers_<machine-name>.conf`  sample in `meta-linux4space/conf/templateconf`. This file be used as the `bblayers.conf` after initializing the environment. In this file, you must define which layers are needed for your platform, by modifying the `BBLAYERS` variable.

For instance, the `bblayers_zynq7-zyboz7-20.conf.sample` contains:

<pre>
LCONF_VERSION = "6"
BBPATH = "${TOPDIR}"
BBFILES ?= ""

BBLAYERS ?= " \
&nbsp;&nbsp;&nbsp;&nbsp;##OEROOT##/meta \
&nbsp;&nbsp;&nbsp;&nbsp;##OEROOT##/meta-poky \
&nbsp;&nbsp;&nbsp;&nbsp;##OEROOT##/../meta-openembedded/meta-oe \
&nbsp;&nbsp;&nbsp;&nbsp;##OEROOT##/../meta-xilinx/meta-xilinx-bsp \
&nbsp;&nbsp;&nbsp;&nbsp;##OEROOT##/../meta-xilinx-xng \
&nbsp;&nbsp;&nbsp;&nbsp;##OEROOT##/../meta-xilinx-linux4space \
&nbsp;&nbsp;&nbsp;&nbsp;##OEROOT##/../meta-linux4space \
 " 

BBLAYERS_ NON_REMOVABLE ?= " \
&nbsp;&nbsp;&nbsp;&nbsp;##OEROOT##/meta \
 "
</pre>


Finally, create the entry point of your new platform: the repo manifest. You should use the same layout as the current manifests availables in the [Linux4Space manifests repository](https://github.com/linux4space/linux4space-manifests).

A manifest is an XML document referencing the needed repositories, called "projects". For each repository, a revision must be present pointing to a commit SHA or a tag. \
The  "meta-linux4space" project is the top-level layer of the whole linux4space project. In addition to fetch the remote repository, the "meta-linux4space" project creates symbolic links for the following purposes:
* `templateconf/bblayers.conf.sample` &rarr; link to the `bblayers_<machine-name>.conf.sample`. You must adapt it to your needs.
* `templateconf/local.conf.sample` &rarr; link to a generic `local.conf.sample`.
* `machine` &rarr; link to the machine environment file. You must adapt it to your needs.
* `setupenv` &rarr; link to a generic environment file.
* `Makefile` &rarr; link to a generic Makefile used to build and run the Docker.
* `Docker` &rarr; link to a generic Dockerfile.

## Troubleshooting

- The Spwaxi demonstration does not work with XNG. The DMA seems to be not supported by XNG on the Zyboz7-20 platform.
- The SpaceWire library demonstration have has timeout failure when running with RMAP. please see the `Improvement` section of the [SpaceWire library](https://github.com/linux4space/demo-spaceWire-library) demo project

## TODO

- Continue the integration of the SpaceWire demos with XNG/initramfs
- Automating the image generation and flashing for the Zybo board
