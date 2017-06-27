package nyc.jsjrobotics.bluetoothpairing

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.widget.FrameLayout
import nyc.jsjrobotics.bluetoothpairing.pairedDevices.PairedDevices
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import nyc.jsjrobotics.bluetoothpairing.bluetooth.ActionFoundReceiver
import android.Manifest.permission
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat






class MainActivity : LifecycleActivity() {

    private var content: FrameLayout? = null
    private val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1

    private val REQUEST_ENABLE_BT : Int = 100

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
        requestRuntimePermissions()
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
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }

        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun requestRuntimePermissions() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf<String>(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION -> {
                var permissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (!permissionGranted) {
                    throw IllegalStateException("Must have permission")
                }
            }
        }
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

