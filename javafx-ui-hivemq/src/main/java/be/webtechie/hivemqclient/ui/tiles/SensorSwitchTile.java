package be.webtechie.hivemqclient.ui.tiles;

import be.webtechie.hivemqclient.model.BooleanValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.application.Platform;

public class SensorSwitchTile extends BaseTile {

    private final Tile statusTile;

    public SensorSwitchTile(Mqtt5AsyncClient client, String topic, String title, String description) {
        super(client, topic);

        statusTile = TileBuilder.create()
                .skinType(SkinType.SWITCH)
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .title(title)
                .description(description)
                .build();

        getChildren().add(statusTile);
    }

    @Override
    public void handleMessage(Mqtt5Publish message) {
        var sensorData = new String(message.getPayloadAsBytes());
        logger.warn("Tilt sensor data: {}", sensorData);
        try {
            var sensor = mapper.readValue(sensorData, BooleanValue.class);
            Platform.runLater(() -> {
                statusTile.setActive(sensor.getValue());
            });
        } catch (JsonProcessingException ex) {
            logger.error("Could not parse the data to JSON: {}", ex.getMessage());
        }
    }
}
