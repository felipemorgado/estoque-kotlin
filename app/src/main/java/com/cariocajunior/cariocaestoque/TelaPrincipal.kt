package com.cariocajunior.cariocaestoque

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cariocajunior.cariocaestoque.databinding.ActivityTelaPrincipalBinding
import com.google.android.material.imageview.ShapeableImageView

class TelaPrincipal : AppCompatActivity() {

    private lateinit var binding: ActivityTelaPrincipalBinding
    private lateinit var imgViewLogout: ShapeableImageView
    private lateinit var adapter: ItemAdapter
    private lateinit var itemList: List<EstoqueModel>
    private var filteredItemList: MutableList<EstoqueModel> = mutableListOf()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#4E16E6")

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterItems(newText)
                return true
            }
        })

        //tirar o foco da searchview
        binding.recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                binding.searchView.clearFocus()
            }
            false
        }

        binding.searchView.isIconifiedByDefault = false // searchview maximizada

//        deslogar listener
        imgViewLogout = findViewById(R.id.imgViewLogout)
        imgViewLogout.setOnClickListener {
            logout()
            Toast.makeText(this, "Deslogado com sucesso!", Toast.LENGTH_SHORT).show()
        }

        binding.floatingActionButton.setOnClickListener {
            abrirTelaCadastroItem()
        }
        carregarItens()
    }

    private fun abrirTelaCadastroItem() {
        val intent = Intent(this, EstoqueCadastroActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.remove("userId")  // Remove o ID do usuário ao fazer logout
        editor.apply()

        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun carregarItens() {
        val dbHelper = DBHelper(this)
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
            ?: return // Retorna se o ID do usuário não estiver disponível
        itemList = dbHelper.getAllItems(userId) // Carrega apenas os itens do usuário logado
        filteredItemList.addAll(itemList)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        adapter = ItemAdapter(filteredItemList)
        binding.recyclerView.adapter = adapter
    }

    //    filtraritems pra searchview
    @SuppressLint("NotifyDataSetChanged")
    private fun filterItems(query: String) {
        filteredItemList.clear()

        if (query.isEmpty()) {
            filteredItemList.addAll(itemList)
        } else {
            val filteredItems = itemList.filter { item ->
                item.nome.contains(query, ignoreCase = true)
            }
            filteredItemList.addAll(filteredItems)
        }

        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: EstoqueModel) {
        val dbHelper = DBHelper(this)
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null) ?: return

        val success = dbHelper.deleteItem(item, userId)
        if (success) {
            Toast.makeText(this, "Item excluído com sucesso!", Toast.LENGTH_SHORT).show()

            // Remover o item da lista itemList
            itemList = itemList.filter { it.id != item.id }

            // Atualizar os IDs dos itens restantes na lista itemList
            for (i in itemList.indices) {
                itemList[i].id = i + 1
            }

            // Atualizar a lista filteredItemList com base na consulta atual da SearchView
            val query = binding.searchView.query.toString()
            filterItems(query)

            adapter.notifyDataSetChanged()

            binding.searchView.setQuery("", false)
            binding.searchView.clearFocus()
        } else {
            Toast.makeText(this, "Falha ao excluir o item.", Toast.LENGTH_SHORT).show()
        }
    }
}