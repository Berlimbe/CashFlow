package com.example.application

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "lancamentos",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Lancamento(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val descricao: String,
    val valor: Double,
    val tipo: String,
    val data: Long,
    val categoriaId: Int?
)