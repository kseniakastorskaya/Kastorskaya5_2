package com.example.kastorakaya5_2

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var resultTextView: TextView
    private lateinit var usernameEditText: EditText
    private lateinit var bookIdEditText: EditText
    private lateinit var bookTitleEditText: EditText
    private lateinit var bookAuthorEditText: EditText
    private lateinit var bookGenreEditText: EditText
    private lateinit var bookYearEditText: EditText

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_USERNAME = "username"
        private const val DEFAULT_USERNAME = "Гость"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        dbHelper = DatabaseHelper(this)

        resultTextView = findViewById(R.id.resultTextView)
        usernameEditText = findViewById(R.id.usernameEditText)
        bookIdEditText = findViewById(R.id.bookIdEditText)
        bookTitleEditText = findViewById(R.id.bookTitleEditText)
        bookAuthorEditText = findViewById(R.id.bookAuthorEditText)
        bookGenreEditText = findViewById(R.id.bookGenreEditText)
        bookYearEditText = findViewById(R.id.bookYearEditText)

        val savedUsername = loadUsername()
        usernameEditText.setText(savedUsername)
        resultTextView.text = "Добро пожаловать, $savedUsername!"

        findViewById<Button>(R.id.saveUsernameButton).setOnClickListener {
            val username = usernameEditText.text.toString()
            if (username.isNotEmpty()) {
                saveUsername(username)
                Toast.makeText(this, "Имя пользователя сохранено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.deleteUsernameButton).setOnClickListener {
            deleteUsername()
            usernameEditText.setText("")
            Toast.makeText(this, "Имя пользователя удалено", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.addBookButton).setOnClickListener {
            addBook()
        }

        findViewById<Button>(R.id.findBookButton).setOnClickListener {
            findBook()
        }

        findViewById<Button>(R.id.updateBookButton).setOnClickListener {
            updateBook()
        }

        findViewById<Button>(R.id.deleteBookButton).setOnClickListener {
            deleteBook()
        }

        findViewById<Button>(R.id.showAllBooksButton).setOnClickListener {
            showAllBooks()
        }
    }

    private fun saveUsername(username: String): Boolean {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        return editor.commit()
    }

    private fun loadUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, DEFAULT_USERNAME) ?: DEFAULT_USERNAME
    }

    private fun updateUsername(newUsername: String): Boolean {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, newUsername)
        return editor.commit()
    }

    private fun deleteUsername(): Boolean {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(KEY_USERNAME)
        return editor.commit()
    }

    private fun addBook() {
        val title = bookTitleEditText.text.toString()
        val author = bookAuthorEditText.text.toString()
        val genre = bookGenreEditText.text.toString()
        val year = bookYearEditText.text.toString().toIntOrNull() ?: 0

        if (title.isNotEmpty() && author.isNotEmpty()) {
            val book = Book(title = title, author = author, genre = genre, year = year)
            if (dbHelper.addBook(book)) {
                Toast.makeText(this, "Книга добавлена", Toast.LENGTH_SHORT).show()
                clearBookFields()
            } else {
                Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Заполните название и автора", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findBook() {
        val id = bookIdEditText.text.toString().toIntOrNull()
        if (id != null) {
            val book = dbHelper.findBook(id)
            if (book != null) {
                bookTitleEditText.setText(book.title)
                bookAuthorEditText.setText(book.author)
                bookGenreEditText.setText(book.genre)
                bookYearEditText.setText(book.year.toString())
                resultTextView.text = "Найдена: ${book.title} (${book.author})"
            } else {
                Toast.makeText(this, "Книга не найдена", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Введите ID книги", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBook() {
        val id = bookIdEditText.text.toString().toIntOrNull()
        if (id != null) {
            val newTitle = bookTitleEditText.text.toString()
            val newAuthor = bookAuthorEditText.text.toString()
            val newGenre = bookGenreEditText.text.toString()
            val newYear = bookYearEditText.text.toString().toIntOrNull() ?: 0
            val updatedBook = Book(id, newTitle, newAuthor, newGenre, newYear)

            if (dbHelper.updateBook(id, updatedBook)) {
                Toast.makeText(this, "Книга обновлена", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Книга с ID=$id не найдена", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Введите ID книги", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteBook() {
        val id = bookIdEditText.text.toString().toIntOrNull()
        if (id != null) {
            if (dbHelper.deleteBook(id)) {
                Toast.makeText(this, "Книга удалена", Toast.LENGTH_SHORT).show()
                clearBookFields()
            } else {
                Toast.makeText(this, "Книга с ID=$id не найдена", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Введите ID книги", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAllBooks() {
        val books = dbHelper.getAllBooks()
        if (books.isEmpty()) {
            resultTextView.text = "База данных пуста"
        } else {
            resultTextView.text = books.joinToString("\n") { book ->
                "ID: ${book.id} | ${book.title} | ${book.author} | ${book.genre} | ${book.year}"
            }
        }
    }

    private fun clearBookFields() {
        bookIdEditText.setText("")
        bookTitleEditText.setText("")
        bookAuthorEditText.setText("")
        bookGenreEditText.setText("")
        bookYearEditText.setText("")
    }
}