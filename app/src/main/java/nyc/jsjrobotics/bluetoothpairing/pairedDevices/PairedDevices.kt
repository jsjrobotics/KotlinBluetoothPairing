package nyc.jsjrobotics.bluetoothpairing.pairedDevices


class PairedDevices : nyc.jsjrobotics.bluetoothpairing.DefaultFragment()  {


    lateinit private var bluetoothAdapter: android.bluetooth.BluetoothAdapter
    lateinit private var presenter: PairedDevicesPresenter
    lateinit private var view : PairedDevicesView

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
        presenter = PairedDevicesPresenter(bluetoothAdapter, lifecycle);
    }

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        view = PairedDevicesView(inflater, container, savedInstanceState);
        presenter.bindView(view)
        return view.getRoot()
    }
}

