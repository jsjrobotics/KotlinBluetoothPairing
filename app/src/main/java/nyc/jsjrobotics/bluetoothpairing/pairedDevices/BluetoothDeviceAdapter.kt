package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import nyc.jsjrobotics.bluetoothpairing.R

class BluetoothDeviceAdapter : RecyclerView.Adapter<BluetoothDeviceViewHolder>() {
    private val onDeviceSelected : PublishSubject<BluetoothDevice> = PublishSubject.create()
    private val pairedDevices: ArrayList<BluetoothDevice> = ArrayList()

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        holder.bind(pairedDevices[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        return BluetoothDeviceViewHolder(parent, Consumer { onDeviceSelected::onNext })
    }

    override fun getItemCount(): Int {
       return pairedDevices.size
    }

    fun onDeviceSelected() : Observable<BluetoothDevice> {
        return onDeviceSelected
    }

    fun addDevices(deviceList: List<BluetoothDevice>) {
        pairedDevices.addAll(deviceList)
        notifyDataSetChanged()
    }

    fun addDevice(device : BluetoothDevice) {
        pairedDevices.add(device)
        notifyDataSetChanged()
    }

}