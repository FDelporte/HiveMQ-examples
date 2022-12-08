# HiveMQ getting started examples

These example projects are linked to posts on the HiveMQ website to illustrate 
the use of a queue between multiple Raspberry Pi to collect and visualize data.

## Articles

### Part 1: Sending sensor data from Raspberry Pi with Java and Pi4j to HiveMQ Cloud

**Directory: java-to-hivemq**

Full article: [MQTT on Raspberry Pi: Send Sensor Data to HiveMQ Cloud with Java and Pi4J](https://www.hivemq.com/blog/mqtt-raspberrypi-part01-sensor-data-hivemqcloud-java-pi4j/)

***

### Part 2: Visualizing data from HiveMQ Cloud on a TilesFX dashboard on Raspberry Pi

**Directory: javafx-ui-hivemq**

Full article: [Using MQTT and Raspberry Pi to Visualize Sensor Data on a TilesFX Dashboard](https://www.hivemq.com/blog/mqtt-raspberrypi-part02-visualizing-sensor-data-on-a-tilesfx-dashboard/)

***

### Part 3: Sending sensor data from Raspberry Pi Pico to HiveMQ Cloud 

**Directory: pico-to-hivemq**

Full article: [Sending sensor data from Raspberry Pi Pico to HiveMQ Cloud](https://www.hivemq.com/blog/mqtt-raspberrypi-part03-sending-sensor-data-hivemqcloud-pico/)

***

### Part 4: Sending sensor data from Raspberry Pi Pico W to HiveMQ Cloud

**Directory: picow-to-hivemq**

Full article: [Sending sensor data from Raspberry Pi Pico W to HiveMQ Cloud](https://webtechie.be/post/2022-12-07-sending-sensor-data-from-raspberry-pi-pico-w-to-hivemq-cloud/)

***

## Notes

If you are developing this application on a PC, you can easily transfer the files to a Raspberry Pi to compile and run
the code on it with the following command (replace with the IP of your board):

E.g. for the first part:

```shell
$ cd java-to-hivemq
$ scp -r * pi@IP_OF_YOUR_PI://home/pi/java-to-hivemq
```