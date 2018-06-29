package com.github.angads25.derpinaserver.io;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.exception.GpioPinExistsException;
import org.json.JSONObject;

import java.util.WeakHashMap;

public class InputManager {
    private GpioManager gpioManager;
    private WeakHashMap<String, GpioPinDigitalInput> digitalSensors;
    private WeakHashMap<String, GpioPinListener> digitalListeners;
    private WeakHashMap<String, GpioPinListener> disabledSensors;

    private static InputManager ourInstance = new InputManager();

    public static InputManager getInstance() {
        return ourInstance;
    }

    private InputManager() {
        gpioManager = GpioManager.getInstance();
        digitalSensors = new WeakHashMap<>();
        digitalListeners = new WeakHashMap<>();
        disabledSensors = new WeakHashMap<>();
    }

    public String registerSensor(Pin pin, String name, PinPullResistance pinPullResistance) {
        if(digitalSensors.containsKey(name)) {
            return new JSONObject() {{
                put("message", "Sensor '"+ name +"' is already assigned ");
                put("response", 502);
            }}.toString();
        } else {
            try {
                GpioPinDigitalInput gpioPin = gpioManager.proviseInputPin(pin, name, pinPullResistance);

                digitalSensors.put(name, gpioPin);
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

    public String enableSensor(String name) {
        if(digitalSensors.containsKey(name)) {
            if(!digitalListeners.containsKey(name)) {
                if (disabledSensors.containsKey(name)) {
                    GpioPinListener event = disabledSensors.get(name);
                    System.out.println(setEventListener(name, event));
                    disabledSensors.remove(name);
                    return new JSONObject() {{
                        put("message", "Added event to '" + name + "' sensor.");
                        put("response", 200);
                    }}.toString();
                } else {
                    return new JSONObject() {{
                        put("message", "Sensor '" + name + "' doesn't seem to do anything.");
                        put("response", 404);
                    }}.toString();
                }
            } else {
                return new JSONObject() {{
                    put("message", "Sensor '" + name + "' is enabled already.");
                    put("response", 404);
                }}.toString();
            }
        } else {
            return new JSONObject() {{
                put("message", "There is no sensor with name: '" + name + "'");
                put("response", 404);
            }}.toString();
        }
    }

    public String disableSensor(String name) {
        if(digitalSensors.containsKey(name)) {
            if (digitalListeners.containsKey(name)) {
                if (disabledSensors.containsKey(name)) {
                    return new JSONObject() {{
                        put("message", "'" + name + "' sensor is already disabled.");
                        put("response", 400);
                    }}.toString();
                } else {
                    GpioPinListener event = digitalListeners.get(name);
                    disabledSensors.put(name, event);
                    System.out.println(removeEventListener(name));
                    return new JSONObject() {{
                        put("message", "Sensor '" + name + "' has been disabled.");
                        put("response", 200);
                    }}.toString();
                }
            } else {
                return new JSONObject() {{
                    put("message", "Sensor '" + name + "' doesn't do anything.");
                    put("response", 404);
                }}.toString();
            }
        } else {
            return new JSONObject() {{
                put("message", "There is no sensor with name: '" + name + "'");
                put("response", 404);
            }}.toString();
        }
    }

    public String setEventListener(String name, GpioPinListener gpioPinListener) {
        if(digitalListeners.containsKey(name)) {
            return new JSONObject() {{
                put("message", "Event already added to '" + name + "' sensor.");
                put("response", 404);
            }}.toString();
        } else {
            digitalSensors.get(name).addListener(gpioPinListener);
            digitalListeners.put(name, gpioPinListener);
            return new JSONObject() {{
                put("message", "Event added to '" + name + "' sensor.");
                put("response", 200);
            }}.toString();
        }
    }

    private String removeEventListener(String name) {
        if(digitalListeners.containsKey(name)) {
            digitalSensors.get(name).removeAllListeners();
            digitalListeners.remove(name);
            return new JSONObject() {{
                put("message", "Event removed from '" + name + "' sensor.");
                put("response", 404);
            }}.toString();
        } else {
            return new JSONObject() {{
                put("message", "Event already removed from '" + name + "' sensor.");
                put("response", 200);
            }}.toString();
        }
    }

    public String unregisterInputPin(String name) {
        if(!digitalSensors.containsKey(name)) {
            return new JSONObject() {{
                put("message", "No sensor with name '" + name + "' is registered.");
                put("response", 404);
            }}.toString();
        } else {
            if(digitalListeners.containsKey(name)) {
                digitalSensors.get(name).removeAllTriggers();
                digitalSensors.get(name).removeAllListeners();
            }
            gpioManager.unProvisePin(digitalSensors.get(name));
            digitalSensors.remove(name);
            disabledSensors.remove(name);
            return new JSONObject() {{
                put("message", "'" + name + "' sensor is unregistered");
                put("response", 200);
            }}.toString();
        }
    }

    public String disposeInput() {
        for(String name: digitalSensors.keySet()) {
            digitalSensors.get(name).removeAllTriggers();
            digitalSensors.get(name).removeAllListeners();
            gpioManager.unProvisePin(digitalSensors.get(name));
        }
        digitalSensors.clear();
        disabledSensors.clear();
        digitalListeners.clear();
        return new JSONObject() {{
            put("message", "All pins wiped out!");
            put("response", 200);
        }}.toString();
    }
}
