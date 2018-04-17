package inc.yoman.toggleonthego

import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * Created by Jitesh on 07/03/18.
 */
internal object Constants {

    private val PACKAGE_NAME = "com.google.android.gms.location.Geofence"

    val GEOFENCES_ADDED_KEY = "$PACKAGE_NAME.GEOFENCES_ADDED_KEY"

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private val GEOFENCE_EXPIRATION_IN_HOURS: Long = 12

    /**
     * For this sample, geofences expire after twelve hours.
     */
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000
    val GEOFENCE_RADIUS_IN_METERS = 1609f // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    val BAY_AREA_LANDMARKS = HashMap<String, LatLng>()
    val latitude = 37.621313
    val longitude = -122.378955


    init {
        // San Francisco International Airport.
        BAY_AREA_LANDMARKS["SFO"] = LatLng(37.621313, -122.378955)

        // Googleplex.
        BAY_AREA_LANDMARKS["GOOGLE"] = LatLng(37.422611, -122.0840577)
    }
}