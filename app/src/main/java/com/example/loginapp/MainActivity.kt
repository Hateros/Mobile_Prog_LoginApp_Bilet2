package com.example.loginapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.loginapp.ui.theme.LoginAppTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginAppTheme {
                LoginForm()
            }
        }
    }
}


@Composable
fun LoginForm() {
    val context = LocalContext.current
    var login by remember { mutableStateOf(loadLogin(context)) }
    var password by remember { mutableStateOf("") }
    var loginHasErrors by remember { mutableStateOf(false) }
    var passwordHasErrors by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Форма входа")
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин") },
            isError = loginHasErrors
        )
        if (loginHasErrors) {
            Text(
                "Логин должен быть от 3-х символов",
            )
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            isError = passwordHasErrors
        )
        if (passwordHasErrors) {
            Text(
                "Пароль должен быть от 3-х символов",
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        )
        {
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )
            Text("Сохранить логин?")
        }
        Button(
            onClick = {
                // Валидация данных - латиница и цифры, длина от 3 символов
                val usernameRegex = "^[a-zA-Z0-9]{3,}$".toRegex()
                val passwordRegex = "^[a-zA-Z0-9]{3,}$".toRegex()

                loginHasErrors = !login.matches(usernameRegex)
                passwordHasErrors = !password.matches(passwordRegex)

                if (!loginHasErrors && !passwordHasErrors) {
                    // Сохраняем логин, если чекбокс активен
                    if (checked) saveLogin(context, login)
                    // Сохраняем пустую строку, если чекбокс неактивен
                    // Чтобы при следующем запуске не заполнялся предыдущий запомненный логин
                    else saveLogin(context, "")
                    showDialog = true
                }
            }
        ) {
            Text("Войти")
        }

        if(showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Вы успешно вошли") },
                text = { Text(if (checked) "С сохранением логина" else "Без сохранения логина") },
                confirmButton = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            showDialog = false
                        }
                        ) {
                            Text("OK")
                        }
                    }
                }
            )
        }


    }

}


// Получаем логин из SharedPreferences
private fun loadLogin(context: Context): String {
    val prefs = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
    // Если логин не сохранен, возвращаем пустую строку
    return prefs.getString("LOGIN", "").toString()
}

// Сохраняем логин в SharedPreferences
// jetpack compose упорно заставляет меня использовать данную аннотацию
@SuppressLint("UseKtx")
private fun saveLogin(context: Context, login: String) {
    val prefs = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
    prefs.edit()
        .putString("LOGIN", login)
        .apply()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LoginAppTheme {
        LoginForm()
    }
}
