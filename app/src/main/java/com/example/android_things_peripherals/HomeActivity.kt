package com.example.android_things_peripherals

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import java.io.IOException

private const val TAG = "HomeActivity"
private const val BUTTON_PIN_NAME = "GPIO6_IO14"
private const val BUTTON_LED_PIN_NAME = "GPIO2_IO02"

class HomeActivity : AppCompatActivity() {

    private lateinit var buttonInputDriver: ButtonInputDriver
    private lateinit var ledGpio: Gpio

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            setLedValue(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            setLedValue(false)
            return false
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val pioManager = PeripheralManager.getInstance()
        Log.d(TAG, "Available GPIO: ${pioManager.gpioList}")

        try {
            buttonInputDriver = ButtonInputDriver(
                BUTTON_PIN_NAME,
                Button.LogicState.PRESSED_WHEN_LOW,
                KeyEvent.KEYCODE_SPACE
            )
            buttonInputDriver.register()
            setupLedButton(pioManager)
        } catch (e: IOException) {
            Log.w(TAG, "Error opening GPIO", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        buttonInputDriver.unregister()
        try {
            buttonInputDriver.close()
//            aButtonGpio.close()
            ledGpio.close()
        } catch (e: IOException) {
            Log.w(TAG, "Error closing GPIO", e)
        }
    }

    private fun setLedValue(value: Boolean) {
        try {
          ledGpio.value = value
        } catch(e: IOException) {
            Log.e(TAG, "Error updated GPIO value", e)
        }
    }

    private fun setupLedButton(pioManager: PeripheralManager) {
        ledGpio = pioManager.openGpio(BUTTON_LED_PIN_NAME)
        //configure as an output
        ledGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
    }
}
