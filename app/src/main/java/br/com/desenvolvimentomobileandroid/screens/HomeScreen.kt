package br.com.desenvolvimentomobileandroid.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Home(
    onAlterarSenha: () -> Unit,
    excluirConta: (String) -> Unit,
    onNavigateBack : () -> Unit,
    email: String,
    senha: String
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "Home")
        Text(text = "Email: $email")
        Text(text = "Senha: $senha")

        OutlinedButton(
            onClick = {
                onAlterarSenha()
            },
            modifier = Modifier
                .padding(top = 16.dp)
        ) {
            Text(text = "Alterar Senha")
        }

        OutlinedButton(
            onClick = {
                excluirConta(email)
            },
            modifier = Modifier
                .padding(top = 16.dp)
        ){
            Text(text = "Excluir Conta")
        }

        ElevatedButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(top = 16.dp)
        ) {
            Text(text = "Voltar")
        }
    }
}