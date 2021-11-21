# HiveMQ getting started examples

These example projects are linked to posts on the HiveMQ website to illustrate 
the use of a queue between multiple Raspberry Pi to collect and visualize data.

## Articles

### Part 1: Sending sensor data from Raspberry Pi with Java and Pi4j to HiveMQ

**Directory: java-to-hivemq**

***




## Notes

If you are developing this application on a PC, you can easily transfer the files to a Raspberry Pi to compile and run
the code on it with the following command (replace with the IP of your board):

E.g. for the first part:

```shell
$ cd java-to-hivemq
$ scp -r * pi@IP_OF_YOUR_PI://home/pi/java-to-hivemq
```