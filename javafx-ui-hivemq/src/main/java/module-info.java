module be.webtechie.hivemqclient {
    requires javafx.graphics;
    requires javafx.controls;
    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.core;
    requires com.hivemq.client.mqtt;
    requires com.fasterxml.jackson.databind;
    requires eu.hansolo.tilesfx;

    exports be.webtechie.hivemqclient to javafx.graphics;
    exports be.webtechie.hivemqclient.model to com.fasterxml.jackson.databind;
}