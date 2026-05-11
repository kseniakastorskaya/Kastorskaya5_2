package com.example.kastorakaya5_2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "library.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_BOOKS = "books"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_AUTHOR = "author"
        private const val COLUMN_GENRE = "genre"
        private const val COLUMN_YEAR = "year"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE IF NOT EXISTS $TABLE_BOOKS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_AUTHOR TEXT, " +
                "$COLUMN_GENRE TEXT, " +
                "$COLUMN_YEAR INTEGER)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
        onCreate(db)
    }

    // Добавление новой книги
    fun addBook(book: Book): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, book.title)
            put(COLUMN_AUTHOR, book.author)
            put(COLUMN_GENRE, book.genre)
            put(COLUMN_YEAR, book.year)
        }
        val result = db.insert(TABLE_BOOKS, null, values)
        db.close()
        return result != -1L
    }

    // Поиск книги по ID
    fun findBook(id: Int): Book? {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.query(
                TABLE_BOOKS,
                arrayOf(COLUMN_ID, COLUMN_TITLE, COLUMN_AUTHOR, COLUMN_GENRE, COLUMN_YEAR),
                "$COLUMN_ID = ?",
                arrayOf(id.toString()),
                null, null, null
            )
            if (cursor.moveToFirst()) {
                return Book(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4)
                )
            }
        } finally {
            cursor?.close()
            db.close()
        }
        return null
    }

    // Получение всех книг
    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_BOOKS", null)
            if (cursor.moveToFirst()) {
                do {
                    val book = Book(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                    )
                    books.add(book)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
            db.close()
        }
        return books
    }

    // Обновление данных книги
    fun updateBook(id: Int, newBook: Book): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, newBook.title)
            put(COLUMN_AUTHOR, newBook.author)
            put(COLUMN_GENRE, newBook.genre)
            put(COLUMN_YEAR, newBook.year)
        }
        val result = db.update(TABLE_BOOKS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    // Удаление книги по ID
    fun deleteBook(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_BOOKS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}