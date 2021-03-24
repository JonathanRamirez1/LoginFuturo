package com.jonathan.loginfuturo.dialogues

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.jonathan.loginfuturo.R
import com.jonathan.loginfuturo.Utils.RxBus
import com.jonathan.loginfuturo.models.NewRateEvent
import com.jonathan.loginfuturo.models.Rate
import kotlinx.android.synthetic.main.dialog_rate.view.*
import java.util.*


class RateDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_rate, null)

        return  AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.alert_dialog_title))
            .setView(view)
            .setPositiveButton(getString(R.string.alert_dialog_ok)) { _, _ ->
                val textRate = view.editTextRateFeedback.text.toString()
                if (textRate.isNotEmpty()) {
                    val imageUrl = FirebaseAuth.getInstance().currentUser!!.photoUrl?.toString() ?: run { "" }
                    val rate = Rate(textRate, view.ratingBarFeedback.rating, Date(), imageUrl)
                    RxBus.publish(NewRateEvent(rate))
                }
            }
            .setNegativeButton(getString(R.string.alert_dialog_cancel)) { _, _ ->
                Toast.makeText(context!!, "Pressed Cancel", Toast.LENGTH_SHORT).show()
            }
            .create()
    }

}


/*override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

    //TODO IMPLEMENTAR DATABINDING PARA LAS VIEW
    // val view   = layoutInflater.inflate(R.layout.dialog_rate, null)

    return AlertDialog.Builder(context!!)
        .setTitle(getString(R.string.alert_dialog_title))
        .setView(R.layout.dialog_rate)
        .setPositiveButton(getString(R.string.alert_dialog_ok)) { _, _ ->
            val textRate = view?.editTextRateFeedback?.text.toString()
            val imageUrl = FirebaseAuth.getInstance().currentUser!!.photoUrl?.toString() ?: run { "" }
            val rate =
                view?.ratingBarFeedback?.rating?.let { Rate(textRate, it, Date(), imageUrl) }
            rate?.let { NewRateEvent(it) }?.let { RxBus.publish(it) }
        }
        .setNegativeButton(getString(R.string.alert_dialog_cancel)) { _, _ ->
            Toast.makeText(context!!, "Pressed Cancel", Toast.LENGTH_SHORT).show()
        }
        .create()
}*/