package nyc.jsjrobotics.bluetoothpairing.selectSpeaker

import android.bluetooth.BluetoothDevice
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import nyc.jsjrobotics.bluetoothpairing.R


class SelectSpeakerView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?) {
    private var root: ViewGroup
    private val connectToA2dp: Button
    private val connectToHeadset: Button

    private val onClickA2dp : PublishSubject<View> = PublishSubject.create()
    private val onClickHeadset : PublishSubject<View> = PublishSubject.create()

    private var deviceToConnectTo: TextView

    init {
        root = inflater!!.inflate(R.layout.select_speaker, container, false) as ViewGroup
        deviceToConnectTo = root.findViewById(R.id.device_to_connect) as TextView
        connectToA2dp = root.findViewById(R.id.connect_to_a2dp) as Button
        connectToA2dp.setOnClickListener(onClickA2dp::onNext)
        connectToHeadset = root.findViewById(R.id.connect_to_headset) as Button
        connectToHeadset.setOnClickListener(onClickHeadset::onNext)
    }

    fun onClickConnectA2dp() : Observable<View> {
        return onClickA2dp
    }

    fun onClickConnectHeadset() : Observable<View> {
        return onClickHeadset
    }

    fun getRoot(): View {
        return root
    }

    fun debugMakeToast(s: String) {
        Toast.makeText(root.context, s, Toast.LENGTH_LONG).show()
    }

    fun setDeviceToConnectTo(device: BluetoothDevice?) {
        if (device == null) {
            deviceToConnectTo.setText(R.string.no_selected_device_to_connect)
        } else if (device.name != null && device.name!!.isNotEmpty()) {
            deviceToConnectTo.setText(device.name)
        } else {
            deviceToConnectTo.setText(device.address)
        }
    }
}