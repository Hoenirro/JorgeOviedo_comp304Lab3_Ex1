package com.jorge.lab3_ex1.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jorge.lab3_ex1.ViewModel.CitiesViewModel

@Composable
fun ListOfCities(
    citiesViewModel: CitiesViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                if (it.length >= 2) {
                    citiesViewModel.getCities(it)
                }
            },
            label = { Text("Search City") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        if (searchQuery.length >= 2) {
            val list = citiesViewModel.cities

            if (list.isEmpty() || list.contains("%s")) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.Red)
                ) {
                    Text(
                        text = "No cities to show",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn {
                    items(list) { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clickable {
                                    navController.navigate("weatherScreen/$city")
                                },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = city)
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate("favoritesScreen") },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            shape = CircleShape,
            //backgroundColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Favorite, contentDescription = "Go to Favorites")
        }
    }
}