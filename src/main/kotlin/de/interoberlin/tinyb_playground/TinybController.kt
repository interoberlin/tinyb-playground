package de.interoberlin.tinyb_playground

import parser.BleDataParser
import repository.ECharacteristic
import repository.EDevice
import tinyb.*

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class TinybController
private constructor() {

    // --------------------
    // Methods
    // --------------------


    @Throws(InterruptedException::class)
    fun scanDevices(manager: BluetoothManager): List<BluetoothDevice> {
        manager.startDiscovery()
        println("Start scan")

        for (i in 0..SCAN_DURATION - 1) {
            print(".")
            Thread.sleep(1000)
        }

        try {
            manager.stopDiscovery()
        } catch (e: BluetoothException) {
            System.err.println("Discovery could not be stopped.")
        }

        println("")
        return manager.devices
    }

    fun selectDevice(devices: List<BluetoothDevice>): BluetoothDevice {
        println("\nSelect a device")
        val br = BufferedReader(InputStreamReader(System.`in`))
        var pickedDeviceID: Int

        while (true) {
            try {
                pickedDeviceID = Integer.parseInt(br.readLine())
            } catch (e: NumberFormatException) {
                println(".. not a number")
                continue
            }

            if (pickedDeviceID > devices.size) {
                println(".. not in range")
                continue
            }

            return devices[pickedDeviceID - 1]
        }
    }

    fun selectAction(): EAction {
        println("\nSelect an action")
        println("  [C]onnect")
        println("  [D]isconnect")
        println("  [P]air")
        println("  [F]ind service")
        println("  [S]how services")
        println("  [R]ead characteristic")
        println("  [Q]uit")
        val br = BufferedReader(InputStreamReader(System.`in`))

        while (true) {
            val input = br.readLine().toLowerCase().substring(0, 1)

            when (input) {
                "c" -> return EAction.CONNECT
                "d" -> return EAction.DISCONNECT
                "p" -> return EAction.PAIR
                "f" -> return EAction.FIND_SERVICE
                "s" -> return EAction.SHOW_SERVICES
                "r" -> return EAction.READ_CHARACTERISTIC
                "q" -> return EAction.QUIT
            }
        }
    }

    fun connectDevice(device: BluetoothDevice) {
        print("\nConnect device " + ANSI_GREEN + device.address + ANSI_RESET + " " + device.name)
        if (device.connect()) {
            println(".. sensor with the provided address connected")
            println("paired  : " + device.paired)
            println("trusted : " + device.trusted)
        } else {
            println(".. could not connect device")
            System.exit(-1)
        }
    }

    fun disconnectDevice(device: BluetoothDevice) {
        print("\n" + ANSI_RED + "Dis" + ANSI_RESET + "connect device " + ANSI_GREEN + device.address + ANSI_RESET + " " + device.name)
        if (device.disconnect())
            println(".. sensor with the provided address " + ANSI_RED + "dis" + ANSI_RESET + "connected")
        else {
            println(".. could not disconnect device")
            System.exit(-1)
        }
    }

    fun pairDevice(device: BluetoothDevice) {
        device.pair()
    }

    fun findService(device: BluetoothDevice) {
        val br = BufferedReader(InputStreamReader(System.`in`))
        val input = br.readLine()

        device.find(input)
    }

    fun showDevices(devices: List<BluetoothDevice>) {
        var i = 0
        for (device in devices) {
            if (device.connected) {
                println(ANSI_GREEN + "#" + ++i + ANSI_RESET + " " + device.address + " " + device.name + " " + device.connected)
            } else {
                println(ANSI_RED + "#" + ++i + ANSI_RESET + " " + device.address + " " + device.name + " " + device.connected)
            }
        }
    }

    fun showServices(device: BluetoothDevice) {
        println("Wait for services")

        var bluetoothServices: List<BluetoothGattService>

        for (i in 1..10) {
            bluetoothServices = device.services

            if (bluetoothServices.isEmpty()) {
                print(".")
                Thread.sleep(1000)
            } else {
                for (service in bluetoothServices) {
                    println("Service " + service.uuid)
                    for (characteristic in service.characteristics) {
                        println("Characteristic " + ANSI_PURPLE + characteristic.uuid + ANSI_RESET)
                    }
                }

                break;
            }
        }
    }

    fun readCharacteristic(device: BluetoothDevice) {
        println("Select characteristic to read")

        val br = BufferedReader(InputStreamReader(System.`in`))
        val input = br.readLine()

        for (service in device.services) {
            for (characteristic in service.characteristics) {
                if (input == characteristic.uuid) {
                    val batteryLevel = BleDataParser.getFormattedValue(null, ECharacteristic.BATTERY_LEVEL, characteristic.readValue())
                    print("Characteristic ${characteristic.uuid} : ${ANSI_YELLOW}${batteryLevel}${ANSI_RESET}")
                    return
                }
            }
        }

        println("Characteristic with UUID ${input} not found")
    }

    companion object {
        private val SCAN_DURATION = 5

        private val ANSI_RESET = "\u001B[0m"
        // private static final String ANSI_BLACK = "\u001B[30m";
        private val ANSI_RED = "\u001B[31m"
        private val ANSI_GREEN = "\u001B[32m"
        private val ANSI_YELLOW = "\u001B[33m";
        // private static final String ANSI_BLUE = "\u001B[34m";
        private val ANSI_PURPLE = "\u001B[35m"
        // private static final String ANSI_CYAN = "\u001B[36m";
        // private static final String ANSI_WHITE = "\u001B[37m";

        private var inst: TinybController? = null

        fun getInstance(): TinybController {
            if (inst == null) {
                inst = TinybController()
            }

            return inst as TinybController
        }
    }
}