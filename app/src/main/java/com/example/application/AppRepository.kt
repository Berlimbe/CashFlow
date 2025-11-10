package com.example.application

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao, private val apiService: CurrencyApiService) {

    fun getAllLancamentos(): Flow<List<Lancamento>> {
        return appDao.getAllLancamentos()
    }

    fun getSaldoTotal(): Flow<Double?> {
        return appDao.getSaldoTotal()
    }

    fun getAllCategorias(): Flow<List<Categoria>> {
        return appDao.getAllCategorias()
    }

    suspend fun insertLancamento(lancamento: Lancamento) {
        appDao.insertLancamento(lancamento)
    }

    suspend fun insertCategoria(categoria: Categoria) {
        appDao.insertCategoria(categoria)
    }

    suspend fun getLancamentoById(id: Int): Lancamento? {
        return appDao.getLancamentoById(id)
    }

    suspend fun updateLancamento(lancamento: Lancamento) {
        appDao.updateLancamento(lancamento)
    }

    suspend fun deleteLancamento(lancamento: Lancamento) {
        appDao.deleteLancamento(lancamento)
    }

    suspend fun deleteCategoria(categoria: Categoria) {
        appDao.deleteCategoria(categoria)
    }

    suspend fun fetchUsdToBrlRate(): CurrencyRate {
        val response = apiService.getExchangeRates()

        val brlRate = response.rates["BRL"] ?: 0.0

        return CurrencyRate(
            moedaBase = "USD",
            taxaBRL = brlRate
        )
    }

    fun getGastosPorCategoria(): Flow<List<GastoPorCategoria>> {
        return appDao.getGastosPorCategoria()
    }
}