package com.deneksepeti.app.clientSocket

import com.deneksepeti.app.data.model.LoggedInUser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ClientHandler {

    companion object {

        private lateinit var socket: Socket
        private lateinit var input: BufferedReader
        private lateinit var output: PrintWriter

        fun getAllPosts(): String {

            socket = Socket("ec2-3-89-104-95.compute-1.amazonaws.com", 3000)
            input = BufferedReader(InputStreamReader(socket.inputStream))
            output = PrintWriter(socket.outputStream)

            var request: String =
                    "getPost\na\n"
            output.println(request)
            output.flush()

            var response: String = ""

            var line: String = input.readLine()
            while (line != null && line.length != 0) {
                response += line + "\n"
                line = input.readLine()
            }

            input.close()
            output.close()
            socket.close()

            return response
        }

        fun applyPost(postId: String, userId: String): String {
            socket = Socket("ec2-3-89-104-95.compute-1.amazonaws.com", 3000)
            input = BufferedReader(InputStreamReader(socket.inputStream))
            output = PrintWriter(socket.outputStream)

            var request: String =
                    "applyPost\n"+
                            "${postId}\n"+
                            "${userId}\n"
            output.println(request)
            output.flush()

            var response: String = ""

            var line: String = input.readLine()
            while (line != null && line.length != 0) {
                response += line + "\n"
                line = input.readLine()
            }

            input.close()
            output.close()
            socket.close()

            return response.trim()
        }

        fun createPost(request: String): String {
            socket = Socket("ec2-3-89-104-95.compute-1.amazonaws.com", 3000)
            input = BufferedReader(InputStreamReader(socket.inputStream))
            output = PrintWriter(socket.outputStream)

            var request = "addPost\n" + request.plus("\n")
            output.println(request)
            output.flush()

            var response: String = ""

            var line: String = input.readLine()
            while (line != null && line.length != 0) {
                response += line + "\n"
                line = input.readLine()
            }

            input.close()
            output.close()
            socket.close()

            return response.trim()
        }

        fun deletePost(postId: String): String {
            socket = Socket("ec2-3-89-104-95.compute-1.amazonaws.com", 3000)
            input = BufferedReader(InputStreamReader(socket.inputStream))
            output = PrintWriter(socket.outputStream)

            var request = "deletePost\n" + postId.plus("\n")
            output.println(request)
            output.flush()

            var response: String = ""

            var line: String = input.readLine()
            while (line != null && line.length != 0) {
                response += line + "\n"
                line = input.readLine()
            }

            input.close()
            output.close()
            socket.close()

            return response.trim()
        }

        fun login(username: String, password: String): LoggedInUser {
            socket = Socket("ec2-3-89-104-95.compute-1.amazonaws.com", 3000)
            input = BufferedReader(InputStreamReader(socket.inputStream))
            output = PrintWriter(socket.outputStream)

            var request = "login\n" + """
                { "password": "$password", "email": "$username" }
            """.trimIndent().plus("\n")
            output.println(request)
            output.flush()

            val userId = input.readLine().trim()
            if ( userId == "401" || userId == "404" ) {
                input.close()
                output.close()
                socket.close()
                throw Error()
            }

            val displayName = input.readLine().trim()

            input.close()
            output.close()
            socket.close()

            return LoggedInUser(userId, displayName)
        }

        fun register(displayName: String, username: String, password: String): LoggedInUser {
            socket = Socket("ec2-3-89-104-95.compute-1.amazonaws.com", 3000)
            input = BufferedReader(InputStreamReader(socket.inputStream))
            output = PrintWriter(socket.outputStream)

            var request = "signup\n" + """
                { "password": "$password", "email": "$username", "displayName": "$displayName" }
            """.trimIndent().plus("\n")
            output.println(request)
            output.flush()

            val userId = input.readLine().trim()
            if (userId == "400") {
                input.close()
                output.close()
                socket.close()
                throw Error()
            }

            input.close()
            output.close()
            socket.close()

            return LoggedInUser(userId, displayName)
        }

    }



}