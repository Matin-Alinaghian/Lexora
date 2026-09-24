package com.lexora.app.ui.screens

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.repository.DictionaryDataSeeder
import com.lexora.app.data.repository.SeedStatus
import com.lexora.app.data.remote.dictionary.DictionaryResult
import com.lexora.app.data.repository.DictionaryRepository
import com.lexora.app.utils.TtsManager
import com.lexora.app.utils.SoundManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DictionaryUiState(
    val searchQuery: String = "",
    val results: List<DictionaryResult> = emptyList(),
    val isLoading: Boolean = false,
    val selectedResult: DictionaryResult? = null,
    val error: String? = null,
    val searchHistory: List<String> = emptyList()
)

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val ttsManager: TtsManager,
    private val soundManager: SoundManager,
    private val dictionaryDataSeeder: DictionaryDataSeeder
) : ViewModel() {

    private val _uiState = MutableStateFlow(DictionaryUiState())
    val uiState: StateFlow<DictionaryUiState> = _uiState.asStateFlow()

    
    val seedStatus: StateFlow<SeedStatus> = dictionaryDataSeeder.status

    private var mediaPlayer: MediaPlayer? = null
    private var searchJob: Job? = null

    init {
                loadSearchHistory()

                viewModelScope.launch {
            dictionaryDataSeeder.refreshWordCount()
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, error = null) }

        searchJob?.cancel()
        if (query.length < 2) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isLoading = true, error = null) }

            dictionaryRepository.searchDictionary(query)
                .collect { results ->
                    if (results.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                results = emptyList(),
                                isLoading = false,
                                error = "\"$query\" was not found in the offline dictionary."
                            )
                        }
                    } else {
                        addToSearchHistory(query)
                        _uiState.update {
                            it.copy(
                                results = results,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                }
        }
    }

    fun speak(word: String, audioUrl: String? = null) {
                stopBackgroundAudio()

        if (!audioUrl.isNullOrBlank()) {
            playOnlineAudio(audioUrl)
        } else {
            ttsManager.speak(word)
        }
    }

    private fun stopBackgroundAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        ttsManager.stop()
    }

    private fun playOnlineAudio(url: String) {
        viewModelScope.launch {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    prepareAsync()
                    setOnPreparedListener { start() }
                    setOnCompletionListener {
                        try { release() } catch (_: Exception) {}
                    }
                    setOnErrorListener { _, _, _ ->
                                                ttsManager.speak(_uiState.value.selectedResult?.word ?: "")
                        true
                    }
                }
            } catch (e: Exception) {
                ttsManager.speak(_uiState.value.selectedResult?.word ?: "")
            }
        }
    }

    fun selectResult(result: DictionaryResult?) {
        soundManager.playSound(SoundManager.SoundType.POPUP)
        _uiState.update { it.copy(selectedResult = result) }
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                results = emptyList(),
                selectedResult = null,
                error = null
            )
        }
    }

    private fun loadSearchHistory() {
            }

    private fun addToSearchHistory(query: String) {
        _uiState.update { state ->
            val history = (listOf(query) + state.searchHistory).distinct().take(20)
            state.copy(searchHistory = history)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
