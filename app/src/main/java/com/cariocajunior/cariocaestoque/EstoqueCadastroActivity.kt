package com.cariocajunior.cariocaestoque

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cariocajunior.cariocaestoque.databinding.ActivityTelaCadastroEstoqueBinding

class EstoqueCadastroActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var binding: ActivityTelaCadastroEstoqueBinding
    private var editItem: EstoqueModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaCadastroEstoqueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#BEBEBE")

        dbHelper = DBHelper(this)

        val imageViewConfirm: ImageView = findViewById(R.id.imageViewConfirm)
        val imageViewBack: ImageView = findViewById(R.id.imageViewBack)

        imageViewConfirm.setOnClickListener {
            salvarDados()
        }

        imageViewBack.setOnClickListener {
            voltarTelaPrincipal()
        }

        editItem = intent.getParcelableExtra("editItem")
        editItem?.let { item ->
            binding.editTextName.setText(item.nome)
            binding.editTextModel.setText(item.modelo)
            binding.editTextQtd.setText(item.qtd.toString())
            binding.editTextWeight.setText(item.peso.toString())
            binding.editTextDescription.setText(item.description)
        }

    }

    private fun salvarDados() {
        val nome = binding.editTextName.text.toString()
        val modelo = binding.editTextModel.text.toString()
        val qtdText = binding.editTextQtd.text.toString()
        val pesoText = binding.editTextWeight.text.toString()

        // Verificações campo_obrigatorio
        if (nome.isEmpty()) {
            binding.editTextName.error = "Campo obrigatório!"
            binding.editTextName.requestFocus()
            return
        }
        if (qtdText.isEmpty()) {
            binding.editTextQtd.error = "Campo obrigatório!"
            binding.editTextQtd.requestFocus()
            return
        }

        val qtd: Int
        try {
            qtd = qtdText.toInt()
        } catch (e: NumberFormatException) {
            binding.editTextQtd.error = "A quantidade é um número muito alto ou não é válido."
            binding.editTextQtd.requestFocus()
            return
        }
        //msm q tiver vazio n vai lançar esse toast
        val peso: Int = if (pesoText.isNotEmpty()) {
            try {
                pesoText.toInt()
            } catch (e: NumberFormatException) {
                binding.editTextWeight.error = "O peso é um número muito alto ou não é válido."
                binding.editTextWeight.requestFocus()
                return
            }
        } else {
            0  // Valor padrão quando o campo está vazio
        }

        val descricao = binding.editTextDescription.text.toString()

        val estoqueModel = EstoqueModel(
            nome = nome,
            modelo = modelo,
            qtd = qtd,
            peso = peso,
            description = descricao
        )

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
            ?: return  // Retorna se o ID do usuário não estiver disponível

        val dbHelper = DBHelper(this)
        val sucesso = if (editItem == null) {
            dbHelper.insertItem(
                estoqueModel,
                userId
            )  // Associa o item ao usuário logado ao inseri-lo
        } else {
            estoqueModel.id = editItem?.id ?: -1
            dbHelper.updateItem(
                estoqueModel,
                userId
            )  // Passa o ID do usuário logado para a função updateItem()
        }

        //manda o toast se for de editar ou adiocionar
        val mensagem = if (editItem == null) {
            "Item adicionado com sucesso!"
        } else {
            "Item editado com sucesso!"
        }

        if (sucesso) {
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Falha ao adicionar/editar o item.", Toast.LENGTH_SHORT).show()
        }

        finish()
        val intent = Intent(this, TelaPrincipal::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun voltarTelaPrincipal() {
        val intent = Intent(this, TelaPrincipal::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

}