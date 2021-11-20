module be.webtechie.hivemqsender {
    // Pi4J MODULES
    requires com.pi4j;
    requires com.pi4j.library.pigpio;
    requires com.pi4j.plugin.pigpio;
    requires com.pi4j.plugin.raspberrypi;

    // SLF4J MODULES
    requires org.slf4j;
    requires org.slf4j.simple;

    // MESSAGING MODULES
    requires jdk.unsupported;
    requires com.hivemq.client.mqtt;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    uses com.pi4j.extension.Extension;
    uses com.pi4j.provider.Provider;

    // Allow access to classes in the following namespaces for Pi4J annotation processing
    opens be.webtechie.hivemqsender to com.pi4j;

    // Allow access to model for Jackson parsing
    opens be.webtechie.hivemqsender.model to com.fasterxml.jackson.databind;
}