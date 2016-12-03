package de.interoberlin.tinyb_playground

import de.interoberlin.tinyb_playground.TextColor.Companion.ANSI_RESET
import parser.BleDataParser
import repository.ECharacteristic
import tinyb.*
import java.io.BufferedReader
import java.io.InputStreamReader

class TinybController
private constructor() : BluetoothNotification<ByteArray> {
    // --------------------
    // Companion object
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Companion object">

    companion object {
        private val SCAN_DURATION = 2

        private var inst: TinybController? = null

        fun getInstance(): TinybController {
            if (inst == null) {
                inst = TinybController()
            }

            return inst as TinybController
        }
    }

    // </editor-fold>

    // --------------------
    // Methods
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Methods">

    fun selectAction(): EAction {
        while (true) {
            val input = BufferedReader(InputStreamReader(System.`in`)).readLine().toLowerCase()

            if (input.length < 3) return EAction.RETRY

            when (input.substring(0, 3)) {
                "hel" -> return EAction.HELP
                "sca" -> return EAction.SCAN
                "sel" -> return EAction.SELECT_DEVICE
                "con" -> return EAction.CONNECT
                "dis" -> return EAction.DISCONNECT
                "pai" -> return EAction.PAIR
                "fin" -> return EAction.FIND_SERVICE
                "sho" -> return EAction.SHOW_SERVICES
                "rea" -> return EAction.READ_CHARACTERISTIC
                "wri" -> return EAction.WRITE_CHARACTERISTIC
                "sub" -> return EAction.SUBSCRIBE_CHARACTERISTIC
                "uns" -> return EAction.UNSUBSCRIBE_CHARACTERISTIC
                "qui" -> return EAction.QUIT
                else -> return EAction.RETRY
            }
        }
    }

    fun showHelp() {
        println("  [Hel]p")
        println("  [Sca]n device")
        println("  [Sel]ect device")
        println("  [Con]nect")
        println("  [Dis]connect")
        println("  [Pai]r")
        println("  [Fin]d service")
        println("  [Sho]w services")
        println("  [Rea]d characteristic")
        println("  [Wri]te characteristic")
        println("  [Sub]scribe characteristic")
        println("  [Uns]ubscribe characteristic")
        println("  [Q]uit")
    }

    @Throws(InterruptedException::class)
    fun scanDevices(): List<BluetoothDevice> {
        val manager = BluetoothManager.getBluetoothManager()
        try {
            manager.startDiscovery()
        } catch(e: BluetoothException) {
            println(TextColor.ERROR + "$e")
            return emptyList()
        }

        println(TextColor.INFO + "Start scan")
        print("      ")

        for (i in 0..SCAN_DURATION - 1) {
            print(".")
            Thread.sleep(1000)
        }

        try {
            manager.stopDiscovery()
        } catch (e: BluetoothException) {
            println(TextColor.ERROR + "Discovery could not be stopped.")
        }
        println("")

        showDevices(manager.devices)
        return manager.devices
    }

    fun showDevices(devices: List<BluetoothDevice>) {
        println(TextColor.INFO + "Show devices")
        var i = 0
        for (device in devices) {
            println("      " + if (device.connected) TextColor.ANSI_GREEN else TextColor.ANSI_RED + "# ${++i} $ANSI_RESET ${device.address} ${device.name} ${device.connected}")
        }
    }

    fun selectDevice(devices: List<BluetoothDevice>): BluetoothDevice {
        println(TextColor.INFO + "Select a device")

        val input = BufferedReader(InputStreamReader(System.`in`)).readLine()
        var pickedDeviceID: Int

        while (true) {
            try {
                pickedDeviceID = Integer.parseInt(input)
            } catch (e: NumberFormatException) {
                println(TextColor.ERROR + "'$input' is not a number")
                continue
            }

            if (pickedDeviceID > devices.size) {
                println(TextColor.ERROR + "'$pickedDeviceID' is not in range")
                continue
            }

            return devices[pickedDeviceID - 1]
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods connection">

    fun connectDevice(device: BluetoothDevice) {
        println(TextColor.INFO + "Connect device ${device.address} ${device.name}")
        try {
            if (device.connect()) {
                println(TextColor.DEBUG + "Paired : ${device.paired}")
                println(TextColor.DEBUG + "Trusted: ${device.trusted}")
            } else {
                println(TextColor.ERROR + "Could not connect device")
                System.exit(-1)
            }
        } catch(e: BluetoothException) {
            println(TextColor.ERROR + "$e")
        }
    }

    fun disconnectDevice(device: BluetoothDevice) {
        println(TextColor.INFO + "Disconnect device ${device.address} ${device.name}")
        if (!device.disconnect()) {
            println(TextColor.ERROR + "Could not disconnect device")
        }
    }

    fun pairDevice(device: BluetoothDevice) {
        println(TextColor.INFO + "Pair device ${device.address} ${device.name}")
        ensureConnection(device)
        device.pair()
    }

    fun findService(device: BluetoothDevice) {
        println(TextColor.INFO + "Find service")
        val br = BufferedReader(InputStreamReader(System.`in`))
        val input = br.readLine()

        device.find(input)
    }

    fun showServices(device: BluetoothDevice) {
        println(TextColor.INFO + "Show service")

        var bluetoothServices: List<BluetoothGattService>

        for (i in 1..10) {
            ensureConnection(device)

            bluetoothServices = device.services

            for (service in bluetoothServices) {
                println("     Service " + service.uuid)
                for (characteristic in service.characteristics) {
                    println("      Characteristic ${TextColor.ANSI_CYAN} characteristic.uuid + ${TextColor.ANSI_RESET}")
                }
            }
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods communication">

    fun readCharacteristic(device: BluetoothDevice) {
        println("$TextColor.INPUT Select characteristic to read")

        val br = BufferedReader(InputStreamReader(System.`in`))
        val input = br.readLine()

        for (service in device.services) {
            for (characteristic in service.characteristics) {
                if (input == characteristic.uuid) {
                    ensureConnection(device)

                    val batteryLevel = BleDataParser.getFormattedValue(null, ECharacteristic.BATTERY_LEVEL, characteristic.readValue())
                    print("Characteristic $characteristic.uuid : $TextColor.ANSI_YELLOW$batteryLevel$TextColor.ANSI_RESET")
                    return
                }
            }
        }

        println(TextColor.ERROR + "Characteristic with UUID $input not found")
    }

    fun writeCharacteristic(device: BluetoothDevice) {
        println("$TextColor.INPUT Select characteristic to write")

        val br = BufferedReader(InputStreamReader(System.`in`))
        val input = br.readLine()

        for (service in device.services) {
            for (characteristic in service.characteristics) {
                if (characteristic.uuid == input || characteristic.uuid.contains(input)) {
                    writeCharacteristic(device, characteristic, byteArrayOf(0x01))
                }
            }
        }

        println(TextColor.ERROR + "Characteristic with UUID $input not found")
    }

    /**
     * Write an array of @param bytes into the specified @param characteristic of a @param device
     */
    fun writeCharacteristic(device: BluetoothDevice, characteristic: BluetoothGattCharacteristic, bytes: ByteArray) {
        ensureConnection(device)
        try {
            val success = characteristic.writeValue(bytes)
            println(if (success) "Characteristic ${characteristic.uuid} :" + TextColor.ANSI_YELLOW + "PING" + TextColor.ANSI_RESET else "Write failed")
        } catch(e: BluetoothException) {
            println(TextColor.ERROR + "$e")
        }
    }

    fun subscribeCharacteristic(device: BluetoothDevice): BluetoothGattCharacteristic? {
        println("$TextColor.INPUT Select characteristic to subscribe")

        val br = BufferedReader(InputStreamReader(System.`in`))
        val input = br.readLine()

        println("$TextColor.DEBUG services ${device.services.size}")
        for (service in device.services) {
            println("$TextColor.DEBUG   characteristic ${service.characteristics.size}")
            for (characteristic in service.characteristics) {
                if (characteristic.uuid == input || characteristic.uuid.contains(input)) {
                    ensureConnection(device)

                    characteristic.enableValueNotifications(this)
                    device.enableConnectedNotifications({
                        println(TextColor.INFO + "Connected because of subscription")
                    })

                    println(TextColor.INFO + "Characteristic ${characteristic.uuid} subscribed")
                    return characteristic
                }
            }
        }

        println(TextColor.ERROR + "Characteristic with UUID $input not found")
        return null
    }

    fun unsubscribeCharacteristic(device: BluetoothDevice, characteristic: BluetoothGattCharacteristic) {
        for (s in device.services) {
            for (c in s.characteristics) {
                if (c.uuid == characteristic.uuid) {
                    characteristic.disableValueNotifications()
                    println("$TextColor.INPUT Characteristic ${characteristic.uuid} ${TextColor.ANSI_YELLOW}unsubscribed$TextColor.ANSI_RESET")
                }
            }
        }
    }

    fun ensureConnection(device: BluetoothDevice) {
        try {
            while (!device.connected) {
                device.connect()
                if (!device.connected) {
                    print(".")
                    Thread.sleep(1000)
                } else {
                    println(TextColor.INFO + "Connected")
                    break
                }
            }
        } catch(e: BluetoothException) {
            println(TextColor.ERROR + "$e")
        }
    }

    // </editor-fold>

    // --------------------
    // Callback interfaces
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Callback interfaces">

    override fun run(value: ByteArray?) {
        println(TextColor.INFO + "PING")
    }

// </editor-fold>
}