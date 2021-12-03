# Based on examples provided by Adafruit
#
# https://learn.adafruit.com/getting-started-with-raspberry-pi-pico-circuitpython/circuitpython
# https://learn.adafruit.com/mqtt-in-circuitpython/connecting-to-a-mqtt-broker
# https://learn.adafruit.com/quickstart-rp2040-pico-with-wifi-and-circuitpython/circuitpython-wifi
# https://learn.adafruit.com/ultrasonic-sonar-distance-sensors/python-circuitpython
#
# SPDX-FileCopyrightText: 2021 ladyada for Adafruit Industries
# SPDX-License-Identifier: MIT

import time
import board
import busio
import adafruit_hcsr04
import adafruit_requests as requests
import adafruit_esp32spi.adafruit_esp32spi_socket as socket
import adafruit_minimqtt.adafruit_minimqtt as MQTT

from digitalio import DigitalInOut
from adafruit_esp32spi import adafruit_esp32spi

# Load the WiFi and HiveMQ Cloud credentials from secrets.py
try:
    from secrets import secrets
except ImportError:
    print("Error, secrets could not be read")
    raise

# MQTT Topic to publish data from Pico to HiveMQ Cloud
topic_name = "pico/distance"

# Initialize the Pico pins, WiFi module and distance sensor
esp32_cs = DigitalInOut(board.GP13)
esp32_ready = DigitalInOut(board.GP14)
esp32_reset = DigitalInOut(board.GP15)
spi = busio.SPI(board.GP10, board.GP11, board.GP12)
esp = adafruit_esp32spi.ESP_SPIcontrol(spi, esp32_cs, esp32_ready, esp32_reset)
hcsr04 = adafruit_hcsr04.HCSR04(trigger_pin=board.GP17, echo_pin=board.GP16)

# Handle HTTP requests
requests.set_socket(socket, esp)

# Check ESP32 status
print("Checking ESP32")
if esp.status == adafruit_esp32spi.WL_IDLE_STATUS:
    print("\tESP32 found and in idle mode")
print("\tFirmware version: ", esp.firmware_version)
print("\tMAC address: ", [hex(i) for i in esp.MAC_address])

# List the detected WiFi networks
print("Discovered WiFi networks:")
for ap in esp.scan_networks():
    print("\t", (str(ap["ssid"], "utf-8")), "\t\tRSSI: ", ap["rssi"])

# Connect to the configured WiFi network
print("Connecting to WiFi: ", secrets["ssid"])
while not esp.is_connected:
    try:
        esp.connect_AP(secrets["ssid"], secrets["password"])
    except RuntimeError as e:
        print("\tCould not connect to WiFi: ", e)
        continue
print("\tConnected to ", str(esp.ssid, "utf-8"), "\t\tRSSI:", esp.rssi)
print("\tIP address of this board: ", esp.pretty_ip(esp.ip_address))
print("\tPing google.com: " + str(esp.ping("google.com")) + "ms")

# Configure MQTT to use the ESP32 interface
MQTT.set_socket(socket, esp)

# Configure MQTT client (uses secure connection by default)
mqtt_client = MQTT.MQTT(
    broker=secrets["broker"],
    port=secrets["port"],
    username=secrets["mqtt_username"],
    password=secrets["mqtt_key"]
)

# Define callback methods and assign them to the MQTT events
def connected(client, userdata, flags, rc):
    print("\tConnected to MQTT broker: ", client.broker)

def disconnected(client, userdata, rc):
    print("\tDisconnected from MQTT broker!")

def publish(mqtt_client, userdata, topic, pid):
    print("\tPublished a message to: ", topic)

mqtt_client.on_connect = connected
mqtt_client.on_disconnect = disconnected
mqtt_client.on_publish = publish

# Connect to the MQTT broker
print("Connecting to MQTT broker...")
try:
    mqtt_client.connect()
    print("\tSucceeded")
except Exception as e:
    print("\tMQTT connect failed: ", e)

# Continuously measure the distance and send the value to HiveMQ
print("Starting the distance measurement")
killed = False
while not killed:
    # Measure distance
    distance = 0
    try:
        distance = hcsr04.distance
    except Exception as e:
        print("Distance measurement failure\n", e)

    # Send to HiveMQ Cloud
    try:
        json = "{\"value\": " + str(distance) + "}"
        print("\tMessage for queue: " + json)
        mqtt_client.publish(topic_name, json)
    except Exception as e:
        print("\tMQTT publish Failed, retrying\n", e)
        killed = True
        continue

    # Sleep a second
    time.sleep(1)