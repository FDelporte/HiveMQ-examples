package be.webtechie.hivemqclient.ui.tiles;

import be.webtechie.hivemqclient.model.StringValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.application.Platform;

public class TiltStatusTile extends BaseTile {

    private final Indicator leftGraphics;
    private final Indicator shakingGraphics;
    private final Indicator rightGraphics;
    private final Tile statusTile;

    public TiltStatusTile(Mqtt5AsyncClient client, String topic) {
        super(client, topic);

        leftGraphics = new Indicator(Tile.RED);
        leftGraphics.setOn(false);

        shakingGraphics = new Indicator(Tile.YELLOW);
        shakingGraphics.setOn(false);

        rightGraphics = new Indicator(Tile.GREEN);
        rightGraphics.setOn(false);

        statusTile = TileBuilder.create()
                .skinType(SkinType.STATUS)
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .title("Tilt")
                .description("Shows how the CrowPi is tilted")
                .leftText("LEFT")
                .middleText("SHAKING")
                .rightText("RIGHT")
                .leftGraphics(leftGraphics)
                .middleGraphics(shakingGraphics)
                .rightGraphics(rightGraphics)
                .build();

        getChildren().add(statusTile);
    }

    @Override
    public void handleMessage(Mqtt5Publish message) {
        var sensorData = new String(message.getPayloadAsBytes());
        logger.warn("Tilt sensor data: {}", sensorData);
        try {
            var sensor = mapper.readValue(sensorData, StringValue.class);
            if (sensor.getValue().equalsIgnoreCase("left")) {
                setValues(true, false, false);
            } else if (sensor.getValue().equalsIgnoreCase("right")) {
                setValues(false, true, false);
            } else if (sensor.getValue().equalsIgnoreCase("shaking")) {
                setValues(false, false, true);
            }

        } catch (JsonProcessingException ex) {
            logger.error("Could not parse the data to JSON: {}", ex.getMessage());
        }
    }

    private void setValues(boolean left, boolean right, boolean shaking) {
        Platform.runLater(() -> {
            if (left) {
                statusTile.setLeftValue(statusTile.getLeftValue() + 1);
            }
            if (right) {
                statusTile.setRightValue(statusTile.getRightValue() + 1);
            }
            if (shaking) {
                statusTile.setMiddleValue(statusTile.getMiddleValue() + 1);
            }
            leftGraphics.setOn(left);
            rightGraphics.setOn(right);
            shakingGraphics.setOn(shaking);
        });
    }
}
