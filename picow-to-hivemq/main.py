import time
import secrets
import network
import ntptime
import ussl

from machine import Pin
from umqtt.simple import MQTTClient
from hcsr04.hcsr04 import HCSR04
from time import sleep

# Blink the onboard LED to show startup
led = Pin("LED", Pin.OUT)
led.off()
time.sleep_ms(50)
led.on()
time.sleep_ms(50)
led.off()
time.sleep_ms(50)
led.on()
time.sleep_ms(50)
led.off()

# Load the WiFi and HiveMQ Cloud credentials from secrets.py
try:
    from secrets import secrets
except ImportError:
    print("Error, secrets could not be read")
    raise

# Connect to WiFi
# Based on https://datasheets.raspberrypi.com/picow/connecting-to-the-internet-with-pico-w.pdf
print('----------------------------------------------------------------------------------------------')
print('Connecting to AP: ' + secrets["ssid"])
wlan = network.WLAN(network.STA_IF)
wlan.active(True)
wlan.connect(secrets["ssid"], secrets["password"])

# Wait for WiFi connection or failure
connected = False
attempt = 0
while not connected and attempt < 10:
    attempt += 1
    if wlan.status() < 0 or wlan.status() >= 3:
        connected = True
    if not connected:
        print("Connection attempt failed: " + str(attempt))
        time.sleep(1)
    else:
        print("Connected on attempt: " + str(attempt))

if not connected or wlan.ifconfig()[0] == "0.0.0.0":
    # Blink LED to show there is a WiFi problem
    print("Bad WiFi connection: " + wlan.ifconfig()[0])
    while True:
        # Endless loop as we don't have a WiFi connection
        led.off()
        time.sleep_ms(150)
        led.on()
        time.sleep_ms(150)

# As we end up here, we now we have a WiFi connection
print("WiFi status: " + str(wlan.ifconfig()))
led.on()

# To validate certificates, a valid time is required
# NTP is used to get the correct time
# https://en.wikipedia.org/wiki/Network_Time_Protocol
print('----------------------------------------------------------------------------------------------')
print('Connecting to NTP')
ntptime.host = "de.pool.ntp.org"
ntptime.settime()
print('Current time: ' + str(time.localtime()))

# Load the certificate for secure connection to HiveMQ Cloud
print('----------------------------------------------------------------------------------------------')
print('Loading CA Certificate')
with open("/certs/hivemq-com-chain_2_only.der", 'rb') as f:
    cacert = f.read()
f.close()
print('Obtained CA Certificate')

# Connect to HiveMQ Cloud
# Based on https://www.tomshardware.com/how-to/send-and-receive-data-raspberry-pi-pico-w-mqtt
print('----------------------------------------------------------------------------------------------')
print("Connecting to " + secrets["broker"] + " as user " + secrets["mqtt_username"])

# Use sslparams as defined below for a secure connection
# sslparams = {'server_side': False,
#             'key': None,
#             'cert': None,
#             'cert_reqs': ussl.CERT_REQUIRED,
#             'cadata': cacert,
#             'server_hostname': secrets["broker"]}

# When using the sslparams below, a connection can be made to HiveMQ Cloud, but it's not secure
sslparams = {'server_hostname': secrets["broker"]}

mqtt_client = MQTTClient(client_id="picow",
                    server=secrets["broker"],
                    port=secrets["port"],
                    user=secrets["mqtt_username"],
                    password=secrets["mqtt_key"],
                    keepalive=3600,
                    ssl=True,
                    ssl_params=sslparams)
mqtt_client.connect()
print('Connected to MQTT Broker: ' + secrets["broker"])

# Send a test message to HiveMQ
mqtt_client.publish('test', 'HelloWorld')

# Continuously measure the distance and send the value to HiveMQ
# Based on https://randomnerdtutorials.com/micropython-hc-sr04-ultrasonic-esp32-esp8266/
hcsr04 = HCSR04(trigger_pin=17, echo_pin=16, echo_timeout_us=10000)
print("Starting the distance measurement")
killed = False
while not killed:
    # Measure distance
    distance = 0
    try:
        distance = hcsr04.distance_cm()
    except Exception as e:
        print("Distance measurement failure\n", e)

    # Send to HiveMQ Cloud
    try:
        json = "{\"value\": " + str(distance) + "}"
        print("\tMessage for queue: " + json)
        mqtt_client.publish("picow/distance", json)
    except Exception as e:
        print("\tMQTT publish Failed, retrying\n", e)
        killed = True
        continue

    # Sleep a second
    time.sleep(1)