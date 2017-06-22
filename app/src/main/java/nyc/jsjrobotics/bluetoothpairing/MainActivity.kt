package nyc.jsjrobotics.bluetoothpairing

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import nyc.jsjrobotics.bluetoothpairing.pairedDevices.PairedDevices

class MainActivity : AppCompatActivity() {

    private var content: FrameLayout? = null

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        content = findViewById(R.id.content) as FrameLayout

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

        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}

