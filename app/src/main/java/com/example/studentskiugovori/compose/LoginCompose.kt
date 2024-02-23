package com.example.studentskiugovori.compose

import android.content.SharedPreferences
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.studentskiugovori.R
import com.example.studentskiugovori.ui.login.LoginViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginCompose(
    loginViewModel: LoginViewModel,
    sheredPrefs: SharedPreferences,
    snackbarHostS: SnackbarHostState
) {

    val snackbarHostState = remember { snackbarHostS }
    var textEmail by remember { mutableStateOf("") }
    var textPass by remember { mutableStateOf("") }

    val modifier = Modifier
        //.clip(RoundedCornerShape(10.dp))
        .background(Color.White)
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
                .background(colorResource(id = R.color.white))
        ) {
            Icon(
                painter = adaptiveIconPainterResource(R.mipmap.ic_launcher),
                contentDescription = "icon",
                modifier = Modifier.background(Color.White),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(32.dp))
            Column {
                OutlinedTextField(
                    value = textEmail,
                    onValueChange = { textEmail = it },
                    shape = RoundedCornerShape(10.dp),
                    label = { Text(text = "Email") },
                    modifier = modifier,
                    placeholder = { Text("Unesi email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                space()
                OutlinedTextField(
                    value = textPass,
                    onValueChange = { textPass = it },
                    shape = RoundedCornerShape(10.dp),
                    label = { Text(text = "Lozinka") },
                    modifier = modifier, placeholder = { Text("Unesi lozinku") },
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
            space()
            if (loginViewModel.showLoading.observeAsState().value == true) {
                CircularProgressIndicator(
                    modifier = Modifier.width(40.dp),
                    color = colorResource(id = R.color.StudomatBlue),
                    trackColor = colorResource(id = R.color.StudomatBlueLite),
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round
                )
            } else {
                OutlinedButton(
                    onClick = {
                        loginViewModel.attemptLogin(textEmail, textPass, sheredPrefs)
                    },
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.StudomatBlue))
                ) {
                    Text(text = "Prijava")
                }
            }
        }
    }
}

@Composable
fun space() {
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun adaptiveIconPainterResource(@DrawableRes id: Int): Painter {
    val res = LocalContext.current.resources
    val theme = LocalContext.current.theme

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Android O supports adaptive icons, try loading this first (even though this is least likely to be the format).
        val adaptiveIcon = ResourcesCompat.getDrawable(res, id, theme) as? AdaptiveIconDrawable
        if (adaptiveIcon != null) {
            BitmapPainter(adaptiveIcon.toBitmap().asImageBitmap())
        } else {
            // We couldn't load the drawable as an Adaptive Icon, just use painterResource
            painterResource(id)
        }
    } else {
        // We're not on Android O or later, just use painterResource
        painterResource(id)
    }
}