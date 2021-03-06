package nyc.jsjrobotics.bluetoothpairing.selectSpeaker

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import nyc.jsjrobotics.bluetoothpairing.DefaultFragment
import nyc.jsjrobotics.bluetoothpairing.R


class SelectSpeaker : DefaultFragment(){

    lateinit private var presenter: SelectSpeakerPresenter
    lateinit private var view : SelectSpeakerView

    lateinit private var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioManager = context.getSystemService (Context.AUDIO_SERVICE) as AudioManager;
        audioManager.startBluetoothSco()

        val bluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        presenter = SelectSpeakerPresenter(bluetoothAdapter, lifecycle, audioManager)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = SelectSpeakerView(inflater, container, savedInstanceState)
        presenter.bindView(view)
        return view.getRoot()
    }

    fun setDeviceToConnectTo(bluetoothDevice: BluetoothDevice) {
        presenter.setDeviceToConnectTo(bluetoothDevice)
    }
}