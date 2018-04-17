package inc.yoman.toggleonthego

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.util.*


class MainActivity : AppCompatActivity(), OnCompleteListener<Void> {
    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    private enum class PendingGeofenceTask {

        ADD, REMOVE, NONE
    }

    /**
     * The list of geofences used in this sample.
     */
    private var mGeofenceList: ArrayList<Geofence>? = null

    /**
     * Used when requesting to add or remove geofences.
     */

    lateinit var mGeofencePendingIntent: PendingIntent

    lateinit var mGeofencingClient: GeofencingClient

    private var mPendingGeofenceTask = PendingGeofenceTask.NONE

    @SuppressLint("WifiManagerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter.isEnabled) {
            mBluetoothAdapter.disable()
        }

        val wifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = false
    }

    private fun init() {
        mGeofencingClient = LocationServices.getGeofencingClient(this)

        mGeofenceList = ArrayList()
    }

    private fun performPendingGeofenceTask() {
        mPendingGeofenceTask = PendingGeofenceTask.ADD
        populateGeofenceList()
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences()
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences()
        }
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    private fun populateGeofenceList() {
//        for (entry in Constants.BAY_AREA_LANDMARKS.entrySet()) {
        mGeofenceList?.add(Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("0")

                // Set the circular region of this geofence.
                .setCircularRegion(
                        Constants.latitude,
                        Constants.longitude,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build())
//        }
    }

    public override fun onStart() {
        super.onStart()

        performPendingGeofenceTask()
    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressLint("MissingPermission")
    private fun addGeofences() {
//        if (!checkPermissions()) {
//            showSnackbar(getString(R.string.insufficient_permissions))
//            return
//        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this)
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    private fun removeGeofences() {
//        if (!checkPermissions()) {
//            showSnackbar(getString(R.string.insufficient_permissions))
//            return
//        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this)
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private fun getGeofencingRequest(): GeofencingRequest {
        val builder = GeofencingRequest.Builder()

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList)

        // Return a GeofencingRequest.
        return builder.build()
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private fun getGeofencePendingIntent(): PendingIntent {
        // Reuse the PendingIntent if we already have it.
//        if (mGeofencePendingIntent != null) {
//            return mGeofencePendingIntent as PendingIntent
//        }
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return mGeofencePendingIntent
    }

    /**
     * Stores whether geofences were added ore removed in [SharedPreferences];
     *
     * @param added Whether geofences were added or removed.
     */
    private fun updateGeofencesAdded(added: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply()
    }

    /**
     * Returns true if geofences were added, otherwise false.
     */
    private fun getGeofencesAdded(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false)
    }

    /**
     * Runs when the result of calling [.addGeofences] and/or [.removeGeofences]
     * is available.
     * @param task the resulting Task, containing either a result or error.
     */
    override fun onComplete(task: Task<Void>) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE
        if (task.isSuccessful) {
            updateGeofencesAdded(!getGeofencesAdded())
//            setButtonsEnabledState()

            val messageId = if (getGeofencesAdded())
                R.string.geofences_added
            else
                R.string.geofences_removed
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            val errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException()!!)
            Log.w("", errorMessage)
        }
    }
}