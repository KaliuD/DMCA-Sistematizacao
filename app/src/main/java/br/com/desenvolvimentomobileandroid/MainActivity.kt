package br.com.desenvolvimentomobileandroid

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.desenvolvimentomobileandroid.data.AppDatabase
import br.com.desenvolvimentomobileandroid.model.User
import br.com.desenvolvimentomobileandroid.screens.AlterarSenha
import br.com.desenvolvimentomobileandroid.screens.CriarConta
import br.com.desenvolvimentomobileandroid.screens.Home
import br.com.desenvolvimentomobileandroid.screens.Login
import br.com.desenvolvimentomobileandroid.ui.theme.DesenvolvimentoMobileAndroidTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase
    private val usuarioViewModel: UsuarioViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getDatabase(this)  // Inicializa o banco de dados

        enableEdgeToEdge()
        setContent {
            DesenvolvimentoMobileAndroidTheme {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Meu App") },
                            navigationIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_action_name),
                                    contentDescription = "Logo do App",
                                    modifier = Modifier.size(50.dp) // Ajuste o tamanho da logo aqui
                                )
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(navController, startDestination = "CriarConta") {
                        composable("CriarConta") {
                            CriarConta(
                                innerPadding,
                                onCriarConta = { email, senha, senhaConfimacao ->
                                    // Lógica para salvar o login usando coroutines
                                    lifecycleScope.launch {
                                        val sucesso = inserirUsuario(email, senha,senhaConfimacao)
                                        if (sucesso) {
                                            navController.navigate("login")
                                            Toast.makeText(this@MainActivity, "Usuário criado com sucesso", Toast.LENGTH_SHORT).show()

                                        }else{
                                            Toast.makeText(this@MainActivity, "Falha ao criar usuário", Toast.LENGTH_SHORT).show()

                                        }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate("login")
                                }
                            )
                        }

                        composable("login") {
                            Login(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onVerificaUsuario = { email, senha ->
                                    // Lógica para verificar o login
                                    lifecycleScope.launch {
                                        val userDao = db.userDao()
                                        val user = userDao.getUserByEmail(email)
                                        if (user != null && user.password == senha) {
                                            withContext(Dispatchers.Main) {
                                                navController.navigate("home")
                                            }
                                            usuarioViewModel.setUsuarioLogado(email, senha)
                                            Toast.makeText(this@MainActivity, "Login bem-sucedido", Toast.LENGTH_SHORT).show()
                                        }else{
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(this@MainActivity, "Falha no login", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            Home(
                                onAlterarSenha = {
                                    navController.navigate("alterarSenha")
                                },
                                excluirConta = { email ->
                                    lifecycleScope.launch {
                                        val sucesso = excluirUsuario(email)
                                        if (sucesso) {
                                            navController.navigate("login")
                                            Toast.makeText(this@MainActivity, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show()
                                        }else{
                                            Toast.makeText(this@MainActivity, "Falha ao excluir a conta", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                email = usuarioViewModel.email.toString(),
                                senha = usuarioViewModel.senha.toString()

                            )
                        }

                        composable("alterarSenha") {
                            AlterarSenha(
                                innerPadding = innerPadding,
                                onAlterarSenha = { email, senhaAtual, novaSenha ->
                                    // Lógica para alterar a senha
                                    lifecycleScope.launch {
                                        val sucesso = alterarSenha(email, senhaAtual, novaSenha)
                                        if (sucesso) {
                                            navController.navigate("login")
                                            Toast.makeText(this@MainActivity, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show()
                                        }else{
                                            Toast.makeText(this@MainActivity, "Falha ao alterar a senha", Toast.LENGTH_SHORT).show()

                                        }
                                    }
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun inserirUsuario(
        email: String,
        senha: String,
        senhaConfimacao: String
    ): Boolean {
        val userDao = db.userDao()
        val existingUser = userDao.getUserByEmail(email)
        if (existingUser == null && senha == senhaConfimacao) {
            userDao.insertUser(User(email = email, password = senha))
            Toast.makeText(this, "Usuário criado com sucesso", Toast.LENGTH_SHORT).show()
            return true
        }else{
            return false
            Toast.makeText(this, "Falha ao criar usuário", Toast.LENGTH_SHORT).show()
        }
    }

    // Função para alterar a senha do usuário
    private suspend fun alterarSenha(email: String, senhaAtual: String, novaSenha: String): Boolean {
        val userDao = db.userDao()
        val existingUser = userDao.getUserByEmail(email)// Buscando o usuário
        if (existingUser != null && existingUser.password == senhaAtual) {
            userDao.updatePassword(email, novaSenha)
            return true
        }
        return false
    }

    private suspend fun excluirUsuario (email: String): Boolean {
        val userDao = db.userDao()
        val existingUser = userDao.getUserByEmail(email)
        if (existingUser != null) {
            userDao.deleteUserByEmail(email)
            return true
        }
        return false
    }

}


