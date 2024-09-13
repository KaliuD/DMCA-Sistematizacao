package br.com.desenvolvimentomobileandroid.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun Login(
    onNavigateBack: () -> Unit,
    onVerificaUsuario: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login")

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Digite seu email")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            maxLines = 1,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = senha,
            onValueChange = {
                senha = it
            },
            label = { Text(text = "Digite sua senha") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        OutlinedButton(
            onClick = {
                onVerificaUsuario(email, senha)
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            enabled = email.isNotBlank() && senha.isNotBlank(),
            shape = MaterialTheme.shapes.small,
        ) {
            Text(text = "Entrar")
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