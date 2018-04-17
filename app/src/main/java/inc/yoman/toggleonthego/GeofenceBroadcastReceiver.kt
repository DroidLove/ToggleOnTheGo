package inc.yoman.toggleonthego

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Jitesh on 07/03/18.
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    /**
     * Receives incoming intents.
     *
     * @param context the application context.
     * @param intent  sent by Location Services. This Intent is provided to Location
     * Services (inside a PendingIntent) when addGeofences() is called.
     */
    override fun onReceive(context: Context, intent: Intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        GeofenceTransitionsJobIntentService.enqueueWork(context, intent)
    }
}
