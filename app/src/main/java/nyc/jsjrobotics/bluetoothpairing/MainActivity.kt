package nyc.jsjrobotics.bluetoothpairing

import android.app.Activity
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.LifecycleRegistry
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import nyc.jsjrobotics.bluetoothpairing.pairedDevices.PairedDevices
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.IntentFilter



class MainActivity : LifecycleActivity() {

    private var content: FrameLayout? = null

    private val REQUEST_ENABLE_BT : Int = 892040292

    // Return int value to listen for in activity result if intent is sent to start bluetooth
    fun sendBluetoothStartIntent(activity : Activity):Int {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        ActivityCompat.startActivityForResult(activity, enableBtIntent, REQUEST_ENABLE_BT, null)
        return REQUEST_ENABLE_BT
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val transaction = supportFragmentManager.beginTransaction();
        val fragmentToShow : String;
        when (item.itemId) {
            R.id.navigation_home -> {
                fragmentToShow = fragmentList[0].first;
            }
            R.id.navigation_dashboard -> {
                fragmentToShow = fragmentList[1].first;
            }
            R.id.navigation_notifications -> {
                fragmentToShow = fragmentList[2].first;
            }
            else -> {
                throw IllegalStateException("Unknown fragment to show");
            }
        }
        val fragmentDisplayStates : List<Pair<Fragment, Boolean>> =
                getTags().mapIndexed({
                    index, tag -> Pair(supportFragmentManager.findFragmentByTag(tag), tag == fragmentToShow)
                })

        fragmentDisplayStates.forEach({fragmentToDisplay ->
            val display : Boolean = fragmentToDisplay.second
            val fragment : Fragment = fragmentToDisplay.first
            if (display) {
                transaction.show(fragment)
            } else {
                transaction.hide(fragment)
            }})
        transaction.commit();
        true
    }

    private var fragmentList: List<Pair<String, () -> Fragment>> =  listOf(
            Pair("PairedDevices",  { PairedDevices() }),
            Pair("Select Speaker",  { SelectSpeaker() }),
            Pair("Select Station", { SelectStation() })
    );

    private fun getTags() : List<String> {
        return fragmentList.map { pair -> pair.first}
    }


    lateinit private var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mReceiver = ActionFoundReceiver(lifecycle)
        content = findViewById(R.id.content) as FrameLayout
        var tempBluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        if (tempBluetoothAdapter == null) {
            setContentView(R.layout.unsupported)
            return
        }
        displayFragments(savedInstanceState)
        bluetoothAdapter = tempBluetoothAdapter;
        if (!bluetoothAdapter.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            registerEnableIntent(REQUEST_ENABLE_BT)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return
        }

        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun displayFragments(savedInstanceState: Bundle?) {
        val transaction : FragmentTransaction = supportFragmentManager.beginTransaction();
        if (savedInstanceState == null) {
            fragmentList.forEachIndexed( { index, fragmentWithTag ->
                val tag : String = fragmentWithTag.first
                val createFragment : FragmentSupplier = fragmentWithTag.second
                val fragment : Fragment = createFragment.invoke();
                transaction.add(R.id.content, fragment, tag);
                if (index == 0) {
                    transaction.show(fragment)
                } else{
                    transaction.hide(fragment)
                }
            })
        }
        transaction.commit()
    }

    private fun registerEnableIntent(resultCode: Int) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ENABLE_BT) {
            when (resultCode) {
                Activity.RESULT_OK -> { }
                else -> {
                    displayMustEnable()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    lateinit private var mReceiver: ActionFoundReceiver

    private fun registerIntentReceiver() {
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
    }
    
    private fun displayMustEnable() {
        setContentView(R.layout.must_enable)
    }
}

class ActionFoundReceiver(val lifecycleRegistry: LifecycleRegistry) : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

