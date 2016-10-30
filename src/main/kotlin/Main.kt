import de.interoberlin.tinyb_playground.EAction
import de.interoberlin.tinyb_playground.TinybController
import tinyb.BluetoothManager

fun main(args: Array<String>) {
    val tinybController = TinybController.getInstance()
    val manager = BluetoothManager.getBluetoothManager()

    // Scan devices

    val devices = tinybController.scanDevices(manager)

    tinybController.showDevices(devices)

    val action = tinybController.selectAction()
    val device = tinybController.selectDevice(devices)

    when (action) {
        EAction.CONNECT -> tinybController.connectDevice(device)
        EAction.DISCONNECT -> tinybController.disconnectDevice(device)
    }

    tinybController.showDevices(devices)
    tinybController.showServices(device)
}