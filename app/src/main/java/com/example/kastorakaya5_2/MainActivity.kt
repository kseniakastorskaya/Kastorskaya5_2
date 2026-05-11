package com.example.kastorakaya5_2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        dbHelper = DatabaseHelper(this)

        resultTextView = findViewById(R.id.resultTextView)
        usernameEditText = findViewById(R.id.usernameEditText)
        bookIdEditText = findViewById(R.id.bookIdEditText)
        bookTitleEditText = findViewById(R.id.bookTitleEditText)
        bookAuthorEditText = findViewById(R.id.bookAuthorEditText)
        bookGenreEditText = findViewById(R.id.bookGenreEditText)
        bookYearEditText = findViewById(R.id.bookYearEditText)

        loadUsername()

        findViewById<Button>(R.id.saveUsernameButton).setOnClickListener {
            val username = usernameEditText.text.toString()
            if (username.isNotEmpty()) {
                sharedPreferences.edit().putString("username", username).apply()
                Toast.makeText(this, "Имя пользователя сохранено", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.deleteUsernameButton).setOnClickListener {
            sharedPreferences.edit().remove("username").apply()
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

    private fun loadUsername() {
        val username = sharedPreferences.getString("username", "Гость")
        usernameEditText.setText(username)
        resultTextView.text = "Добро пожаловать, $username!"
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