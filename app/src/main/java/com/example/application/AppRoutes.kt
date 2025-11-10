package com.example.application

object AppRoutes {
    val ADD_SCREEN: Any
        get() {
            TODO()
        }
    const val DASHBOARD = "dashboard"
    const val CATEGORIA_SCREEN = "categoria_screen"

    const val ADD_EDIT_ROUTE = "add_screen?lancamentoId={lancamentoId}"

    const val ARG_LANCAMENTO_ID = "lancamentoId"

    fun getAddRoute(): String {
        return "add_screen"
    }

    fun getEditRoute(id: Int): String {
        return "add_screen?lancamentoId=$id"
    }
}