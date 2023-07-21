package com.cariocajunior.cariocaestoque

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val itemList: List<EstoqueModel>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // Array de frases exclusao
    private val messages = arrayOf(
        "Dica: Você também pode clicar e segurar no item para excluir!",
        "Dica: Você pode criar outras contas e cadastrar novos itens!",
        "Dica: Para editar um item basta clicar em cima dele!",
        // Adicione mais frases aqui...
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_item_std, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)

        holder.itemView.setOnLongClickListener {
            exibirCaixaDialogoExclusao(holder.itemView.context, item)
            true
        }

        holder.imageViewDelete.setOnClickListener {
            exibirCaixaDialogoExclusao(holder.itemView.context, item)
        }

        holder.itemView.setOnClickListener {
            val item = itemList[position]
            val intent = Intent(holder.itemView.context, EstoqueCadastroActivity::class.java)
            intent.putExtra("editItem", item)
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun exibirCaixaDialogoExclusao(context: Context, item: EstoqueModel) {
        val randomMessage = messages.random() // Seleciona uma frase aleatória

        val dialog = AlertDialog.Builder(context)
            .setTitle("Deseja excluir este item?")
            .setMessage(randomMessage)
            .setPositiveButton("Excluir") { _, _ ->
                (context as? TelaPrincipal)?.deleteItem(item)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.txtViewProduct)
        private val textViewModel: TextView = itemView.findViewById(R.id.txtViewModel)
        private val textViewQtd: TextView = itemView.findViewById(R.id.txtViewQtd)
        private val textViewWeight: TextView = itemView.findViewById(R.id.txtViewWeight)
        private val textViewDescription: TextView = itemView.findViewById(R.id.txtViewDescription)
        val imageViewDelete: ImageView = itemView.findViewById(R.id.imageViewDelete)

        fun bind(item: EstoqueModel) {
            textViewName.text = item.nome
            textViewModel.text = item.modelo
            textViewQtd.text = item.qtd.toString()
            textViewWeight.text = item.peso.toString()
            textViewDescription.text = item.description
        }
    }
}