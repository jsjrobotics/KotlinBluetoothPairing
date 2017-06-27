package nyc.jsjrobotics.bluetoothpairing.bluetooth

import android.arch.lifecycle.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import nyc.jsjrobotics.bluetoothpairing.BuildConfig
import nyc.jsjrobotics.bluetoothpairing.MainActivity

class ActionFoundReceiver(val lifecycleRegistry: LifecycleRegistry) : BroadcastReceiver(), LifecycleObserver {

    private val discoveryDisabled : PublishSubject<Boolean> = PublishSubject.create()
    private val deviceFound : PublishSubject<BluetoothDevice> = PublishSubject.create()

    init {
        lifecycleRegistry.addObserver(this);
    }

    fun onDiscoveryDisabled() : Observable<Boolean> {
        return discoveryDisabled
    }

    fun onDeviceFound() : Observable<BluetoothDevice> {
        return deviceFound
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getAction()
        if (BluetoothDevice.ACTION_FOUND == action) {
            val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            deviceFound.onNext(device)
            System.out.println("Saw -> " + device.name )
        } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED == action) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE)
            if (state != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                discoveryDisabled.onNext(true)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun registerBroadcastReceiver(source : LifecycleOwner) {
        if (source is MainActivity) {
            log("Registering for Bluetooth action Found ")
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            source.registerReceiver(this, filter)
        }
    }

    fun registerBroadcastReceiver(source : LifecycleOwner, context : Context) {
        log("Registering for Bluetooth action Found ")
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(this, filter)
    }

    private fun log(s: String) {
        if (BuildConfig.DEBUG) {
            Log.w("ActionFoundReceiver", s)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun removeLifecyleObserver(source : LifecycleOwner) {
        if (source is MainActivity) {
            log("UnRegistering Bluetooth action Found ")
            source.unregisterReceiver(this)
            source.lifecycle.removeObserver(this)
        }
    }
}