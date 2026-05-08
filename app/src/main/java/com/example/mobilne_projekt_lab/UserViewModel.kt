package com.example.mobilne_projekt_lab

import android.app.Application
import android.net.Uri
import android.os.Build
import android.view.animation.Transformation
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.switchMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.io.File
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getInstance(application).userDao()
    private val timeDao = AppDatabase.getInstance(application).TimeDao()
    private val selectedType = MutableLiveData<String?>(null)
    private var _time= MutableStateFlow(0)
    var time: StateFlow<Int> = _time.asStateFlow()
    private var _isRunning = MutableStateFlow(false)
    var isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    private var timerJob: Job? = null
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    /*val routeImages = mapOf(
        "Rusałka-Strzeszyn" to (R.drawable.rusalka_strzeszyn),
        "Grunwald-Wilda" to (R.drawable.grunwald_wilda),
        "Malta-Swarzędz" to (R.drawable.malta_swarzedz),
        "Kiekrz-Morasko" to (R.drawable.kiekrz_morasko),
        "Górczyn-Stary Rynek" to (R.drawable.gorczyn_stary_rynek)
    )*/
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
    //private val _opis = MutableLiveData<String?>(null)
    //val opis: StateFlow<String>= _opis.asStateFlow()
    init{ insertIfEmpty()}
    val trasy: LiveData<List<String>> = selectedType.switchMap{ type ->
        if (type == null) {
            userDao.getAllNames()
        }
        else if(type== ""){
            userDao.getAllNames()
        }
        else {
            userDao.getByType(type)
        }
    }


    private val _splashShown = MutableStateFlow(false)
    val splashShown: StateFlow<Boolean> = _splashShown.asStateFlow()
    fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val context = getApplication<Application>()
            val inputStream = context.contentResolver.openInputStream(uri)

            val fileName = "user_route_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun setSplashShown() {
        _splashShown.value = true
    }
    private fun normalizeToResourceName(name: String): String {
        val polishChars = mapOf(
            'ą' to 'a', 'ć' to 'c', 'ę' to 'e', 'ł' to 'l', 'ń' to 'n',
            'ó' to 'o', 'ś' to 's', 'ź' to 'z', 'ż' to 'z',
            'Ą' to 'a', 'Ć' to 'c', 'Ę' to 'e', 'Ł' to 'l', 'Ń' to 'n',
            'Ó' to 'o', 'Ś' to 's', 'Ź' to 'z', 'Ż' to 'z'
        )

        return name.lowercase()
            .map { polishChars[it] ?: it }
            .joinToString("")
            .replace(Regex("[\\s-]"), "_")
            .replace(Regex("[^a-z0-9_]"), "")
    }

    fun getRouteImage(name: String): Int? {
        val resourceName = normalizeToResourceName(name)
        val resId = getApplication<Application>().resources.getIdentifier(
            resourceName,
            "drawable",
            getApplication<Application>().packageName
        )
        return if (resId != 0) resId else null
    }
    fun setType(type: String)
    {
        selectedType.value=type
    }
    fun startTimer() {
        if (_isRunning.value) return
        _isRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000L)
                _time.value = _time.value + 1
            }
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()

        _time.value = 0
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveTime(nazwa: String)
    {
        viewModelScope.launch{
            timeDao.insertTime(nazwa,_time.value, LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
        }

    }
    fun getTimes(name: String): LiveData<List<Czasy>>
    {
        /*viewModelScope.launch{
             timeDao.getByName(name)
        }*/
        return timeDao.getByName(name)
    }
    fun getRouteData(name: String): LiveData<String> {
        return userDao.getRouteByName(name)
    }
    fun addRoute(name: String, description: String,type: String,imageUri: Uri?) {
        viewModelScope.launch {
            var savedPath: String? = null
            imageUri?.let {
                savedPath = saveImageToInternalStorage(it)
            }
            userDao.insertRoute(Trasy(nazwa = name, opis = description,typ=type,image_uri=savedPath))
        }
    }
    fun getNames() {
        viewModelScope.launch {
            userDao.getAllNames()
        }
    }
    fun getByType(type: String)
    {
        viewModelScope.launch {
            userDao.getByType(type)
        }
    }
    fun getDescription(name: String): LiveData<String> {
        viewModelScope.launch {
            userDao.getDescription(name)
        }
        return userDao.getDescription(name)
    }
    fun insertIfEmpty() {
        val trasy = mapOf("Rusałka-Strzeszyn" to listOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas tristique sapien ac est blandit pharetra.","Biegowa"), "Grunwald-Wilda" to listOf("Quisque sed ipsum sed diam aliquet pretium a quis massa.","Biegowa"), "Malta-Swarzędz" to listOf("Nunc purus arcu, rhoncus non tortor sit amet, auctor blandit lacus.","Rowerowa"), "Kiekrz-Morasko" to listOf("Curabitur aliquet sem eget ipsum vehicula imperdiet. Sed sapien velit, pellentesque ac facilisis id, vehicula vel ante.","Rowerowa"), "Górczyn-Stary Rynek" to listOf("Maecenas ut tincidunt diam, ut tristique justo. Nunc dignissim maximus porttitor. Ut fringilla vel velit vitae vehicula.","Biegowa"))
        var a=1
        viewModelScope.launch {



            if (userDao.getCount() == 0) {
                for ((name, opis) in trasy) {
                    userDao.insertRoute(Trasy(a, name, opis[0],opis[1]))
                    a = a + 1
                }

            }
        }
    }

}