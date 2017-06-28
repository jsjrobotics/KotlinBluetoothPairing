package nyc.jsjrobotics.bluetoothpairing.bluetooth

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


class ServiceListener : BluetoothProfile.ServiceListener{
    private val onHeadsetConnected : PublishSubject<BluetoothHeadset> = PublishSubject.create()
    private val onHeadsetDisconnected : PublishSubject<BluetoothHeadset> = PublishSubject.create()
    private val onA2dpConnected : PublishSubject<BluetoothA2dp> = PublishSubject.create()
    private val onA2dpDisconnected : PublishSubject<BluetoothA2dp> = PublishSubject.create()

    private var headset: BluetoothHeadset? = null
    private var speaker: BluetoothA2dp? = null

    override fun onServiceDisconnected(profile: Int) {
        updateBluetoothProfile(profile, null);
    }

    object instance {
        val serviceListener : ServiceListener = ServiceListener()
    }

    fun onHeadsetConnected() : Observable<BluetoothHeadset> {
        return onHeadsetConnected
    }

    fun onHeadsetDisconnected() : Observable<BluetoothHeadset> {
        return onHeadsetDisconnected
    }

    fun onA2dpConnected() : Observable<BluetoothA2dp> {
        return onA2dpConnected
    }

    fun onA2dpDisconnected() : Observable<BluetoothA2dp> {
        return onA2dpDisconnected
    }



    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
        updateBluetoothProfile(profile, proxy)
    }

    fun updateBluetoothProfile(profile: Int, proxy: BluetoothProfile?) {
        val registerProfile = proxy != null
        when (profile) {
            BluetoothProfile.HEADSET -> {
                if (registerProfile) {
                    headset = proxy as BluetoothHeadset
                    onHeadsetConnected.onNext(headset)
                } else {
                    onHeadsetDisconnected.onNext(headset)
                    headset = null
                }
            }
            BluetoothProfile.A2DP -> {
                if (registerProfile) {
                    speaker = proxy as BluetoothA2dp
                    onA2dpConnected.onNext(speaker)
                } else {
                    onA2dpDisconnected.onNext(speaker)
                    speaker = null
                }

            }
        }
    }

}