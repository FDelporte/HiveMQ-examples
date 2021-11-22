package be.webtechie.hivemqclient.ui.tiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseTile extends Pane {

    protected static final Logger logger = LogManager.getLogger(BaseTile.class.getName());

    protected final ObjectMapper mapper = new ObjectMapper();

    public static final int TILE_WIDTH = 200;
    public static final int TILE_HEIGHT = 300;

    public BaseTile(Mqtt5AsyncClient client, String topic) {
        client.toAsync().subscribeWith()
                .topicFilter(topic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(this::handleMessage)
                .send();
    }

    /**
     * Method to be overridden in each tile.
     */
    protected void handleMessage(Mqtt5Publish message) {
        logger.warn("Message not handled: {}", message.getPayloadAsBytes());
    }
}
