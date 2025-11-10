package com.example.application

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var nomeCategoria by remember { mutableStateOf("") }
    val listaCategorias by viewModel.todasCategorias.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciar Categorias") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nomeCategoria,
                    onValueChange = { nomeCategoria = it },
                    label = { Text("Nova Categoria") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (nomeCategoria.isNotBlank()) {
                            viewModel.adicionarCategoria(Categoria(nome = nomeCategoria))
                            nomeCategoria = ""
                        }
                    },

                    enabled = nomeCategoria.isNotBlank()
                ) {
                    Text("Salvar")
                }
            }

            LazyColumn {
                items(listaCategorias) { categoria ->
                    CategoriaItem(
                        categoria = categoria,
                        onDeleteClick = {
                            viewModel.deleteCategoria(categoria)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriaItem(
    categoria: Categoria,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = categoria.nome,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))


            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir Categoria"
                )
            }
        }
    }
}