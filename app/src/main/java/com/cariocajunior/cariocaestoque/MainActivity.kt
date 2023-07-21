package com.cariocajunior.cariocaestoque

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.cariocajunior.cariocaestoque.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbh: DBHelper
    private lateinit var sharedPreferences: SharedPreferences
    private var isNewAccount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#FFFFFF")

        val textInputLayoutPass: TextInputLayout = findViewById(R.id.txtInputLayoutPass)
        val editTextPass: TextInputEditText = findViewById(R.id.editTextPass)
        val textInputLayoutEmail: TextInputLayout = findViewById(R.id.txtInputLayoutEmail)
        dbh = DBHelper(this)
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        //verificar os dominios
        val emailPattern = Regex("^\\S+@(gmail\\.com|hotmail\\.com)$")

        // Verifica se o usuário está logado
        if (isLoggedIn()) {
            // Obtém o valor de isNewAccount do Intent (definido anteriormente na CadastroActivity)
            isNewAccount = intent.getBooleanExtra("isNewAccount", false)

            // Exibe o "Toast" de boas-vindas somente quando o login for bem-sucedido e a conta não for nova
            if (!isNewAccount) {
                val email = sharedPreferences.getString("userId", "")
                val userName = email?.split("@")?.get(0) ?: ""
                Toast.makeText(this, "Bem-vindo de volta, $userName!", Toast.LENGTH_SHORT).show()
            }

            navegationTelaPrincipal()
            return
        }

        //F: parte da tela de login, eu devia ter feito uma activity pra tela de login
        //F: mas agr vai aqui na main msm
        //listener pro button de login
        binding.btnEnter.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val pass = binding.editTextPass.text.toString()

            when {
                email.isEmpty() -> {
                    binding.editTextEmail.error = "Preencha o e-mail!"
                    binding.editTextEmail.requestFocus()
                }

                pass.isEmpty() -> {
                    textInputLayoutPass.endIconMode = TextInputLayout.END_ICON_NONE
                    binding.editTextPass.error = "Preencha a senha!"
                    binding.editTextPass.requestFocus()
                }

                !email.matches(emailPattern) -> {
                    textInputLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
                    binding.editTextEmail.error =
                        "E-mail inválido!"
                    binding.editTextEmail.requestFocus()
                }

                pass.length <= 5 -> {
                    textInputLayoutPass.endIconMode = TextInputLayout.END_ICON_NONE
                    binding.editTextPass.error = "A senha precisa pelo menos ter 6 caracteres!!"
                    binding.editTextPass.requestFocus()
                }

                else -> {
                    //Login, o checkuser vai checar no banco de dados
                    val checkuser = dbh.checkUserPass(email, pass)
                    if (checkuser) {
                        saveLoginState(
                            true,
                            email
                        ) // Salva o estado de login como verdadeiro e o ID do usuário logado
                        navegationTelaPrincipal()
                    } else {
                        textInputLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.editTextEmail.error = "E-mail ou senha incorretos!"
                        binding.editTextEmail.requestFocus()
                    }
                }
            }
        }

        //tela pra se cadastrar
        binding.txtTelaCadastroLink.setOnClickListener {
            openTelaCadastro()
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


    }

    private fun isLoggedIn(): Boolean {
        val userId = sharedPreferences.getString("userId", null)
        return userId != null
    }

    private fun saveLoginState(isLoggedIn: Boolean, userId: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.putString("userId", userId) // Salva o ID do usuário logado
        editor.apply()
    }

//    private fun login() {
//        //vai mostrar a progressbar + delay
//        val progressbar = binding.progressBar
//        progressbar.visibility = View.VISIBLE
//
//        binding.btnEnter.isEnabled = false
//        binding.btnEnter.setTextColor(Color.parseColor("#000000"))
//
//        //vai tirar o teclado da frente dps de clicar no button
//        val inputMethodManager =
//            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            navegationTelaPrincipal()
//        }, 1500)
//    }

    private fun navegationTelaPrincipal() {
        val intent = Intent(this, TelaPrincipal::class.java)
        intent.putExtra("isNewAccount", isNewAccount)
        startActivity(intent)
        finish()
    }

    private fun openTelaCadastro() {
        val intent = Intent(this, CadastroActivity::class.java)
        startActivity(intent)

    }

}