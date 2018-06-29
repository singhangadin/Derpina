package com.github.angads25.derpinaserver.controllers;

import com.github.angads25.derpinaserver.io.OutputManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/state")
public class StateController {

    @GetMapping("/{name}")
    public String getPinState(@PathVariable String name) {
        OutputManager manager = OutputManager.getInstance();
        return manager.getPinState(name);
    }
}
