package com.myjar.jarassignment.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myjar.jarassignment.createRetrofit
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.repository.JarRepository
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class JarViewModel : ViewModel() {

    private val _listStringData = MutableStateFlow<List<ComputerItem>>(emptyList())
    val listStringData: StateFlow<List<ComputerItem>>
        get() = _listStringData

    private val repository: JarRepository = JarRepositoryImpl(createRetrofit())

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean>
        get() = _isSearching

    private val _searchTerm = MutableStateFlow("")
    val searchTerm: StateFlow<String>
        get() = _searchTerm

    fun updateSearchTerm(searchTerm: String) {
        _searchTerm.value = searchTerm
    }

    val filteredList = searchTerm
        .debounce(500)
        .onEach { _isSearching.value = true }
        .combine(_listStringData) { text, searchList ->
            if (text.isEmpty()) {
                searchList
            } else {
                delay(2000)
                searchList.filter {
                    it.name.contains(text, ignoreCase = true)
                }
            }
        }.onEach {
            _isSearching.value = false
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), _listStringData.value)

    fun fetchData() {
        viewModelScope.launch {
            repository.fetchResults().collect { resultList ->
                _listStringData.value = resultList
            }
        }
    }
}