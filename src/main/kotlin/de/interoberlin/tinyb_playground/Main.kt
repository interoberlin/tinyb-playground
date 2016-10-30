package de.interoberlin.tinyb_playground

import tinyb.BluetoothDevice
import tinyb.BluetoothManager

object Main {
    @JvmStatic fun main(args: Array<String>) {
        val tinybController = TinybController.getInstance()
        val manager = BluetoothManager.getBluetoothManager()

        // Scan devices

        var devices = tinybController.scanDevices(manager)

        tinybController.showDevices(devices)

        val action = tinybController.selectAction()
        var device = tinybController.selectDevice(devices)

        when (action) {
            EAction.CONNECT -> tinybController.connectDevice(device)
            EAction.DISCONNECT -> tinybController.disconnectDevice(device)
        }

        tinybController.showDevices(devices)
        tinybController.showServices(device)
    }
}
