package com.example.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    private val _cotacaoAtual = MutableStateFlow<CurrencyRate?>(null)
    val cotacaoAtual = _cotacaoAtual.asStateFlow()

    fun fetchExchangeRate() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val rate = repository.fetchUsdToBrlRate()
                    _cotacaoAtual.update { rate }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    init {
        fetchExchangeRate()
    }

    val todosLancamentos: StateFlow<List<Lancamento>> = repository.getAllLancamentos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val saldoTotal: StateFlow<Double?> = repository.getSaldoTotal()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val gastosPorCategoria: StateFlow<List<GastoPorCategoria>> = repository.getGastosPorCategoria()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun adicionarLancamento(lancamento: Lancamento) {
        viewModelScope.launch {
            repository.insertLancamento(lancamento)
        }
    }

    val todasCategorias: StateFlow<List<Categoria>> = repository.getAllCategorias()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = emptyList()
        )

    fun adicionarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            repository.insertCategoria(categoria)
        }
    }
    private val _lancamentoSelecionado = MutableStateFlow<Lancamento?>(null)
    val lancamentoSelecionado = _lancamentoSelecionado.asStateFlow()

    fun loadLancamento(id: Int) {
        viewModelScope.launch {
            _lancamentoSelecionado.value = repository.getLancamentoById(id)
        }
    }

    fun clearLancamento() {
        _lancamentoSelecionado.value = null
    }

    suspend fun updateLancamento(lancamento: Lancamento) {
        viewModelScope.launch {
            repository.updateLancamento(lancamento)
        }
    }

    fun deleteCategoria(categoria: Categoria) {
        viewModelScope.launch {
            repository.deleteCategoria(categoria)
        }
    }

    fun deleteLancamento(lancamento: Lancamento) {
        viewModelScope.launch {
            repository.deleteLancamento(lancamento)
        }
    }
}