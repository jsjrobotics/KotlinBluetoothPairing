package nyc.jsjrobotics.bluetoothpairing.bluetooth

import android.app.Activity
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

class ActionFoundReceiver : BroadcastReceiver(), LifecycleObserver {


    private val discoveryInProgress: PublishSubject<Boolean> = PublishSubject.create()
    private val deviceFound : PublishSubject<BluetoothDevice> = PublishSubject.create()

    object instance {
        val actionFoundReceiver : ActionFoundReceiver = ActionFoundReceiver()
    }

    fun registerLifecycle(lifecycleRegistry: LifecycleRegistry) {
        lifecycleRegistry.addObserver(this);
    }

    fun onDiscoveryInProgress() : Observable<Boolean> {
        return discoveryInProgress
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
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
            discoveryInProgress.onNext(true)
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
            discoveryInProgress.onNext(false)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun registerBroadcastReceiver(source : LifecycleOwner) {
        val context : Context? = getContextFromLifecycleOwner(source)
        if (context == null) {
            throw IllegalStateException("Must not have null context")
        }
        log("Registering for Bluetooth action Found ")
        val filter = IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        context.registerReceiver(this, filter)
    }

    private fun getContextFromLifecycleOwner(source: LifecycleOwner): Context? {
        if (source is LifecycleActivity) {
            return source
        } else if (source is LifecycleFragment) {
            return source.context
        }
        return null
    }

    private fun log(s: String) {
        if (BuildConfig.DEBUG) {
            Log.w("ActionFoundReceiver", s)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun removeLifecyleObserver(source : LifecycleOwner) {
        val context : Context? = getContextFromLifecycleOwner(source)
        if (context == null) {
            throw IllegalStateException("Must not have null context")
        }
        log("UnRegistering Bluetooth action Found ")
        context.unregisterReceiver(this)
        source.lifecycle.removeObserver(this)

    }
}