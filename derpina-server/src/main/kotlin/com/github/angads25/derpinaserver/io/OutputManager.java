package com.github.angads25.derpinaserver.io;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.exception.GpioPinExistsException;
import org.json.JSONObject;

import java.util.WeakHashMap;

public class OutputManager {
    private GpioManager gpioManager;
    private WeakHashMap<String, GpioPinDigitalOutput> outputPins;

    private static OutputManager ourInstance = new OutputManager();

    public static OutputManager getInstance() {
        return ourInstance;
    }

    private OutputManager() {
        gpioManager = GpioManager.getInstance();
        outputPins = new WeakHashMap<>();
    }

    public String registerOutputPin(Pin pin, String name, PinState pinState) {
        if(outputPins.containsKey(name)) {
            return new JSONObject() {{
                put("message", "Name '"+ name +"' is already assigned to another switch");
                put("response", 502);
            }}.toString();
        } else {
            try {
                GpioPinDigitalOutput gpioPin = gpioManager.proviseOutputPin(pin, name, pinState);
                outputPins.put(name, gpioPin);
                return new JSONObject() {{
                    put("message", "'" + name + "' pin is registered");
                    put("response", 200);
                }}.toString();
            } catch(GpioPinExistsException e) {
                return new JSONObject() {{
                    put("message", "'" + pin.getName() + "' pin is already assigned to another name");
                    put("response", 502);
                    put("error", e.toString());
                }}.toString();
            }
        }
    }

    public String updateOutputPin(Pin pin, String name, PinState pinState) {
        if(outputPins.containsKey(name)) {
            GpioPinDigitalOutput gpioPin = outputPins.get(name);
            if(pin == gpioPin.getPin()) {
                // Change only pinstate
                gpioPin.setState(pinState);
            } else {
                // Remove old gpio pin, register new one
                gpioManager.unProvisePin(outputPins.get(name));
                outputPins.remove(name);
                registerOutputPin(pin, name, pinState);
            }
            return new JSONObject() {{
                put("message", "'" + name + "' pin is updated");
                put("response", 200);
            }}.toString();
        } else {
            return new JSONObject() {{
                put("message", "No component with name '" + name + "'");
                put("response", 404);
            }}.toString();
        }
    }

    public String updateOutputPinName(String oldName, String newName) {
        if(outputPins.containsKey(oldName)) {
            outputPins.put(newName, outputPins.remove(oldName));
            return new JSONObject() {{
                put("message", "'" + newName + "' pin is updated");
                put("response", 200);
            }}.toString();
        } else {
            return new JSONObject() {{
                put("message", "No component with name '" + oldName + "' is registered.");
                put("response", 404);
            }}.toString();
        }
    }

    public String enableRelayPin(String name) {
        if(!outputPins.containsKey(name)) {
            return new JSONObject() {{
                put("message", "No component with name '" + name + "' is registered.");
                put("response", 404);
            }}.toString();
        } else {
            if(outputPins.get(name).isLow()) {
                return new JSONObject() {{
                    put("message", "'" + name + "' pin is already enabled");
                    put("response", 200);
                }}.toString();
            } else {
                outputPins.get(name).low();
                return new JSONObject() {{
                    put("message", "'" + name + "' pin is enabled");
                    put("response", 200);
                }}.toString();
            }
        }
    }

    public String disableRelayPin(String name) {
        if(!outputPins.containsKey(name)) {
            return new JSONObject() {{
                put("message", "No component with name '" + name + "' is registered.");
                put("response", 404);
            }}.toString();
        } else {
            if(outputPins.get(name).isHigh()) {
                return new JSONObject() {{
                    put("message", "'" + name + "' pin is already disabled");
                    put("response", 200);
                }}.toString();
            } else {
                outputPins.get(name).high();
                return new JSONObject() {{
                    put("message", "'" + name + "' pin is disabled");
                    put("response", 200);
                }}.toString();
            }
        }
    }

    public String getPinState(String name) {
        if(!outputPins.containsKey(name)) {
            return new JSONObject() {{
                put("message", "No component with name '" + name + "' is registered.");
                put("response", 404);
            }}.toString();
        } else {
            return new JSONObject() {{
                put("message", "The component '"+ name +"' is " + (outputPins.get(name).isHigh()? "OFF":"ON"));
                put("state", (outputPins.get(name).isHigh()? "OFF":"ON"));
                put("response", 200);
            }}.toString();
        }
    }

    public String unregisterOutputPin(String name) {
        if(!outputPins.containsKey(name)) {
            return new JSONObject() {{
                put("message", "No component with name '" + name + "' is registered.");
                put("response", 404);
            }}.toString();
        } else {
            disableRelayPin(name);
            gpioManager.unProvisePin(outputPins.get(name));
            outputPins.remove(name);
            return new JSONObject() {{
                put("message", "'" + name + "' pin is unregistered");
                put("response", 200);
            }}.toString();
        }
    }

    public String disposeOutput() {
        for(String name: outputPins.keySet()) {
            outputPins.get(name).high();
            gpioManager.unProvisePin(outputPins.get(name));
        }
        outputPins.clear();
        return new JSONObject() {{
            put("message", "All pins wiped out!");
            put("response", 200);
        }}.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        disposeOutput();
        super.finalize();
    }
}
