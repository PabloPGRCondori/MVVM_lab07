package com.example.datossinmvvm_lab07

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = crearDatabase(context)
    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                actions = {
                    // Botón para agregar usuario
                    IconButton(onClick = {
                        val user = User(0, firstName, lastName)
                        coroutineScope.launch {
                            AgregarUsuario(user = user, dao = dao)
                            // Resetear campos
                            firstName = ""
                            lastName = ""
                            // Listar usuarios después de agregar
                            dataUser = getUsers(dao = dao)
                        }
                    }) {
                        // Ícono para agregar usuario
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Agregar Usuario")
                    }
                    // Botón para listar usuarios
                    IconButton(onClick = {
                        coroutineScope.launch {
                            dataUser = getUsers(dao = dao) // Actualiza la lista de usuarios
                        }
                    }) {
                        // Ícono para listar usuarios
                        Icon(imageVector = Icons.Default.List, contentDescription = "Listar Usuarios")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(50.dp))
            TextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID (solo lectura)") },
                readOnly = true,
                singleLine = true
            )
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name: ") },
                singleLine = true
            )
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name:") },
                singleLine = true
            )

            // Eliminar último usuario
            Button(
                onClick = {
                    coroutineScope.launch {
                        val lastUser = dao.getLastUser()
                        if (lastUser != null) {
                            dao.delete(lastUser)
                            dataUser = getUsers(dao) // Actualizar lista después de eliminar
                        } else {
                            dataUser = "No hay usuarios para eliminar"
                        }
                    }
                }
            ) {
                Text("Eliminar Último Usuario", fontSize = 16.sp)
            }

            // Mostrar los usuarios listados
            Text(text = dataUser, fontSize = 16.sp)
        }
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

        // Función para obtener todos los usuarios y convertirlos a un String
        suspend fun getUsers(dao: UserDao): String {
            return dao.getAll().joinToString(separator = "\n") { user ->
                "${user.firstName} - ${user.lastName}"
            }
        }

        // Función para agregar un usuario
        suspend fun AgregarUsuario(user: User, dao: UserDao) {
            try {
                dao.insert(user)
            } catch (e: Exception) {
                Log.e("User", "Error: insert: ${e.message}")
            }
        }
@Preview(showBackground = true)
@Composable
fun PreviewScreenUser() {
    ScreenUser()
}

