package be.webtechie.hivemqsender.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SensorTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private final String json = "{\"temperature\":123.45,\"humidity\":234.56,\"motionDetected\":true}";

    @Test
    void toJson() throws JsonProcessingException {
        var sensor = new Sensor(123.45, 234.56, 345.56, 456.78);
        assertEquals(json, mapper.writeValueAsString(sensor));
    }

    @Test
    void toObject() throws JsonProcessingException {
        var sensor = mapper.readValue(json, Sensor.class);
        assertAll(
                () -> assertEquals(123.45, sensor.getTemperature()),
                () -> assertEquals(234.56, sensor.getHumidity())
        );
    }
}