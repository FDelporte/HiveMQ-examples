package be.webtechie.hivemqclient.ui;

import be.webtechie.hivemqclient.model.Sensor;
import be.webtechie.hivemqclient.ui.tiles.NoiseTextTile;
import be.webtechie.hivemqclient.ui.tiles.SensorSwitchTile;
import be.webtechie.hivemqclient.ui.tiles.TiltStatusTile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import eu.hansolo.tilesfx.tools.Helper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

import static be.webtechie.hivemqclient.HiveMqClient.*;
import static be.webtechie.hivemqclient.ui.tiles.BaseTile.TILE_HEIGHT;
import static be.webtechie.hivemqclient.ui.tiles.BaseTile.TILE_WIDTH;

public class DashboardView extends FlowGridPane {

    private static final Logger logger = LogManager.getLogger(DashboardView.class.getName());

    private final ObjectMapper mapper = new ObjectMapper();

    private final Tile gaucheTemperature;
    private final Tile gaucheDistance;
    private final Tile gaucheLight;
    private final Tile percentageHumidity;

    public DashboardView(Mqtt5AsyncClient client) {
        super(5, 2);

        logger.info("Creating dashboard view");

        setHgap(5);
        setVgap(5);
        setAlignment(Pos.CENTER);
        setCenterShape(true);
        setPadding(new Insets(5));
        setBackground(new Background(new BackgroundFill(Color.web("#101214"), CornerRadii.EMPTY, Insets.EMPTY)));

        Tile clockTile = TileBuilder.create()
                .skinType(Tile.SkinType.CLOCK)
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .title("Clock Tile")
                .dateVisible(true)
                .locale(Locale.US)
                .running(true)
                .build();

        gaucheTemperature = TileBuilder.create()
                .skinType(Tile.SkinType.GAUGE)
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .title("Temperature")
                .unit("°C")
                .threshold(21)
                .maxValue(50)
                .build();

        percentageHumidity = TileBuilder.create()
                .skinType(Tile.SkinType.PERCENTAGE)
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .title("Humidity")
                .unit(Helper.PERCENTAGE)
                .description("% of water vapour present in the air")
                .maxValue(100)
                .build();

        gaucheDistance = TileBuilder.create()
                .skinType(Tile.SkinType.GAUGE)
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .title("Distance")
                .unit("cm")
                .maxValue(255)
                .build();

        gaucheLight = TileBuilder.create()
                .skinType(Tile.SkinType.GAUGE)
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .title("Light strength")
                .maxValue(1024)
                .build();

        getChildren().addAll(
                clockTile,
                gaucheTemperature,
                percentageHumidity,
                gaucheDistance,
                gaucheLight,
                new TiltStatusTile(client, TOPIC_TILT),
                new SensorSwitchTile(client, TOPIC_TOUCH, "Touch sensor", "Show if the sensor is touched"),
                new SensorSwitchTile(client, TOPIC_MOTION, "Motion sensor", "Show if motion is detected"),
                new NoiseTextTile(client, TOPIC_NOISE));

        client.toAsync().subscribeWith()
                .topicFilter(TOPIC_SENSORS)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(this::handleSensorData)
                .send();
    }

    private void handleSensorData(Mqtt5Publish message) {
        var sensorData = new String(message.getPayloadAsBytes());
        logger.warn("Sensor data: {}", sensorData);
        try {
            var sensor = mapper.readValue(sensorData, Sensor.class);
            gaucheTemperature.setValue(sensor.getTemperature());
            percentageHumidity.setValue(sensor.getHumidity());
            gaucheDistance.setValue(sensor.getDistance());
            gaucheLight.setValue(sensor.getLight());
        } catch (JsonProcessingException ex) {
            logger.error("Could not parse the data to JSON: {}", ex.getMessage());
        }
    }
}
