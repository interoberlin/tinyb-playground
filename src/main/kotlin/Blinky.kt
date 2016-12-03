import de.interoberlin.tinyb_playground.ECharacteristic
import de.interoberlin.tinyb_playground.EService
import de.interoberlin.tinyb_playground.TextColor
import de.interoberlin.tinyb_playground.TinybController
import tinyb.BluetoothDevice
import java.security.SecureRandom
import java.util.*

fun main(args: Array<String>) {
    val tinybController = TinybController.getInstance()

    val devices: List<BluetoothDevice>
    val device: BluetoothDevice

    devices = tinybController.scanDevices()
    device = tinybController.selectDevice(devices)

    println(TextColor.DEBUG + "Device ${device.address}")
    println(TextColor.DEBUG + "Trying to connect...")

    tinybController.connectDevice(device)
    val service = device.find(EService.SENTIENT_LIGHT_LED.id)
    val characteristic = service.find(ECharacteristic.SENTIENT_LIGHT_LED_TX.id)

    println(TextColor.DEBUG + "Service ${service.uuid}")
    println(TextColor.DEBUG + "Characteristic ${characteristic.uuid}")

    // Endless loop
    println(TextColor.INFO + "Start sending values")
    while (true) {
        Thread.sleep(2000)
        // Turn off LEDs
        tinybController.writeCharacteristic(device, characteristic, byteArrayOf(0.toByte(), 0.toByte(), 0.toByte(), 0.toByte(), 0.toByte()))
        tinybController.writeCharacteristic(device, characteristic, byteArrayOf(0.toByte(), 1.toByte(), 0.toByte(), 0.toByte(), 0.toByte()))
        tinybController.writeCharacteristic(device, characteristic, byteArrayOf(0.toByte(), 2.toByte(), 0.toByte(), 0.toByte(), 0.toByte()))
        // Turn on LEDs
        Thread.sleep(2000)
        tinybController.writeCharacteristic(device, characteristic, byteArrayOf(0.toByte(), 0.toByte(), 255.toByte(), 100.toByte(), 50.toByte()))
        tinybController.writeCharacteristic(device, characteristic, byteArrayOf(0.toByte(), 1.toByte(), 100.toByte(), 50.toByte(), 255.toByte()))
        tinybController.writeCharacteristic(device, characteristic, byteArrayOf(0.toByte(), 2.toByte(), 50.toByte(), 255.toByte(), 100.toByte()))
    }

}