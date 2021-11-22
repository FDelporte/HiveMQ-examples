package be.webtechie.hivemqclient;

import be.webtechie.hivemqclient.ui.DashboardView;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class HiveMqClient extends Application {

    private static final Logger logger = LogManager.getLogger(HiveMqClient.class.getName());

    public static final String TOPIC_MOTION = "crowpi/motion";
    public static final String TOPIC_NOISE = "crowpi/noise";
    public static final String TOPIC_SENSORS = "crowpi/sensors";
    public static final String TOPIC_TILT = "crowpi/tilt";
    public static final String TOPIC_TOUCH = "crowpi/touch";

    private static final String HIVEMQ_SERVER = "ID_OF_YOUR_INSTANCE.s1.eu.hivemq.cloud";
    private static final String HIVEMQ_USER = "YOUR_USERNAME";
    private static final String HIVEMQ_PASSWORD = "YOUR_PASSWORD";
    private static final int HIVEMQ_PORT = 8883;

    @Override
    public void start(Stage stage) {
        logger.info("Starting up...");

        Mqtt5AsyncClient client = MqttClient.builder()
                .useMqttVersion5()
                .identifier("JavaFX_" + UUID.randomUUID())
                .serverHost(HIVEMQ_SERVER)
                .serverPort(HIVEMQ_PORT)
                .sslWithDefaultConfig()
                .buildAsync();

        client.connectWith()
                .simpleAuth()
                .username(HIVEMQ_USER)
                .password(HIVEMQ_PASSWORD.getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        logger.error("Could not connect to HiveMQ: {}", throwable.getMessage());
                    } else {
                        logger.info("Connected to HiveMQ: {}", connAck.getReasonCode());
                    }
                });

        var scene = new Scene(new DashboardView(client), 1024, 620);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}