package com.github.angads25.derpinaserver.controllers;

import com.github.angads25.derpinaserver.io.OutputManager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/control")
public class ComponentController {

    @GetMapping("/{name}/{state}")
    public String testSwitch(@PathVariable String name, @PathVariable String state) {
        OutputManager manager = OutputManager.getInstance();
        if(state.equals("on")) {
            return manager.enableRelayPin(name);
        }
        else {
            return manager.disableRelayPin(name);
        }
    }
}
