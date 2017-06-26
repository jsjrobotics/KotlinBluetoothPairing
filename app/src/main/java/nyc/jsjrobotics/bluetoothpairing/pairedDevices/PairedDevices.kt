package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import nyc.jsjrobotics.bluetoothpairing.BuildConfig


class PairedDevices : nyc.jsjrobotics.bluetoothpairing.DefaultFragment()  {


    lateinit private var bluetoothAdapter: android.bluetooth.BluetoothAdapter
    lateinit private var presenter: PairedDevicesPresenter
    lateinit private var view : PairedDevicesView

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        } else {
          //  bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
        }
        presenter = PairedDevicesPresenter(bluetoothAdapter, lifecycle);
    }

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        view = PairedDevicesView(inflater, container, savedInstanceState);
        presenter.bindView(view)
        return view.getRoot()
    }

    override fun onStart() {
        super.onStart()
    }
}

