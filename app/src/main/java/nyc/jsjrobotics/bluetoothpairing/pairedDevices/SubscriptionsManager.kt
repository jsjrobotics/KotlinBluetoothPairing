package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import io.reactivex.disposables.Disposable

class SubscriptionsManager {
    private val subscriptions: ArrayList<Disposable> = ArrayList()
    fun add(subscription: Disposable) {
        subscriptions.add(subscription)
    }

    fun clear() {
        subscriptions.forEach({disposable -> disposable.dispose()})
        subscriptions.clear()
    }

    fun addAll(toAdd: List<Disposable>) {
        toAdd.forEach({add(it)})
    }

}