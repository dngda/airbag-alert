package com.airmineral.airbagalert.ui.incident

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airmineral.airbagalert.data.model.Incident
import com.airmineral.airbagalert.data.repo.AlertRepository
import kotlinx.coroutines.launch

class IncidentViewModel(private val alertRepository: AlertRepository) : ViewModel() {

    val incidentList = MutableLiveData<List<Incident>>()

    fun getIncidentList() {
        viewModelScope.launch {
            alertRepository.getAlertData().let {
                incidentList.postValue(it)
            }
        }
    }
}