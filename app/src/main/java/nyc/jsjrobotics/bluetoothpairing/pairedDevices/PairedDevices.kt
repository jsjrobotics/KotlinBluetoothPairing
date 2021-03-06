package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import io.reactivex.functions.Consumer
import nyc.jsjrobotics.bluetoothpairing.MainActivity
import nyc.jsjrobotics.bluetoothpairing.bluetooth.ActionFoundReceiver
import java.util.*


class PairedDevices : nyc.jsjrobotics.bluetoothpairing.DefaultFragment()  {


    lateinit private var bluetoothAdapter: android.bluetooth.BluetoothAdapter
    lateinit private var presenter: PairedDevicesPresenter
    lateinit private var view : PairedDevicesView
    private val subscriptions: SubscriptionsManager = SubscriptionsManager()


    lateinit private var  discoverableReceiver: ActionFoundReceiver

    lateinit private var  audioManager: AudioManager

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        discoverableReceiver = ActionFoundReceiver.instance.actionFoundReceiver
        discoverableReceiver.registerLifecycle(lifecycle)
        bluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        presenter = PairedDevicesPresenter(bluetoothAdapter, audioManager, lifecycle, Consumer{device -> setDeviceToConnectTo(device)});
    }

    fun setDeviceToConnectTo(bluetoothDevice: BluetoothDevice) {
        (activity as MainActivity).setDeviceToConnectTo(bluetoothDevice)
    }
    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        view = PairedDevicesView(inflater, container, savedInstanceState);
        presenter.bindView(view)
        return view.getRoot()
    }

    override fun onStart() {
        super.onStart()
        val makeDiscoverable = presenter.onMakeDiscoverable().subscribe(this::handleMakeDiscoverable)
        val discoveryInProgress = discoverableReceiver.onDiscoveryInProgress().subscribe(this::discoveryStateChanged)
        val deviceFound = discoverableReceiver.onDeviceFound().subscribe(this::deviceFound)
        val subscriptionList = Arrays.asList(
                makeDiscoverable,
                discoveryInProgress,
                deviceFound
        )
        subscriptions.addAll(subscriptionList)

    }

    fun deviceFound(device : BluetoothDevice) {
        presenter.addDevice(device)
    }

    fun discoveryStateChanged(isDiscovering: Boolean) {
        presenter.showStopSearching(isDiscovering)
    }

    fun handleMakeDiscoverable(makeDiscoverable : Boolean) {
        if (makeDiscoverable) {
            presenter.showStopDiscoverable(makeDiscoverable)
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 90)
            startActivity(discoverableIntent)
        }
    }

    override fun onStop() {
        super.onStop()
        subscriptions.clear()
    }
}

