package br.com.desenvolvimentomobileandroid

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import br.com.desenvolvimentomobileandroid.screens.CriarConta
import br.com.desenvolvimentomobileandroid.screens.Home
import br.com.desenvolvimentomobileandroid.screens.Login
import br.com.desenvolvimentomobileandroid.ui.theme.DesenvolvimentoMobileAndroidTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

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
                                onNavigateBack = {
                                navController.popBackStack()
                            })
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
}


