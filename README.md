# Learning Bluetooth

## Steps to setup Bluetooth
1. Add Permissions in `AndroidManifest.xml`.
2. Enable Bluetooth using `BluetoothAdapter`.
3. Check if Bluetooth is supported.
4. Find Bluetooth devices using `BluetoothDevice` & `BroadcastReceiver`.
5. Pair Bluetooth devices using `BluetoothDevice`.
7. Establish connection with Bluetooth devices using `BluetoothSocket`.
8. Transfer Bluetooth data using `BluetoothSocket`.
9. Close connection using `BluetoothSocket`.

*Note to clarify here between **points 5 and 6**, as they are closely related. There is a difference between being paired and being connected:*

- To be paired means that two devices are aware of each other's existence, have a shared link-key that can be used for authentication, and are capable of establishing an encrypted connection with each other.
- To be connected means that the devices currently share an RFCOMM channel and are able to transmit data with each other. The current Bluetooth APIs require devices to be paired before an RFCOMM connection can be established. Pairing is automatically performed when you initiate an encrypted connection with the Bluetooth APIs.

### Documentation

https://developer.android.com/develop/connectivity/bluetooth/setup
