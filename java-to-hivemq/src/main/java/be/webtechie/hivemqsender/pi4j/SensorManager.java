package be.webtechie.hivemqsender.pi4j;

import be.webtechie.hivemqsender.hivemq.HiveMqManager;
import be.webtechie.hivemqsender.model.Sensor;
import be.webtechie.hivemqsender.pi4j.components.*;
import com.pi4j.context.Context;
import com.pi4j.util.Console;

import java.util.Timer;
import java.util.TimerTask;

public class SensorManager {

    private static final String TOPIC_MOTION = "crowpi/motion";
    private static final String TOPIC_NOISE = "crowpi/noise";
    private static final String TOPIC_TOUCH = "crowpi/touch";
    private static final String TOPIC_TILT = "crowpi/tilt";
    private static final String TOPIC_SENSORS = "crowpi/sensors";

    private static final String VALUE_TRUE = "{\"value\":true}";
    private static final String VALUE_FALSE = "{\"value\":false}";

    public SensorManager(Console console, HiveMqManager hiveMqManager) {
        Context pi4j = CrowPiPlatform.buildNewContext();

        PirMotionSensorComponent motionSensor = new PirMotionSensorComponent(console, pi4j);
        motionSensor.onMovement(() -> hiveMqManager.sendMessage(TOPIC_MOTION, VALUE_TRUE));
        motionSensor.onStillstand(() -> hiveMqManager.sendMessage(TOPIC_MOTION, VALUE_FALSE));

        SoundSensorComponent soundSensor = new SoundSensorComponent(console, pi4j);
        soundSensor.onNoise(() -> hiveMqManager.sendMessage(TOPIC_NOISE, VALUE_TRUE));
        soundSensor.onSilence(() -> hiveMqManager.sendMessage(TOPIC_NOISE, VALUE_FALSE));

        TouchSensorComponent touchSensor = new TouchSensorComponent(console, pi4j);
        touchSensor.onTouch(() -> hiveMqManager.sendMessage(TOPIC_TOUCH, VALUE_TRUE));
        touchSensor.onRelease(() -> hiveMqManager.sendMessage(TOPIC_TOUCH, VALUE_FALSE));

        TiltSensorComponent tiltSensor = new TiltSensorComponent(console, pi4j);
        tiltSensor.onTiltLeft(() -> hiveMqManager.sendMessage(TOPIC_TILT, "{\"value\":\"left\"}"));
        tiltSensor.onTiltRight(() -> hiveMqManager.sendMessage(TOPIC_TILT, "{\"value\":\"right\"}"));
        tiltSensor.onShake(() -> hiveMqManager.sendMessage(TOPIC_TILT, "{\"value\":\"shaking\"}"));

        HumiTempComponent dht11 = new HumiTempComponent(console, pi4j);
        LightSensorComponent lightSensor = new LightSensorComponent(console, pi4j);
        UltrasonicDistanceSensorComponent distanceSensor = new UltrasonicDistanceSensorComponent(console, pi4j);

        Timer timer = new Timer();
        TimerTask task = new SendMeasurements(hiveMqManager, dht11, lightSensor, distanceSensor);
        timer.schedule(task, 10_000, 5_000);
    }

    private static class SendMeasurements extends TimerTask {
        private final HiveMqManager hiveMqManager;
        private final HumiTempComponent dht11;
        private final LightSensorComponent lightSensor;
        private final UltrasonicDistanceSensorComponent distanceSensor;

        public SendMeasurements(HiveMqManager hiveMqManager,
                                HumiTempComponent dht11,
                                LightSensorComponent lightSensor,
                                UltrasonicDistanceSensorComponent distanceSensor) {
            this.hiveMqManager = hiveMqManager;
            this.dht11 = dht11;
            this.lightSensor = lightSensor;
            this.distanceSensor = distanceSensor;
        }

        @Override
        public void run() {
            var sensor = new Sensor(dht11.getTemperature(), dht11.getHumidity(),
                    lightSensor.readLight(2), distanceSensor.measure());
            hiveMqManager.sendMessage(TOPIC_SENSORS, sensor.toJson());
        }
    }
}
