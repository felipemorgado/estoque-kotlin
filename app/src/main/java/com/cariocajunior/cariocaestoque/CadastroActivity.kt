package com.cariocajunior.cariocaestoque

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.cariocajunior.cariocaestoque.databinding.ActivityTelaCadastroBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTelaCadastroBinding
    private lateinit var db: DBHelper
    private var isNewAccount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#FFFFFF")

        val textInputLayoutPass: TextInputLayout = findViewById(R.id.txtInputLayoutPass)
        val editTextPass: TextInputEditText = findViewById(R.id.editTextPass)
        val textInputConfirmPass: TextInputLayout = findViewById(R.id.txtInputLayoutConfirmPass)
        val editTextConfirmPass: TextInputEditText = findViewById(R.id.editTextConfirmPass)
        val textInputLayoutEmail: TextInputLayout = findViewById(R.id.txtInputLayoutEmail)
        db = DBHelper(this)

        binding.btnSign.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val pass = binding.editTextPass.text.toString()
            val confirmpass = binding.editTextConfirmPass.text.toString()
            val savedata = db.insertData(email, pass, confirmpass)
            //verificar os dominios
            val emailPattern = Regex("^\\S+@(gmail\\.com|hotmail\\.com)$")

            when {
                email.isEmpty() -> {
                    binding.editTextEmail.error = "Preencha o E-mail!"
                    binding.editTextEmail.requestFocus()
                }

                pass.isEmpty() -> {
                    textInputLayoutPass.endIconMode = TextInputLayout.END_ICON_NONE
                    binding.editTextPass.error = "Preencha a senha!"
                    binding.editTextPass.requestFocus()

                }

                confirmpass.isEmpty() -> {
                    textInputConfirmPass.endIconMode = TextInputLayout.END_ICON_NONE
                    binding.editTextConfirmPass.error = "Confirme a senha!"
                    binding.editTextConfirmPass.requestFocus()
                }

                !email.matches(emailPattern) -> {
                    textInputLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
                    binding.editTextEmail.error =
                        "Aceitamos somente e-mails com @gmail.com e @hotmail.com"
                    binding.editTextEmail.requestFocus()
                }

                pass.length <= 5 -> {
                    textInputLayoutPass.endIconMode = TextInputLayout.END_ICON_NONE
                    binding.editTextPass.error = "A senha precisa pelo menos ter 6 caracteres!"
                    binding.editTextPass.requestFocus()
                }

                confirmpass != pass -> {
                    textInputConfirmPass.endIconMode = TextInputLayout.END_ICON_NONE
                    binding.editTextConfirmPass.error = "As senhas não são iguais!"
                    binding.editTextConfirmPass.requestFocus()
                }

                else -> {
                    if (savedata) {
                        //toast
                        cadastrar()
                    } else {
                        textInputConfirmPass.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.editTextEmail.error = "Email já cadastrado!"
                        binding.editTextEmail.requestFocus()
                    }
                }
            }
        }
        binding.txtTelaLoginLink.setOnClickListener {
            navigationTelaLogin()
        }

        //Quando o usuario escrever algo na senha o togglepass ativa
        editTextPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nada pra fazer aqui
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Verificar se o campo de senha está vazio
                val isPassEmpty = s.isNullOrEmpty()

                // Configurar o passwordToggle baseado no estado do campo de senha
                textInputLayoutPass.endIconMode =
                    if (isPassEmpty) {
                        TextInputLayout.END_ICON_NONE
                    } else {
                        TextInputLayout.END_ICON_PASSWORD_TOGGLE
                    }
            }

            override fun afterTextChanged(s: Editable?) {
                // Nada pra fazer aqui
            }
        })

        // TextWatcher para o campo de confirmação de senha
        editTextConfirmPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nada pra fazer aqui
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Verificar se o campo de confirmação de senha está vazio
                val isConfirmPassEmpty = s.isNullOrEmpty()

                // Configurar o passwordToggle baseado no estado do campo de confirmação de senha
                textInputConfirmPass.endIconMode =
                    if (isConfirmPassEmpty) {
                        TextInputLayout.END_ICON_NONE
                    } else {
                        TextInputLayout.END_ICON_PASSWORD_TOGGLE
                    }
            }

            override fun afterTextChanged(s: Editable?) {
                // Nada pra fazer aqui
            }
        })

    }

    private fun cadastrar() {
        isNewAccount = true
        navigationTelaLogin()
        //fazer a logica
    }

    private fun navigationTelaLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        // Fechar a CadastroActivity e retornar à MainActivity
        finishAffinity()
    }

}