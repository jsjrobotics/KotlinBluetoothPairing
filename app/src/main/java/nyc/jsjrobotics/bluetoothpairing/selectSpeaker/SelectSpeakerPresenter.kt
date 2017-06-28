package nyc.jsjrobotics.bluetoothpairing.selectSpeaker

import android.arch.lifecycle.*
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.view.View
import io.reactivex.disposables.Disposable
import nyc.jsjrobotics.bluetoothpairing.bluetooth.ServiceListener
import nyc.jsjrobotics.bluetoothpairing.pairedDevices.SubscriptionsManager
import java.util.*


class SelectSpeakerPresenter(val bluetoothAdapter: BluetoothAdapter,
                             val lifecycle: LifecycleRegistry) : LifecycleObserver{
    private val serviceListener: ServiceListener
    private val subscriptions: SubscriptionsManager = SubscriptionsManager()
    lateinit private var view: SelectSpeakerView

    init {
        lifecycle.addObserver(this)
        serviceListener = ServiceListener.instance.serviceListener
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun init(source : LifecycleOwner, event : Lifecycle.Event) {
        val clickA2dp: Disposable = view.onClickConnectA2dp().subscribe(this::connectToA2dp)
        val clickHeadset : Disposable = view.onClickConnectHeadset().subscribe(this::connectToHeadset)
        val onA2dpConnected : Disposable = serviceListener.onA2dpConnected().subscribe(this::handleA2dpConnected)
        val onA2dpDisconnected : Disposable = serviceListener.onA2dpConnected().subscribe(this::handleA2dpDisconnected)
        val onHeadsetConnected : Disposable = serviceListener.onHeadsetConnected().subscribe(this::handleHeadsetConnected)
        val onHeadsetDisconnected : Disposable = serviceListener.onHeadsetDisconnected().subscribe(this::handleHeadsetDisconnected)

        val subscriptionList : List<Disposable> = Arrays.asList(
                clickA2dp,
                clickHeadset,
                onA2dpConnected,
                onA2dpDisconnected,
                onHeadsetConnected,
                onHeadsetDisconnected
        )
        subscriptions.addAll(subscriptionList);

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unRegister(source : LifecycleOwner ) {
        subscriptions.clear()
    }


    fun connectToA2dp(view : View) {
        bluetoothAdapter.getProfileProxy(view.context, serviceListener, BluetoothProfile.A2DP)
    }

    fun connectToHeadset(view : View) {
        bluetoothAdapter.getProfileProxy(view.context, serviceListener, BluetoothProfile.HEADSET)
    }

    fun handleHeadsetConnected(device : BluetoothHeadset) {
        device.connectedDevices.forEach({d ->
            view.debugMakeToast("Headset " + d.name + " -> audio enabled: " + device.isAudioConnected(d))
        })
    }

    fun handleHeadsetDisconnected(device : BluetoothHeadset) {
        view.debugMakeToast("Headset disconnected")
    }

    fun handleA2dpConnected(device : BluetoothA2dp) {
        view.debugMakeToast("A2dp Connected")
    }

    fun handleA2dpDisconnected(device : BluetoothA2dp) {
        view.debugMakeToast("A2dp disconnected")
    }

    fun bindView(view: SelectSpeakerView) {
        this.view = view
    }

}