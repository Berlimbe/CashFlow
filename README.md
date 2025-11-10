# PROJETO FINAL: Controle Financeiro Pessoal (CashFlow App)

O **CashFlow App** é um aplicativo Android desenvolvido em Kotlin e Jetpack Compose com o objetivo de auxiliar o usuário no controle de suas finanças pessoais de forma prática e offline, garantindo o registro e a visualização organizada de receitas e despesas.

---

## 1. Informações e Membros

### Integrantes da Equipe

| Nome | Área de Contribuição Primária |
| :--- | :--- |
| **Marlon Zorzi Kososki** | Gerência de Dados (Room, Entidades, Queries), otimizou o código e limpou comentários desnecessários |
| **Carlos Eduardo Bittencourt da Costa** | Arquitetura, ViewModel e Integração (Coroutines, Retrofit), auxiliou na implantação e pesquisa da apí externa retrofit |
| **Bernardo Aurélio Almeida Rosa** | Interface do Usuário (Compose), Navegação entre páginas (Navigation Compose) e Experiência do Usuário (UX), testes de funcionalidades |

### Tecnologias Utilizadas

* **Linguagem:** Kotlin
* **Ambiente:** Android Studio
* **Arquitetura:** MVVM (Model-View-ViewModel)
* **Persistência:** Room Database
* **UI Toolkit:** Jetpack Compose, Material 3
* **Assíncrono:** Kotlin Coroutines / Flow
* **Navegação:** Jetpack Compose Navigation
* **API Externa:** Retrofit (para a melhoria opcional)

---

## 2. Implementação dos Requisitos Funcionais (RF)

Todos os requisitos obrigatórios foram implementados com sucesso:

| Requisito Funcional | Implementação | Detalhe da Solução |
| :--- | :--- | :--- |
| **RF01:** Cadastrar Lançamentos (CRUD: Create) | Completo | Implementado na `AddScreen`. Permite informar Descrição, Valor, Tipo (Receita/Despesa), Data (`DatePicker`) e **Categoria** (Dropdown). |
| **RF02:** Editar e Excluir (CRUD: Update, Delete) | Completo | A `DashboardScreen` torna os itens clicáveis. A navegação passa o ID do item para a `AddScreen`, que carrega o formulário para edição ou exclusão. |
| **RF03:** Listar Lançamentos (CRUD: Read) | Completo | `DashboardScreen` exibe a lista completa de lançamentos em um `LazyColumn`, ordenados por data. |
| **RF04:** Saldo Total e Resumo por Categoria | Completo | **Saldo Total:** Exibido no Dashboard com correção no SQL (`IFNULL`) para garantir atualização imediata, mesmo com lançamentos únicos. **Resumo por Categoria:** Exibido no Dashboard utilizando uma query `GROUP BY` no Room. |
| **RF05:** Armazenamento Persistente | Completo | Uso obrigatório do Room Database. O aplicativo é 100% funcional offline. |

---

## 3. Detalhamento Arquitetural e Estrutural

### A. Padrão Arquitetural (MVVM)

O projeto segue rigorosamente o padrão **MVVM**:
* **View (UI):** Funções `@Composable` (ex: `DashboardScreen`, `AddScreen`). Observam os dados reativos (`StateFlow/collectAsState`) do ViewModel.
* **ViewModel:** (`MainViewModel`). Gerencia o estado e a lógica de negócios, interagindo com o Repositório e expondo dados para a UI via `StateFlow`.
* **Model/Data Layer:** (`AppRepository`, `AppDao`, `AppDatabase`). Responsável por coordenar o acesso ao Room e ao Retrofit.

### B. Diagrama de Navegação

O esquema de navegação é gerenciado pelo `AppNavigation` (NavHost), utilizando rotas dinâmicas:

```mermaid
graph TD
    A[DashboardScreen (RF03/RF04)] -->|Ícone +| B(AddScreen: Modo Cadastro)
    A -->|Clique no Item| B
    A -->|Ícone Categoria| C(CategoriaScreen)
    B -->|Salvar/Excluir| A
    C -->|Voltar| A
```

### Estrutura do Banco de Dados (Room)

erDiagram
    CATEGORIA {
        int id PK "auto incremento"
        string nome
    }

    LANCAMENTO {
        int id PK "auto incremento"
        string descricao
        double valor
        string tipo "Receita ou Despesa"
        long data
        int categoriaId FK "Pode ser Nulo"
    }

    CATEGORIA ||--o{ LANCAMENTO : tem

---

## 4. Detalhes da API Externa (Retrofit)

Detalhe:	Implementação
Finalidade:	Exibir a taxa de câmbio USD para BRL (Real Brasileiro) no Dashboard.
URL Base:	https://api.exchangerate-api.com/
Endpoint Utilizado:	/v4/latest/USD
Método: HTTP	GET
Integração:	O MainViewModel utiliza Coroutines para chamar o AppRepository, que por sua vez utiliza o serviço Retrofit.

## 5. Instruções para Execução

- Requisitos: Certifique-se de ter o Android Studio (versão compatível com AGP 8.1.4 e Kotlin 1.9.0) instalado.
- Clonar o Repositório: Clone este repositório Git público.
- Sincronizar Gradle: Abra o projeto no Android Studio e aguarde a sincronização automática do Gradle.
- Permissão de Internet: A permissão android.permission.INTERNET já está incluída no AndroidManifest.xml para a funcionalidade Retrofit.
- Executar: Execute o aplicativo em um emulador ou dispositivo físico (API 25+).
- Teste de Categorias: Para usar a função de cadastro, vá ao ícone de Categoria (canto superior direito) e adicione categorias antes de fazer novos lançamentos.
