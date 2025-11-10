package com.example.application

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Insert
    suspend fun insertLancamento(lancamento: Lancamento)

    @Update
    suspend fun updateLancamento(lancamento: Lancamento)

    @Delete
    suspend fun deleteLancamento(lancamento: Lancamento)

    @Query("SELECT * FROM lancamentos ORDER BY data DESC")
    fun getAllLancamentos(): Flow<List<Lancamento>>


    @Insert
    suspend fun insertCategoria(categoria: Categoria)

    @Update
    suspend fun updateCategoria(categoria: Categoria)

    @Delete
    suspend fun deleteCategoria(categoria: Categoria)

    @Query("SELECT * FROM categorias ORDER BY nome ASC")
    fun getAllCategorias(): Flow<List<Categoria>>

    @Query("""
    SELECT 
        IFNULL(
            (SELECT SUM(valor) FROM lancamentos WHERE tipo = 'Receita'), 0.0
        ) - IFNULL(
            (SELECT SUM(valor) FROM lancamentos WHERE tipo = 'Despesa'), 0.0
        )
    """)
    fun getSaldoTotal(): Flow<Double?>

    @Query("SELECT * FROM lancamentos WHERE id = :id")
    suspend fun getLancamentoById(id: Int): Lancamento?

    @Query("""
    SELECT 
        C.nome AS nomeCategoria,
        SUM(L.valor) AS totalGasto
    FROM lancamentos L
    INNER JOIN categorias C ON L.categoriaId = C.id
    WHERE L.tipo = 'Despesa'
    GROUP BY C.nome
    ORDER BY totalGasto DESC
""")
    fun getGastosPorCategoria(): Flow<List<GastoPorCategoria>>
}