package com.example.application

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    lancamentoId: Int,
    viewModel: MainViewModel,
    onBack: () -> Unit,
) {
    val isEditMode = lancamentoId != -1
    val lancamentoSelecionado by viewModel.lancamentoSelecionado.collectAsState()
    val listaCategorias by viewModel.todasCategorias.collectAsState()

    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Despesa") }
    var categoriaMenuAberto by remember { mutableStateOf(false) }
    var categoriaSelecionada by remember { mutableStateOf<Categoria?>(null) }
    var dataSelecionada by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(isEditMode) }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dataSelecionada)
    val scope = rememberCoroutineScope() // Escopo para a lógica assíncrona do botão Salvar


    LaunchedEffect(key1 = Unit) {
        if (isEditMode) {
            viewModel.loadLancamento(lancamentoId)
        } else {
            viewModel.clearLancamento()
            isLoading = false
        }
    }

    LaunchedEffect(key1 = lancamentoSelecionado) {
        if (lancamentoSelecionado != null && isEditMode) {
            val item = lancamentoSelecionado!!


            descricao = item.descricao
            valor = item.valor.toString()
            tipo = item.tipo
            dataSelecionada = item.data

            isLoading = false // Para de carregar
        }
    }

    LaunchedEffect(key1 = lancamentoSelecionado, key2 = listaCategorias) {
        if (isEditMode && lancamentoSelecionado != null && listaCategorias.isNotEmpty()) {
            categoriaSelecionada = listaCategorias.find { it.id == lancamentoSelecionado!!.categoriaId }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dataSelecionada = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val onBackClean: () -> Unit = {
        viewModel.clearLancamento()
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Lançamento" else "Adicionar Lançamento") },
                navigationIcon = {
                    IconButton(onClick = onBackClean) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text(text = "Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it },
                    label = { Text(text = "Valor (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateFormatter.format(Date(dataSelecionada)),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Data") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = categoriaSelecionada?.nome
                            ?: if (listaCategorias.isEmpty()) "Nenhuma categoria. Adicione primeiro."
                            else "Selecione uma Categoria",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = if (listaCategorias.isNotEmpty()) {
                            { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaMenuAberto) }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = listaCategorias.isNotEmpty()) { categoriaMenuAberto = true }
                    )

                    if (listaCategorias.isNotEmpty()) {
                        DropdownMenu(
                            expanded = categoriaMenuAberto,
                            onDismissRequest = { categoriaMenuAberto = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            listaCategorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.nome) },
                                    onClick = {
                                        categoriaSelecionada = categoria
                                        categoriaMenuAberto = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = { tipo = "Receita" }) { Text(text = "Receita") }
                    Button(onClick = { tipo = "Despesa" }) { Text(text = "Despesa") }
                }
                Text(text = "Tipo selecionado: $tipo")
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        scope.launch {
                            val valorDouble = valor.toDoubleOrNull() ?: 0.0

                            if (!isEditMode && categoriaSelecionada == null) {
                                return@launch
                            }

                            val lancamento = Lancamento(
                                id = if (isEditMode) lancamentoId else 0,
                                descricao = descricao,
                                valor = valorDouble,
                                tipo = tipo,
                                data = dataSelecionada,
                                categoriaId = categoriaSelecionada?.id
                            )

                            if (isEditMode) {
                                viewModel.updateLancamento(lancamento)
                            } else {
                                viewModel.adicionarLancamento(lancamento)
                            }
                            onBackClean()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isEditMode) "Salvar Alterações" else "Salvar")
                }

                if (isEditMode) {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.deleteLancamento(lancamentoSelecionado!!)
                                onBackClean()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Excluir Lançamento")
                    }
                }
            }
        }
    }
}