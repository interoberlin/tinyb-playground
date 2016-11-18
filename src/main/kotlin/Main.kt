import de.interoberlin.tinyb_playground.EAction
import de.interoberlin.tinyb_playground.TinybController
import tinyb.BluetoothManager

fun main(args: Array<String>) {
    val tinybController = TinybController.getInstance()
    val manager = BluetoothManager.getBluetoothManager()

    // Scan devices

    val devices = tinybController.scanDevices(manager)

    tinybController.showDevices(devices)

    val device = tinybController.selectDevice(devices)

    do {
        var action = tinybController.selectAction()
        when (action) {
            EAction.CONNECT -> tinybController.connectDevice(device)
            EAction.DISCONNECT -> tinybController.disconnectDevice(device)
            EAction.PAIR -> tinybController.pairDevice(device)
            EAction.FIND_SERVICE -> tinybController.findService(device)
            EAction.SHOW_SERVICES -> tinybController.showServices(device)
            EAction.READ_CHARACTERISTIC -> tinybController.readCharacteristic(device);
            EAction.WRITE_CHARACTERISTIC -> tinybController.writeCharacteristic(device);
            EAction.QUIT -> println("Byebye")
        }
    } while (action != EAction.QUIT);
}