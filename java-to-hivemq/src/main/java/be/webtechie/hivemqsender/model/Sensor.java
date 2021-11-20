package be.webtechie.hivemqsender.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sensor {

    private Double temperature;
    private Double humidity;
    private Double light;
    private Double distance;

    public Sensor() {
        // For JSON mapping
    }

    public Sensor(Double temperature, Double humidity, Double light, Double distance) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.light = light;
        this.distance = distance;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getLight() {
        return light;
    }

    public void setLight(Double light) {
        this.light = light;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception ex) {
            System.err.println("Could not parse to JSON: " + ex.getMessage());
        }
        return "";
    }
}
