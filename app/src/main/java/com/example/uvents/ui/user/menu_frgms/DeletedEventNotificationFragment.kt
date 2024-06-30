package com.example.uvents.ui.user.menu_frgms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.MenuController
import com.example.uvents.controllers.adapter.DeletedEventNotificationAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [DeletedEventNotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeletedEventNotificationFragment(private val menuController: MenuController,) : Fragment() {

    private lateinit var rvNotifications: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when the Fragment is at least Started.
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                menuController.setPersonalPage()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_deleted_event_notification, container, false)
        if(v != null) {
            rvNotifications = v.findViewById(R.id.rv_deleted_events)
        }

        val deletedEventNotificationAdapter: DeletedEventNotificationAdapter =
            DeletedEventNotificationAdapter(menuController.deletedBookedEvents)
        rvNotifications.adapter = deletedEventNotificationAdapter

        return v
    }

}