package com.example.coinconverter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinconverter.data.model.ExchangeResponseValue
import com.example.coinconverter.domain.GetExchangeValueUseCase
import com.example.coinconverter.domain.SaveExchangeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class MainViewModel(
    private val saveExchangeUseCase: SaveExchangeUseCase,
    private val getExchangeValueUseCase: GetExchangeValueUseCase
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun getExchangeValue(coins: String) {
        viewModelScope.launch {
            getExchangeValueUseCase(coins)
                .flowOn(Dispatchers.Main)
                .onStart {
                    //Dialog de progresso
                    _state.value = State.Loading
                }
                .catch {
                    // Erro
                    _state.value = State.Error(it)
                }
                .collect {
                    //Sucesso
                    _state.value = State.Success(it)
                }
        }
    }

    fun saveExchange(exchange: ExchangeResponseValue) {
        viewModelScope.launch {
            saveExchangeUseCase(exchange)
                .flowOn(Dispatchers.Main)
                .onStart {
                    //Dialog de progresso
                    _state.value = State.Loading
                }
                .catch {
                    // Erro
                    _state.value = State.Error(it)
                }
                .collect {
                    //Sucesso
                    _state.value = State.Saved
                }
        }
    }

    sealed class State {
        object Loading: State()
        object Saved: State()

        data class Success(val exchange: ExchangeResponseValue) : State()
        data class Error(val error: Throwable) : State()
    }
}