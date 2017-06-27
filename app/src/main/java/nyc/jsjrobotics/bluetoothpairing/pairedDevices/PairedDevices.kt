package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import nyc.jsjrobotics.bluetoothpairing.bluetooth.ActionFoundReceiver


class PairedDevices : nyc.jsjrobotics.bluetoothpairing.DefaultFragment()  {


    lateinit private var bluetoothAdapter: android.bluetooth.BluetoothAdapter
    lateinit private var presenter: PairedDevicesPresenter
    lateinit private var view : PairedDevicesView
    private val subscriptions: SubscriptionsManager = SubscriptionsManager()


    lateinit private var  discoverableReceiver: ActionFoundReceiver

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        discoverableReceiver = ActionFoundReceiver(lifecycle);
        bluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        presenter = PairedDevicesPresenter(bluetoothAdapter, lifecycle);
    }

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        view = PairedDevicesView(inflater, container, savedInstanceState);
        presenter.bindView(view)
        return view.getRoot()
    }

    override fun onStart() {
        super.onStart()
        subscriptions.add(presenter.onMakeDiscoverable().subscribe(this::handleMakeDiscoverable))
        subscriptions.add(discoverableReceiver.onDiscoveryInProgress().subscribe({ isDiscovering -> presenter.showStopSearching(isDiscovering)}))

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

