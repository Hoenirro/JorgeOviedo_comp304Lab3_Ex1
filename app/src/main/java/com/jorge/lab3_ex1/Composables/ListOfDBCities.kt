package com.jorge.lab3_ex1.Composables

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jorge.lab3_ex1.ViewModel.CitiesViewModel

@Composable
fun ListOfDBCities(
    citiesViewModel: CitiesViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val list = citiesViewModel.dbcities
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        LazyColumn(
            modifier = modifier
        ) {
            items(list.size) { id ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable{
                            navController.navigate("weatherScreen/${list[id].cityName}")
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = list[id].cityName)
                    IconButton(onClick = {
                        citiesViewModel.deleteOneCity(list[id])
                        Toast.makeText(
                            context,
                            "${list[id].cityName} removed from favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }

        }
        // Floating Action Button to navigate to the initial screen
        FloatingActionButton(
            onClick = { navController.navigate("initialScreen") },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            shape = CircleShape,
        ) {
            Icon(Icons.Default.Search, contentDescription = "Go to Search")
        }

        // Floating Action Button to navigate back
        FloatingActionButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = CircleShape,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back")
        }
    }
}