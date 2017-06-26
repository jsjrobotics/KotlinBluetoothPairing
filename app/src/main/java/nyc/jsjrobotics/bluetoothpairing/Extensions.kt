package nyc.jsjrobotics.bluetoothpairing

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


typealias FragmentSupplier = () -> Fragment

fun ViewGroup.inflate(layoutId : Int, attachToParent: Boolean = false) : View {
    val inflater = LayoutInflater.from(context)
    return inflater.inflate(layoutId, this, attachToParent)
}