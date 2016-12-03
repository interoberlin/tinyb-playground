package de.interoberlin.tinyb_playground

import java.util.ArrayList

enum class EService
constructor(
        var id: String?, var description: String?) {

    GENERIC_ACCESS("00001800-0000-1000-8000-00805f9b34fb", "Generic access"),
    GENERIC_ATTRIBUTE("00001801-0000-1000-8000-00805f9b34fb", "Generic attribute"),
    DEVICE_INFORMATION("0000180a-0000-1000-8000-00805f9b34fb", "Device information"),
    BATTERY_LEVEL("0000180f-0000-1000-8000-00805f9b34fb", "Battery level"),

    CONNECTED_TO_MASTER_MODULE("00002000-0000-1000-8000-00805f9b34fb", "Connected to master module"),
    DIRECT_CONNECTION("00002002-0000-1000-8000-00805f9b34fb", "Direct connection"),

    HEART_RATE("0000180d-0000-1000-8000-00805f9b34fb", "Heart rate"),

    INTEROBERLIN_UART("00002001-0000-1000-8000-00805f9b34fb", "Interoberlin UART"),
    SENTIENT_LIGHT_FLOOR_SENSOR("00003001-0000-1000-8000-00805f9b34fb", "Sentient light floor sensor"),
    SENTIENT_LIGHT_LED("00004001-0000-1000-8000-00805f9b34fb", "Sentient Light LED");

    val characteristics: List<ECharacteristic>
        get() {
            val characteristics = ArrayList<ECharacteristic>()

            for (e in ECharacteristic.values()) {
                if (e.service == this) {
                    characteristics.add(e)
                }
            }

            return characteristics
        }

    companion object {
        // --------------------
        // Methods
        // --------------------

        // <editor-fold defaultstate="collapsed" desc="Methods">

        fun fromId(id: String): EService? {
            for (s in EService.values()) {
                if (s.id == id)
                    return s
            }

            return null
        }

        // </editor-fold>
    }
}
