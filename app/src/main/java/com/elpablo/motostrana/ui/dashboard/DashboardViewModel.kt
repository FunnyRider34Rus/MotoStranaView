package com.elpablo.motostrana.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elpablo.motostrana.App.Companion.remoteEventDB
import com.elpablo.motostrana.model.Event
import dagger.hilt.android.scopes.ViewModelScoped

@ViewModelScoped
class DashboardViewModel : ViewModel() {

    fun getEvents(event: String) : LiveData<List<Event>> {
        val eventList = MutableLiveData<List<Event>>()
        remoteEventDB.collection(event).orderBy("date").get().addOnSuccessListener { documentSnapshot ->
            val events = documentSnapshot.toObjects(Event::class.java)
            eventList.postValue(events.asReversed())
        }
        return eventList
    }
}