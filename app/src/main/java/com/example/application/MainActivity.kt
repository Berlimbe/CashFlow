package com.example.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.application.ui.theme.ApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val application = (LocalContext.current.applicationContext as FinanceApp)
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(application.repository)
    )

    NavHost(
        navController = navController,
        startDestination = AppRoutes.DASHBOARD
    ) {

        composable(AppRoutes.DASHBOARD) {
            DashboardScreen(
                viewModel = viewModel,
                onAddClick = {
                    navController.navigate(AppRoutes.getAddRoute())
                },
                onCategoryClick = {
                    navController.navigate(AppRoutes.CATEGORIA_SCREEN)
                },
                onItemClick = { id ->
                    navController.navigate(AppRoutes.getEditRoute(id))
                }
            )
        }

        composable(
            route = AppRoutes.ADD_EDIT_ROUTE,
            arguments = listOf(
                navArgument(AppRoutes.ARG_LANCAMENTO_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { navBackStackEntry ->
            val lancamentoId = navBackStackEntry.arguments?.getInt(AppRoutes.ARG_LANCAMENTO_ID) ?: -1

            AddScreen(
                lancamentoId = lancamentoId,
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.CATEGORIA_SCREEN) {
            CategoriaScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onAddClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    val listaDeLancamentos by viewModel.todosLancamentos.collectAsState()
    val saldoTotal by viewModel.saldoTotal.collectAsState()
    val gastosPorCategoria by viewModel.gastosPorCategoria.collectAsState()
    val cotacao by viewModel.cotacaoAtual.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Dashboard") },
                actions = {
                    IconButton(onClick = onCategoryClick) {
                        Icon(Icons.Default.Category, "Gerenciar Categorias")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, "Adicionar Lançamento")
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Saldo Total", fontSize = 20.sp)
            val saldoColor = if ((saldoTotal ?: 0.0) >= 0) Color(0xFF00C853) else Color.Red
            Text(
                text = "R$ ${String.format("%.2f", saldoTotal ?: 0.0)}",
                fontSize = 32.sp,
                color = saldoColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            val taxa = cotacao?.taxaBRL
            Text(
                text = if (taxa != null) {
                    "Cotação USD/BRL: R$ ${String.format("%.3f", taxa)}"
                } else {
                    "Carregando cotação..."
                },
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Resumo de Gastos (Despesas)", fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start))

            if (gastosPorCategoria.isEmpty()) {
                Text(text = "Nenhum gasto por categoria registrado.", modifier = Modifier.padding(top = 8.dp))
            } else {
                gastosPorCategoria.forEach { resumo ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = resumo.nomeCategoria)
                        Text(text = "R$ ${String.format("%.2f", resumo.totalGasto)}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "--- Lançamentos Recentes ---", modifier = Modifier.padding(bottom = 8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listaDeLancamentos) { lancamento ->
                    LancamentoItem(
                        lancamento = lancamento,
                        onClick = { onItemClick(lancamento.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun LancamentoItem(
    lancamento: Lancamento,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val valorColor = if (lancamento.tipo == "Receita") Color(0xFF00C853) else Color.Red

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = lancamento.descricao, fontSize = 18.sp)
                Text(text = "Data: ${java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(lancamento.data))}", fontSize = 12.sp)
            }
            Text(
                text = "R$ ${String.format("%.2f", lancamento.valor)}",
                color = valorColor,
                fontSize = 18.sp
            )
        }
    }
}