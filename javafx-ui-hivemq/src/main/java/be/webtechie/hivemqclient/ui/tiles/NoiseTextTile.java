package be.webtechie.hivemqclient.ui.tiles;

import be.webtechie.hivemqclient.model.BooleanValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.application.Platform;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static eu.hansolo.tilesfx.Tile.SkinType.TEXT;

public class NoiseTextTile extends BaseTile {

    private final Tile statusTile;

    public NoiseTextTile(Mqtt5AsyncClient client, String topic) {
        super(client, topic);

        statusTile = TileBuilder.create()
                .skinType(TEXT)
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .title("Noise sensor")
                .textVisible(true)
                .build();

        getChildren().add(statusTile);
    }

    @Override
    public void handleMessage(Mqtt5Publish message) {
        var sensorData = new String(message.getPayloadAsBytes());
        logger.warn("Noise sensor data: {}", sensorData);
        try {
            var sensor = mapper.readValue(sensorData, BooleanValue.class);
            if (sensor.getValue()) {
                Platform.runLater(() -> {
                    statusTile.setDescription("Noise detected on " +
                            ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                });
            }
        } catch (JsonProcessingException ex) {
            logger.error("Could not parse the data to JSON: {}", ex.getMessage());
        }
    }
}
