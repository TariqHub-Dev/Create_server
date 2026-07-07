package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Application.configureRouting() {
    routing {
        get("/books") {
            val books = transaction {
                BookTable.selectAll().map {
                    Book(
                        id = it[BookTable.id],
                        title = it[BookTable.title],
                        author = it[BookTable.author],
                        isBorrowed = it[BookTable.isBorrowed]
                    )
                }
            }
            call.respond(books)
        }
        
        post("/books") {
            try {
                val newBook = call.receive<Book>()

                transaction {
                    BookTable.insert {
                        it[id] = newBook.id
                        it[title] = newBook.title
                        it[author] = newBook.author
                        it[isBorrowed] = newBook.isBorrowed
                    }
                }
                call.respond(
                    HttpStatusCode.Created,
                    mapOf("status" to "Buku berhasil di tambahkan!")
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Format data tidak valid"))
            }
        }
        get("/books/{id}") {

            val idParam = call.parameters["id"] ?: return@get

            val book = transaction {
                BookTable.selectAll()
                    .where { BookTable.id eq idParam }
                    .map {
                        Book(
                            id = it[BookTable.id],
                            title = it[BookTable.title],
                            author = it[BookTable.author],
                            isBorrowed = it[BookTable.isBorrowed]
                        )
                    }
                    .singleOrNull()
            }

            if (book != null) {
                call.respond(book)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Buku dengan ID $idParam Tidak di temukan")
                )
            }
        }

        delete("/books/{id}") {
            val idParam = call.parameters["id"] ?: return@delete

            val rowsDeleted = transaction {
                BookTable.deleteWhere { BookTable.id eq idParam }
            }

            if (rowsDeleted > 0) {
                call.respond(
                    HttpStatusCode.OK,
                    mapOf("status" to "Buku dengan ID $idParam Berhasil di hapus")
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Buku dengan ID $idParam Tidak di temukan")
                )
            }
        }

        put("/books/{id}") {
            val idParam = call.parameters["id"] ?: return@put

            try {
                val updateBook = call.receive<Book>()

                val rowsUpdated = transaction {
                    BookTable.update({ BookTable.id eq idParam }) {
                        it[title] = updateBook.title
                        it[author] = updateBook.author
                        it[isBorrowed] = updateBook.isBorrowed
                    }
                }

                if (rowsUpdated > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        mapOf("status" to "Buku dengan ID $idParam berhasil di perbarui!")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Buku dengan ID $idParam Tidak di temukan")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Gagal memperbarui data: ${e.message}")
                )
            }
        }
        patch("/books/{id}"){
            val idParam = call.parameters["id"] ?: return@patch

            try{
                val pathData = call.receive<BookPatch>()

                val rowsUpdated = transaction {
                    BookTable.update({ BookTable.id eq idParam}) {

                        if (pathData.title != null) {
                            it[title] = pathData.title
                        }
                        if (pathData.author != null){
                            it[author] = pathData.author
                        }
                        if (pathData.isBorrowed != null){
                            it[isBorrowed] = pathData.isBorrowed
                        }
                    }
                }
                if (rowsUpdated>0){
                    call.respond(HttpStatusCode.OK, mapOf("status" to "Buku dengan id $idParam berhasil di patch"))
            }else{
                call.respond(HttpStatusCode.NotFound, mapOf("status" to "Buku dengan id $idParam tidak di temukan"))
            }

            } catch (e : Exception){
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Gagal melakukan patch"))
            }
        }

        head("/books/{id}"){
            val idParam = call.parameters["id"] ?: return@head

            val isBookExits = transaction {
                BookTable.selectAll()
                    .where (BookTable.id eq idParam)
                    .any()
            }
            if (isBookExits){
                call.respond(HttpStatusCode.OK)
            }else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        options("/books"){

            call.response.headers.append(HttpHeaders.Allow, "GET,POST,OPTIONS")
            call.respond(HttpStatusCode.OK)
        }

        options("/books/{id}"){
            call.response.headers.append(HttpHeaders.Allow,"GET,PUT,PATCH,DELETE,OPTIONS")
            call.respond(HttpStatusCode.OK)
        }
    }
}