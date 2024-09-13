package br.com.desenvolvimentomobileandroid

import androidx.lifecycle.ViewModel

class UsuarioViewModel: ViewModel()  {
    var email: String? = null
    var senha: String? = null

    fun setUsuarioLogado(email: String, senha: String) {
        this.email = email
        this.senha = senha
    }
}