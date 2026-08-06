package com.badara.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.badara.mobile.viewmodel.MainViewModel

@Composable
fun MainScreen(vm: MainViewModel = viewModel()) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin1234") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Badara Android", style = MaterialTheme.typography.headlineSmall)

        if (vm.token == null) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") }
            )
            Button(onClick = { vm.login(username, password) }) {
                Text("로그인")
            }
        } else {
            Text("Sync Status: ${vm.syncStatus}")
            Button(onClick = { vm.refreshFiles() }) {
                Text("새로고침")
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.files) { file ->
                    Text("${file.filename} (${file.syncStatus})")
                }
            }
        }

        vm.errorMessage?.let {
            Text("오류: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}
