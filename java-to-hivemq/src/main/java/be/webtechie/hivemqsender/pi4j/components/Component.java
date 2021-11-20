package be.webtechie.hivemqsender.pi4j.components;

import be.webtechie.hivemqsender.pi4j.components.events.SimpleEventHandler;
import com.pi4j.context.Context;
import com.pi4j.util.Console;

public class Component {

    protected final Console console;
    protected final Context pi4j;

    public Component(Console console, Context pi4j) {
        this.console = console;
        this.pi4j = pi4j;
    }

    /**
     * Utility function to sleep for the specified amount of milliseconds.
     * An {@link InterruptedException} will be catched and ignored while setting the interrupt flag again.
     *
     * @param milliseconds Time in milliseconds to sleep
     */
    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Utility function to trigger a simple event handler.
     * If the handler is currently null it gets silently ignored.
     *
     * @param handler Event handler to call or null
     */
    protected void triggerSimpleEvent(SimpleEventHandler handler) {
        if (handler != null) {
            handler.handle();
        }
    }
}
