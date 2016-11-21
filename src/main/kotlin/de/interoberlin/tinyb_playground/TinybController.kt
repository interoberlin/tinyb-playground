package de.interoberlin.tinyb_playground

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
        private val SCAN_DURATION = 5

        private val ANSI_RESET = "\u001B[0m"
        private val ANSI_RED = "\u001B[31m"
        private val ANSI_GREEN = "\u001B[32m"
        private val ANSI_YELLOW = "\u001B[33m"
        private val ANSI_PURPLE = "\u001B[35m"
        private val ANSI_CYAN = "\u001B[36m"

        private val DEBUG = "${ANSI_CYAN}DEBUG$ANSI_RESET"
        private val INFO = "$ANSI_GREEN INFO$ANSI_RESET"
        // private val WARN = "$ANSI_YELLOW WARN$ANSI_RESET"
        private val ERROR = "${ANSI_RED}ERROR$ANSI_RESET"
        private val INPUT = "${ANSI_PURPLE}INPUT$ANSI_RESET"

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
        manager.startDiscovery()
        println("$INFO Start scan")
        print("      ")

        for (i in 0..SCAN_DURATION - 1) {
            print(".")
            Thread.sleep(1000)
        }

        try {
            manager.stopDiscovery()
        } catch (e: BluetoothException) {
            println("$ERROR Discovery could not be stopped.")
        }
        println("")

        showDevices(manager.devices)
        return manager.devices
    }

    fun showDevices(devices: List<BluetoothDevice>) {
        println("$INFO Show devices")
        var i = 0
        for (device in devices) {
            println("      " + if (device.connected) ANSI_GREEN else ANSI_RED + "# ${++i} $ANSI_RESET ${device.address} ${device.name} ${device.connected}")
        }
    }

    fun selectDevice(devices: List<BluetoothDevice>): BluetoothDevice {
        println("$INFO Select a device")

        val input = BufferedReader(InputStreamReader(System.`in`)).readLine()
        var pickedDeviceID: Int

        while (true) {
            try {
                pickedDeviceID = Integer.parseInt(input)
            } catch (e: NumberFormatException) {
                println("$ERROR '$input' is not a number")
                continue
            }

            if (pickedDeviceID > devices.size) {
                println("$ERROR '$pickedDeviceID' is not in range")
                continue
            }

            return devices[pickedDeviceID - 1]
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods connection">

    fun connectDevice(device: BluetoothDevice) {
        println("$INFO Connect device ${device.address} ${device.name}")
        if (device.connect()) {
            println("$DEBUG Paired : ${device.paired}")
            println("$DEBUG Trusted: ${device.trusted}")
        } else {
            println("$ERROR Could not connect device")
            System.exit(-1)
        }
    }

    fun disconnectDevice(device: BluetoothDevice) {
        println("$INFO Disconnect device ${device.address} ${device.name}")
        if (!device.disconnect()) {
            println("$ERROR Could not disconnect device")
        }
    }

    fun pairDevice(device: BluetoothDevice) {
        println("$INFO Pair device ${device.address} ${device.name}")
        device.pair()
    }

    fun findService(device: BluetoothDevice) {
        println("$INFO Find service")
        val br = BufferedReader(InputStreamReader(System.`in`))
        val input = br.readLine()

        device.find(input)
    }

    fun showServices(device: BluetoothDevice) {
        println("$INFO Show service")

        var bluetoothServices: List<BluetoothGattService>

        for (i in 1..10) {
            if (!device.connected) device.connect()
            bluetoothServices = device.services

            for (service in bluetoothServices) {
                println("     Service " + service.uuid)
                for (characteristic in service.characteristics) {
                    println("      Characteristic " + ANSI_CYAN + characteristic.uuid + ANSI_RESET)
                }
            }
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods communication">

    fun readCharacteristic(device: BluetoothDevice) {
        println("$INPUT Select characteristic to read")

        val br = BufferedReader(InputStreamReader(System.`in`))
        val input = br.readLine()

        for (service in device.services) {
            for (characteristic in service.characteristics) {
                if (input == characteristic.uuid) {
                    if (!device.connected) device.connect()
                    val batteryLevel = BleDataParser.getFormattedValue(null, ECharacteristic.BATTERY_LEVEL, characteristic.readValue())
                    print("Characteristic $characteristic.uuid : $ANSI_YELLOW$batteryLevel$ANSI_RESET")
                    return
                }
            }
        }

        println("$ERROR Characteristic with UUID $input not found")
    }

    fun writeCharacteristic(device: BluetoothDevice) {
        println("$INPUT Select characteristic to write")

        val br = BufferedReader(InputStreamReader(System.`in`))
        val input = br.readLine()

        for (service in device.services) {
            for (characteristic in service.characteristics) {
                if (characteristic.uuid == input || characteristic.uuid.contains(input)) {
                    if (!device.connected) device.connect()
                    val success = characteristic.writeValue(byteArrayOf(0x01))

                    println(if (success) "Characteristic ${characteristic.uuid} :${ANSI_YELLOW}0x01$ANSI_RESET" else "Write failed")
                    return
                }
            }
        }

        println("$ERROR Characteristic with UUID $input not found")
    }

    fun subscribeCharacteristic(device: BluetoothDevice): BluetoothGattCharacteristic? {
        println("$INPUT Select characteristic to subscribe")

        val br = BufferedReader(InputStreamReader(System.`in`))
        val input = br.readLine()

        for (service in device.services) {
            for (characteristic in service.characteristics) {
                if (input == characteristic.uuid) {
                    if (characteristic.uuid == input || characteristic.uuid.contains(input)) {
                        if (!device.connected) device.connect()
                        characteristic.enableValueNotifications(this)
                        device.enableConnectedNotifications({
                            println("$INFO Connected")
                        })

                        println("$INFO Characteristic ${characteristic.uuid} subscribed")
                        return characteristic
                    }
                }
            }
        }

        println("$ERROR Characteristic with UUID $input not found")
        return null
    }

    fun unsubscribeCharacteristic(device: BluetoothDevice, characteristic: BluetoothGattCharacteristic) {
        for (s in device.services) {
            for (c in s.characteristics) {
                if (c.uuid == characteristic.uuid) {
                    characteristic.disableValueNotifications()
                    println("$INPUT Characteristic ${characteristic.uuid} ${ANSI_YELLOW}unsubscribed$ANSI_RESET")
                }
            }
        }
    }

    // </editor-fold>

    // --------------------
    // Callback interfaces
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Callback interfaces">

    override fun run(value: ByteArray?) {
        println("$INFO PING")
    }

// </editor-fold>
}