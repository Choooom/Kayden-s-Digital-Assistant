package com.example.kaydensdigitalassistant.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployeeDetailViewModel(private val repository: EmployeeRepository) : ViewModel() {
    var name: String = ""
    var contactNumber = "09296726163"
    var emailAddress = ""
    var birthdate = ""
    var password = ""
    var username = ""

    val allEmployees: LiveData<List<EmployeeDetail>> = repository.allEmployees.asLiveData()

    private val _currentEmployee = MutableStateFlow<EmployeeDetail?>(null)
    val currentEmployee: StateFlow<EmployeeDetail?> = _currentEmployee.asStateFlow()

    fun loginEmployee(username: String, password: String) = viewModelScope.launch {
        val employee = repository.loginEmployee(username, password)
        _currentEmployee.value = employee
    }

    fun insertEmployee(employee: EmployeeDetail) = viewModelScope.launch {
        repository.insertEmployee(employee)
    }

    fun clearEmployeeDetail() = viewModelScope.launch {
        name = ""
        emailAddress = ""
        birthdate = ""
        password = ""
        username = ""
    }

    fun insertCompleteEmployee() = viewModelScope.launch {
        println("Insert Employee Called")
        val employee = EmployeeDetail(
            name = name,
            contactNumber = contactNumber,
            emailAddress = emailAddress,
            birthdate = birthdate,
            password = password,
            username = username
        )
        repository.insertEmployee(employee)
    }

    fun updateEmployee(employee: EmployeeDetail) = viewModelScope.launch {
        repository.updateEmployee(employee)
    }

    fun deleteEmployee(employee: EmployeeDetail) = viewModelScope.launch {
        repository.deleteEmployee(employee)
    }

    class EmployeeDetailViewModelFactory(private val repository: EmployeeRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EmployeeDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EmployeeDetailViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class EmployeeRepository(private val employeeDetailDao: EmployeeDetailDao) {
    val allEmployees: Flow<List<EmployeeDetail>> = employeeDetailDao.getAllEmployees()

    suspend fun insertEmployee(employee: EmployeeDetail): Long {
        return employeeDetailDao.insertEmployee(employee)
    }

    suspend fun loginEmployee(username: String, password: String): EmployeeDetail? {
        return employeeDetailDao.loginEmployee(username, password)
    }

    suspend fun updateEmployee(employee: EmployeeDetail) {
        employeeDetailDao.updateEmployee(employee)
    }

    suspend fun deleteEmployee(employee: EmployeeDetail) {
        employeeDetailDao.deleteEmployee(employee)
    }
}
