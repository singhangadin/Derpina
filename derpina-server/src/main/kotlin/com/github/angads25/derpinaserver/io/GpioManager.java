package com.github.angads25.derpinaserver.io;

import com.pi4j.io.gpio.*;

public class GpioManager {
    private GpioController gpio;
    private static GpioManager ourInstance = new GpioManager();

    public static GpioManager getInstance() {
        return ourInstance;
    }

    private GpioManager() {
        gpio = GpioFactory.getInstance();
    }

    public GpioPinDigitalOutput proviseOutputPin(Pin pin, String name, PinState defaultState) {
        return gpio.provisionDigitalOutputPin(pin, name, defaultState);
    }

    public GpioPinDigitalInput proviseInputPin(Pin pin, String name, PinPullResistance pinPullResistance) {
        return gpio.provisionDigitalInputPin(pin, name, pinPullResistance);
    }

    public void unProvisePin(GpioPinDigital gpioPinDigital) {
        gpio.unprovisionPin(gpioPinDigital);
    }

    public void shutdown() {
        gpio.shutdown();
    }

    @Override
    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }
}
