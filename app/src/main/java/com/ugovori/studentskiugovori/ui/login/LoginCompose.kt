package com.ugovori.studentskiugovori.ui.login

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ugovori.studentskiugovori.R
import com.ugovori.studentskiugovori.compose.AppTheme
import com.ugovori.studentskiugovori.model.Repository
import com.ugovori.studentskiugovori.utils.NetworkService


@Preview
@Composable
fun LoginPreview() {
    AppTheme {
        LoginCompose(
            viewModel { LoginViewModel(Repository(NetworkService())) },
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
    val keyboardController = LocalSoftwareKeyboardController.current

    fun onDone() {
        keyboardController?.hide()
        loginViewModel.attemptLogin(textEmail, textPass, sheredPrefs)
    }

    var passwordVisibility by remember { mutableStateOf(false) }
    val icon = painterResource(
        id = if (passwordVisibility)
            R.drawable.visibility_hide
        else
            R.drawable.visibility_show
    )

    Scaffold(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(128.dp))
            Text(
                text = stringResource(R.string.studentski_ugovori_title_login),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.tvoji_podaci_su_sigurni_i_ne_dijele_se_s_tre_im_stranama),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
            OutlinedTextField(
                value = textEmail,
                onValueChange = { textEmail = it },
                shape = RoundedCornerShape(10.dp),
                label = { Text(text = stringResource(R.string.email_label)) },
                placeholder = { Text(stringResource(R.string.enter_email_placeholder)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = textPass,
                onValueChange = { textPass = it },
                shape = RoundedCornerShape(10.dp),
                label = { Text(text = stringResource(R.string.password_label)) },
                placeholder = { Text(stringResource(R.string.enter_password_placeholder)) },
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                visualTransformation = if (passwordVisibility) VisualTransformation.None
                else PasswordVisualTransformation(),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    Modifier
                        .height(40.dp)
                        .width(120.dp)
                ) {
                    if (loginViewModel.showLoading.observeAsState().value == true) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .width(35.dp)
                                .padding(5.dp)
                                .align(Alignment.Center),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.onSecondary,
                            strokeWidth = 4.dp,
                            strokeCap = StrokeCap.Round
                        )
                    } else {
                        OutlinedButton(
                            onClick = {
                                onDone()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = stringResource(R.string.login_button))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}
