package com.example.datossinmvvm_lab07

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser(modifier: Modifier) {
    val context = LocalContext.current
    var db: UserDatabase
    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser = remember { mutableStateOf("") }

    db = crearDatabase(context)
    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios") },
                actions = {
                    // Botón para agregar usuario
                    Button(
                        onClick = {
                            val user = User(0, firstName, lastName)
                            coroutineScope.launch {
                                AgregarUsuario(user = user, dao = dao)
                            }
                            firstName = ""
                            lastName = ""
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Agregar Usuario", fontSize = 14.sp)
                    }

                    // Botón para listar usuarios
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val data = getUsers(dao = dao)
                                dataUser.value = data
                            }
                        }
                    ) {
                        Text("Listar Usuarios", fontSize = 14.sp)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // Campo de ID (solo lectura)
            TextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID (solo lectura)") },
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Campo de First Name
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name:") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Campo de Last Name
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name:") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Botón para eliminar último usuario
            Button(
                onClick = {
                    coroutineScope.launch {
                        EliminarUltimoUsuario(dao = dao)
                        val data = getUsers(dao = dao)
                        dataUser.value = data
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Eliminar Último Usuario", fontSize = 16.sp)
            }

            Spacer(Modifier.height(24.dp))

            // Mostrar lista de usuarios
            Text(
                text = dataUser.value,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

suspend fun EliminarUltimoUsuario(dao: UserDao) {
    try {
        dao.deleteLast()
    } catch (e: Exception) {
        Log.e("User", "Error: deleteLast: ${e.message}")
    }
}

@Composable
fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun getUsers(dao: UserDao): String {
    var rpta: String = ""
    val users = dao.getAll()
    users.forEach { user ->
        val fila = user.firstName + " - " + user.lastName + "\n"
        rpta += fila
    }
    return rpta
}

suspend fun AgregarUsuario(user: User, dao: UserDao) {
    try {
        dao.insert(user)
    } catch (e: Exception) {
        Log.e("User", "Error: insert: ${e.message}")
    }
}
