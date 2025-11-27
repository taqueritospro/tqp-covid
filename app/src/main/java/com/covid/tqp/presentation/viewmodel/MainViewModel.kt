package com.covid.tqp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.tqp.domain.model.DomainModel
import com.covid.tqp.domain.usecase.GetDataUseCase
import kotlinx.coroutines.launch

class MainViewModel(
    private val getDataUseCase: GetDataUseCase
) : ViewModel() {

    private val _data = MutableLiveData<List<DomainModel>>()
    val data: LiveData<List<DomainModel>> = _data

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _data.value = getDataUseCase()
        }
    }
}