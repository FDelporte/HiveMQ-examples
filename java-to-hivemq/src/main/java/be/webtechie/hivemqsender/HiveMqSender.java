package be.webtechie.hivemqsender;

import be.webtechie.hivemqsender.hivemq.HiveMqManager;
import be.webtechie.hivemqsender.pi4j.SensorManager;
import com.pi4j.util.Console;

public class HiveMqSender {

    // Logger helper provided by Pi4J
    private static Console console;
    // Sends data to HiveMQ Cloud
    private static HiveMqManager hiveMqManager;
    // Initializes the sensors and reads the values
    private static SensorManager sensorManager;

    public static void main(String[] args) {
        console = new Console();
        hiveMqManager = new HiveMqManager(console);
        sensorManager = new SensorManager(console, hiveMqManager);

        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}