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


## Install Java 8 JDK, build and run the server.

```bash
#Server is built on Ktor, a Kotlin server API thing, we need jdk (RP1 only likes jdk v8) to run Kotlin code
sudo apt-get install openjdk-8-jre

# Add these to ~/.profile
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-armhf
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-armhf/bin

#You should be able to run / build the server with

cd ~/workspace/DellTVControl/server
./gradlew run
./gradlew build


# Run the built jar with
java -jar build/libs/tvcontrol-0.0.4-all.jar

```

















