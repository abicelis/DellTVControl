# Hardware
* Pi 1
* 8GB microSDHC + micro to SD converter
* Dell P2714H Monitor
* Dell AC511 USB Soundbar


# Software
* Raspbian OS Buster Lite (May 2020)
* alsa
* triggerhappy


# What this guide does
Enable AC511's volume control knob through Raspi


# What this guide should eventually cover, or, TODO's
Hack the monitor using the RPi1 so that
  - Using a custom remote control, control power/volume/UI buttons.
  - When shutting down the "TV" (Via the remote or the Power button)
    - Set USB Soundbar volume to zero, or at least 50% vol (So that when turning back on we don't get crackly sounds from the VGA converter)
    - Shut down RPi gracefully.
    - Turn off Monitor.
  - When turning on handle USB soundbar volume.


# Dell USB soundbar installation

## Set Dell USB soundbar as default USB sound device
**Note:** OG Guide [here](https://www.raspberrypi-spy.co.uk/2019/06/using-a-usb-audio-device-with-the-raspberry-pi/)  

With the soundbar plugged in..

```bash
# List sound devices, Note the <card_number>
aplay -l

# Change ALSA config to set Dell Soundbar as Default Audio Device
sudo nano /usr/share/alsa/alsa.conf

# Change these
defaults.ctl.card <card_number>
defaults.pcm.card <card_number>
```



## Change volume and test soundbar sound

```bash
# Change volume with alsamixer "GUI"
alsamixer

# Test USB sound with
speaker-test -c2
# NOTE: Soundbar will either reproduce USB audio or 3.5mm audio, not both. To get USB audio, make sure to disconnect the 3.5mm jack!
```



## Change volume programatically

```bash
# List amixer controls ("Switch" controls mute/unmute, "Volume" controls change volume)
sudo amixer controls
# Note the "PCM Playback Volume" numid, in my case it's 6

# Get current volume
amixer cget numid=6

# Set volume
amixer cset numid=6 50%
# OR
/usr/bin/amixer -M set PCM 5%+
```



## Triggerhappy daemon - How to get volume knob working by listening to the VOLUME_UP and DOWN events that come from the Soundbar!
**Note:** OG Guide [here](https://blog.0x79.com/raspberry-pi-volume-keys-with-triggerhappy.html)  

HID? events that are sent by input devices are files in `/dev/input/event*`
These events have names like `KEY_VOLUMEUP`, one can find the correct names of specific keys/events using the triggerhappy daemon (thd)

```bash
# Run this then press a key / turn volume knob!
thd --dump /dev/input/event*
```


With those key names, we create a mapping config file

```bash
sudo nano /etc/triggerhappy/triggers.d/audio.conf

# Add these lines to change the volume when volume up down events are received
KEY_VOLUMEUP    1      /usr/bin/amixer -M set PCM 5%+
KEY_VOLUMEDOWN  1      /usr/bin/amixer -M set PCM 5%-
```


Restart the daemon!

```bash
sudo service triggerhappy restart

# NOTE: Can also do status/start/stop with the service command
# Note, the `service x status` command shows a little log, check it for status/errors!

# You can also run and see the daemon capture events then do its work with:
thd --triggers /etc/triggerhappy/triggers.d/ /dev/input/event*
```


Note, by default triggerhappy runs as user `nobody` (You can see this by running`sudo service triggerhappy status`), `nobody` doesn't have enough permissions to run `amixer`, so we'll need to change the user to 'pi'.
**Note:** OG Guide [here](https://retropie.org.uk/forum/topic/18133/triggerhappy-daemon-thd-doesn-t-work-on-my-pi-running-retropie-help/24?_=1594513494684&lang=en-US)  

```bash 
sudo nano /etc/systemd/system/multi-user.target.wants/triggerhappy.service
#Edit user 'nobody' to 'pi' there.
```


## Force HDMI so that RPi boots properly without an HDMI cable plugged in?
```bash
# Not exactly sure why this is needed, but...
# In the SD card, boot partition's ./config.txt, uncomment the line:
hdmi_force_hotplug=1

```





# Enable server

## Configure GPIO in/out setting and resistor pulls from startup

By default, the GPIO pins we're using are by default set as INPUTs, so we're good there.
We are changing the PULL (up/down) resustor modes tho.

More info here: https://www.raspberrypi.org/documentation/configuration/config-txt/gpio.md

```bash
sudo nano /boot/config.txt
# Add the lines below:

# GPIO configs for server
gpio=18,27,22,23,24=pn
gpio=17=pd
```


## Install git, link to GitHub via Pkey, clone the DellTVControl repo

```bash
# Geenrate a keypair
ssh-keygen

#Go to Github.com, install the public key into abicelis.
cat ~/.ssh/id_rsa.pub

# Install git
sudo apt-get install git

# Clone the repo
cd ~
mkdir workspace
cd workspace
git clone git@github.com:abicelis/DellTVControl.git

```


## Install Java 8 JDK, build/run the server or just run the fatJar from the repo

```bash
#To run the server, we need the Java JDK (RP1 only works with JDK v8)
sudo apt-get install openjdk-8-jre

# Add these to ~/.profile
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-armhf
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-armhf/bin

# From here, you can either run / build the project
cd ~/workspace/DellTVControl/server
./gradlew run
./gradlew build

# Or just run the latest fatjar from the repo (Recommended, gradle takes ages to do anything on a RP1)
cd ~/workspace/DellTVControl/server
java -jar ~/workspace/DellTVControl/server/fatjar/web-socket-server-x.y-all.jar

```


## Wiring table of TV cables - RPi GPIO pins

| Wire color | Function    | Physical Pin (RPi) | BCM Pin                                  | GPIO Boot Setting |
|------------|-------------|--------------------|------------------------------------------|-------------------|
|            |             |                    | Used in server code and /boot/config.txt | /boot/config.txt  |
| Brown      | GND         | 6                  | -                                        | -                 |
| Red        | POWER_SENSE | 11                 | 17                                       | IN/pulldown       |
| Orange     | MACRO_4     | 12                 | 18                                       | IN/no pull        |
| Yellow     | MACRO_2     | 13                 | 27                                       | IN/no pull        |
| Green      | MACRO_1     | 15                 | 22                                       | IN/no pull        |
| Blue       | MACRO_3     | 16                 | 23                                       | IN/no pull        |
| Gray       | POWER       | 18                 | 24                                       | IN/no pull        |
Table made with: https://www.tablesgenerator.com/markdown_tables

Also, here's a handy GPIO pin table which will let us make sure where the GPIO physical pins are
Note that we're using WiringPi's gpio utility to control the GPIO pins. 
Also note that WiringPi is pre installed in raspbian!
More info: http://wiringpi.com/the-gpio-utility/

Also also note that the server code uses the BCM (Broadcom) pin nomenclature.

```bash

pi@husker:~ $ gpio readall
 +-----+-----+---------+------+---+-Model B1-+---+------+---------+-----+-----+
 | BCM | wPi |   Name  | Mode | V | Physical | V | Mode | Name    | wPi | BCM |
 +-----+-----+---------+------+---+----++----+---+------+---------+-----+-----+
 |     |     |    3.3v |      |   |  1 || 2  |   |      | 5v      |     |     |
 |   2 |   8 |   SDA.1 |   IN | 1 |  3 || 4  |   |      | 5v      |     |     |
 |   3 |   9 |   SCL.1 |   IN | 1 |  5 || 6  |   |      | 0v      |     |     |
 |   4 |   7 | GPIO. 7 |   IN | 1 |  7 || 8  | 1 | ALT0 | TxD     | 15  | 14  |
 |     |     |      0v |      |   |  9 || 10 | 1 | ALT0 | RxD     | 16  | 15  |
 |  17 |   0 | GPIO. 0 |   IN | 0 | 11 || 12 | 0 | IN   | GPIO. 1 | 1   | 18  |
 |  27 |   2 | GPIO. 2 |   IN | 0 | 13 || 14 |   |      | 0v      |     |     |
 |  22 |   3 | GPIO. 3 |   IN | 0 | 15 || 16 | 0 | IN   | GPIO. 4 | 4   | 23  |
 |     |     |    3.3v |      |   | 17 || 18 | 0 | IN   | GPIO. 5 | 5   | 24  |
 |  10 |  12 |    MOSI |   IN | 0 | 19 || 20 |   |      | 0v      |     |     |
 |   9 |  13 |    MISO |   IN | 0 | 21 || 22 | 0 | IN   | GPIO. 6 | 6   | 25  |
 |  11 |  14 |    SCLK |   IN | 0 | 23 || 24 | 1 | IN   | CE0     | 10  | 8   |
 |     |     |      0v |      |   | 25 || 26 | 1 | IN   | CE1     | 11  | 7   |
 +-----+-----+---------+------+---+----++----+---+------+---------+-----+-----+
 | BCM | wPi |   Name  | Mode | V | Physical | V | Mode | Name    | wPi | BCM |
 +-----+-----+---------+------+---+-Model B1-+---+------+---------+-----+-----+
```


## Troubleshooting/testing the websocket server

Note: to troubleshoot the websocket server, you could use websocat..

` websocat --linemode-strip-newlines ws://192.168.0.101:55555`

Server accepts JSON in the form of: `{"action":"<action>","value":"<value_if_applicable>"}`

where <action> is one of: VOLUME_UP, VOLUME_DOWN, VOLUME_SET, VOLUME_GET, POWER_GET, POWER_TOGGLE, MACRO_1, POWER_GET, MACRO_3, MACRO_4

Examples:

```bash
{"action":"VOLUME_UP","value":""}
{"action":"VOLUME_SET","value":"44"}
{"action":"POWER_GET","value":""}
{"action":"POWER_TOGGLE","value":""}
{"action":"POWER_GET","value":""}
```


## Run the server on boot.

```bash
sudo nano /etc/rc.local
# Add this BEFORE the 'exit 0' line
# ALSO MAKE SURE TO REPLACE THE X Y placeholders in 'web-socket-server-X.Y-all.jar' WITH THE PROPER VERSION

# Run the web-socket-server
java -jar ~/workspace/DellTVControl/web-socket-server/fatjar/web-socket-server-X.Y-all.jar &
```

