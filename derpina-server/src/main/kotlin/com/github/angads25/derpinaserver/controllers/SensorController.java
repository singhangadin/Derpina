package com.github.angads25.derpinaserver.controllers;

import com.github.angads25.derpinaserver.io.InputManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    @GetMapping("/{name}/{state}")
    public String testSwitch(@PathVariable String name, @PathVariable String state) {
        InputManager inputManager = InputManager.getInstance();
        if(state.equals("on")) {
            String response = inputManager.enableSensor(name);
            System.out.println(response);
            return response;
        }
        else {
            String response = inputManager.disableSensor(name);
            System.out.println(response);
            return response;
        }
    }
}
