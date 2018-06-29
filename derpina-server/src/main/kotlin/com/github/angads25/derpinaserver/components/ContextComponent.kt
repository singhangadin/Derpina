package com.github.angads25.derpinaserver.components

import com.github.angads25.derpinaserver.io.GpioManager
import com.github.angads25.derpinaserver.io.InputManager
import com.github.angads25.derpinaserver.io.OutputManager
import com.github.angads25.derpinaserver.utils.TurnOffLightFanTask
import com.pi4j.io.gpio.PinPullResistance
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.RaspiPin
import com.pi4j.io.gpio.event.GpioPinListenerDigital
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener



@Component
class ContextComponent : ServletContextListener {
    private lateinit var timer : Timer
    private lateinit var timerTask : TurnOffLightFanTask

    override fun contextInitialized(p0: ServletContextEvent?) {
        println("Context Started")

        timer = Timer(true)

        val outputManager = OutputManager.getInstance()
        println(outputManager.registerOutputPin(RaspiPin.GPIO_02, "tubelight", PinState.HIGH))
        println(outputManager.registerOutputPin(RaspiPin.GPIO_04, "plug", PinState.HIGH))
        println(outputManager.registerOutputPin(RaspiPin.GPIO_05, "fan", PinState.HIGH))

        val inputManager = InputManager.getInstance()
        println(inputManager.registerSensor(RaspiPin.GPIO_10, "motion", PinPullResistance.PULL_DOWN))
        inputManager.setEventListener("motion", GpioPinListenerDigital { event ->
            val now = Calendar.getInstance(Locale.getDefault())
            val hourOfDay = now.get(Calendar.HOUR_OF_DAY)
            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            if(event?.state?.isHigh()!!) {
                System.out.println("Motion Detected! : " + dateFormat.format(Calendar.getInstance(Locale.getDefault()).time))
                if(hourOfDay >= 19) {
                    System.out.println(outputManager.enableRelayPin("tubelight"))
                }
                System.out.println(outputManager.enableRelayPin("fan"))
                timer.cancel()
                timer.purge()
            }

            if(event.state.isLow) {
                System.out.println("All is quiet! : "  + dateFormat.format(Calendar.getInstance(Locale.getDefault()).time))
                timer = Timer()
                timerTask = TurnOffLightFanTask(outputManager)
                timer.schedule(timerTask, 60000)
            }
        })
    }

    override fun contextDestroyed(p0: ServletContextEvent?) {
        val inputManager = InputManager.getInstance()
        val outputManager = OutputManager.getInstance()

        println(inputManager.disposeInput())
        println(outputManager.disposeOutput())

        val gpioManager = GpioManager.getInstance()
        gpioManager.shutdown()

        timerTask.cancel()
        timer.cancel()
        timer.purge()
        println("Context Stopped")
    }
}
