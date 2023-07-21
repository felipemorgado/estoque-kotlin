package com.cariocajunior.cariocaestoque

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "Userdata", null, 4) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table Userdata (username TEXT primary key, password TEXT)")
        db?.execSQL("create table Estoque (id INTEGER primary key autoincrement, nome TEXT, modelo TEXT, qtd INTEGER, peso INTEGER, description TEXT, user_id INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists Userdata")
        db?.execSQL("drop table if exists Estoque")
        onCreate(db)
    }

    fun insertData(username: String, password: String, confirmPass: String): Boolean {
        //req pra inserir no cadastro
        //F: tem outro jeito melhor de fazer isso?
        if (checkEmailExists(username)) {
            return false
        }
        if (password.isEmpty()) {
            return false
        }
        if (confirmPass.isEmpty()) {
            return false
        }
        if (password != confirmPass) {
            return false
        }
        if (username.endsWith("@gmail.com").not() && username.endsWith("@hotmail.com").not()) {
            return false
        }
        if (password.length < 6) {
            return false
        }

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("username", username)
        cv.put("password", password)
        val result = db.insert("Userdata", null, cv)
        db.close()

        return result != -1L
    }

    fun checkUserPass(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val query = "select * from Userdata where username = '$username' and password = '$password'"
        val cursor = db.rawQuery(query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    private fun checkEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM Userdata WHERE username = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(email))
        val emailExists = cursor.count > 0
        cursor.close()
        return emailExists
    }

    //TelaPrincipal

    fun insertItem(estoqueModel: EstoqueModel, userId: String): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("nome", estoqueModel.nome)
        cv.put("modelo", estoqueModel.modelo)
        cv.put("qtd", estoqueModel.qtd)
        cv.put("peso", estoqueModel.peso)
        cv.put("description", estoqueModel.description)
        cv.put("user_id", userId) // Associa o ID do usuário ao item
        val result = db.insert("Estoque", null, cv)
        return result != -1L
    }

    fun updateItem(estoqueModel: EstoqueModel, userId: String): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("nome", estoqueModel.nome)
        cv.put("modelo", estoqueModel.modelo)
        cv.put("qtd", estoqueModel.qtd)
        cv.put("peso", estoqueModel.peso)
        cv.put("description", estoqueModel.description)
        val result = db.update(
            "Estoque",
            cv,
            "id = ? AND user_id = ?",
            arrayOf(estoqueModel.id.toString(), userId)
        )
        return result > 0
    }

    fun getAllItems(userId: String): List<EstoqueModel> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Estoque WHERE user_id = ?", arrayOf(userId))
        val items = mutableListOf<EstoqueModel>()

        cursor.use {
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex("id")
                val nomeIndex = cursor.getColumnIndex("nome")
                val modeloIndex = cursor.getColumnIndex("modelo")
                val qtdIndex = cursor.getColumnIndex("qtd")
                val pesoIndex = cursor.getColumnIndex("peso")
                val descriptionIndex = cursor.getColumnIndex("description")

                val id = cursor.getInt(idIndex)
                val nome = cursor.getString(nomeIndex)
                val modelo = cursor.getString(modeloIndex)
                val qtd = cursor.getInt(qtdIndex)
                val peso = cursor.getInt(pesoIndex)
                val desc = cursor.getString(descriptionIndex)

                val item = EstoqueModel(id, nome, modelo, qtd, peso, desc)
                items.add(item)
            }
        }

        return items
    }

    fun deleteItem(item: EstoqueModel, userId: String): Boolean {
        val db = this.writableDatabase

        // Excluir o item pelo ID
        val result =
            db.delete("Estoque", "id = ? AND user_id = ?", arrayOf(item.id.toString(), userId))

        if (result != -1) {
            // Atualizar os IDs dos itens restantes
            val updateQuery =
                "UPDATE Estoque SET id = id - 1 WHERE id > ${item.id} AND user_id = '$userId'"
            db.execSQL(updateQuery)

            // Atualizar o último ID para o próximo ID disponível
            val nextIdQuery =
                "UPDATE sqlite_sequence SET seq = (SELECT MAX(id) FROM Estoque) WHERE name = 'Estoque'"
            db.execSQL(nextIdQuery)
        }

        db.close()
        return result != -1
    }

}
