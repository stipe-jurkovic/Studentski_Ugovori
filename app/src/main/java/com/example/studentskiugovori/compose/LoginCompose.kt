package com.example.studentskiugovori.compose

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.AdaptiveIconDrawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.studentskiugovori.R
import com.example.studentskiugovori.model.Repository
import com.example.studentskiugovori.ui.login.LoginViewModel
import com.example.studentskiugovori.utils.NetworkService
import android.graphics.Canvas
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip


@Preview
@Composable
fun LoginPreview() {
    AppTheme {
        LoginCompose(
            LoginViewModel(Repository(NetworkService())),
            LocalContext.current.getSharedPreferences("sharedPrefs", 0),
            SnackbarHostState()
        )
    }
}


@Composable
fun LoginCompose(
    loginViewModel: LoginViewModel,
    sheredPrefs: SharedPreferences,
    snackbarHostS: SnackbarHostState
) {

    val snackbarHostState = remember { snackbarHostS }
    var textEmail by remember { mutableStateOf("") }
    var textPass by remember { mutableStateOf("") }

    var passwordVisibility by remember { mutableStateOf(false) }
    val icon = painterResource(
        id = if (passwordVisibility)
            R.drawable.invisible
        else
            R.drawable.view
    )

    Scaffold(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Sandbox()
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Studentski Ugovori", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                OutlinedTextField(
                    value = textEmail,
                    onValueChange = { textEmail = it },
                    shape = RoundedCornerShape(10.dp),
                    label = { Text(text = "Email") },
                    placeholder = { Text("Unesi email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textPass,
                    onValueChange = { textPass = it },
                    shape = RoundedCornerShape(10.dp),
                    label = { Text(text = "Lozinka") },
                    placeholder = { Text("Unesi lozinku") },
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisibility = !passwordVisibility
                        }) {
                            Icon(
                                painter = icon,
                                contentDescription = "Visibility Icon",
                                modifier = Modifier.padding(7.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisibility) VisualTransformation.None
                    else PasswordVisualTransformation()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (loginViewModel.showLoading.observeAsState().value == true) {
                CircularProgressIndicator(
                    modifier = Modifier.width(40.dp),
                    color = colorResource(id = R.color.md_theme_secondary),
                    trackColor = colorResource(id = R.color.md_theme_onSecondary),
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round
                )
            } else {
                OutlinedButton(
                    onClick = {
                        loginViewModel.attemptLogin(textEmail, textPass, sheredPrefs)
                    },
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.md_theme_secondary))
                ) {
                    Text(text = "Prijava")
                }
            }
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun Sandbox() {
    ResourcesCompat.getDrawable(
        LocalContext.current.resources,
        R.mipmap.ic_launcher, LocalContext.current.theme
    )?.let { drawable ->
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                "An image",
                modifier = Modifier.requiredSize(96.dp)
            )
        }
    }
}