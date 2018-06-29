package com.github.angads25.derpinaserver.utils;

import com.github.angads25.derpinaserver.io.OutputManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimerTask;

public class TurnOffLightFanTask extends TimerTask {
    private OutputManager outputManager;

    public TurnOffLightFanTask(OutputManager outputManager) {
        super();
        this.outputManager = outputManager;
    }

    @Override
    public void run() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("Power Saver! : " + dateFormat.format(Calendar.getInstance(Locale.getDefault()).getTime()));
        System.out.println(outputManager.disableRelayPin("fan"));
        System.out.println(outputManager.disableRelayPin("tubelight"));
    }
}
