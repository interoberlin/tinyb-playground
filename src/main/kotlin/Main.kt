import de.interoberlin.tinyb_playground.EAction
import de.interoberlin.tinyb_playground.TinybController
import tinyb.BluetoothDevice
import tinyb.BluetoothGattCharacteristic

fun main(args: Array<String>) {
    val tinybController = TinybController.getInstance()

    var devices: List<BluetoothDevice>? = null
    var device: BluetoothDevice? = null
    var characteristic: BluetoothGattCharacteristic? = null

    do {
        val action = tinybController.selectAction()
        when (action) {
            EAction.HELP -> tinybController.showHelp()
            EAction.SCAN -> devices = tinybController.scanDevices()
            EAction.SELECT_DEVICE -> if (devices != null) device = tinybController.selectDevice(devices)
            EAction.CONNECT -> if (device != null) tinybController.connectDevice(device)
            EAction.DISCONNECT -> if (device != null) tinybController.disconnectDevice(device)
            EAction.PAIR -> if (device != null) tinybController.pairDevice(device)
            EAction.FIND_SERVICE -> if (device != null) tinybController.findService(device)
            EAction.SHOW_SERVICES -> if (device != null) tinybController.showServices(device)
            EAction.READ_CHARACTERISTIC -> if (device != null) tinybController.readCharacteristic(device)
            EAction.WRITE_CHARACTERISTIC -> if (device != null) tinybController.writeCharacteristic(device)
            EAction.SUBSCRIBE_CHARACTERISTIC -> if (device != null) characteristic = tinybController.subscribeCharacteristic(device)
            EAction.UNSUBSCRIBE_CHARACTERISTIC -> if (device != null && characteristic != null) tinybController.unsubscribeCharacteristic(device, characteristic)
            EAction.QUIT -> println("Byebye")
            else -> println("Command not found. Type 'help' to show the help page or type 'quit' to end this program.")
        }
    } while (action != EAction.QUIT)
}