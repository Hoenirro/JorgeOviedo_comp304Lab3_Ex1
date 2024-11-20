package com.jorge.lab3_ex1.Composables

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.jorge.lab3_ex1.R
import com.jorge.lab3_ex1.RoomDB.City
import com.jorge.lab3_ex1.ViewModel.CitiesViewModel
import com.jorge.lab3_ex1.ViewModel.WeatherViewModel

@Composable
fun WeatherViewScreen(
    navController: NavHostController,
    city: String,
    weatherViewModel: WeatherViewModel,
    citiesViewModel: CitiesViewModel
) {
    val context = LocalContext.current

    // Fetch weather data
    weatherViewModel.getWeather(city)
    val weatherObject = weatherViewModel.weatherO

    // Check if the city is in favorites
    var isFavorite by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = citiesViewModel.dbcities) {
        isFavorite = citiesViewModel.dbcities.any { it.cityName == city }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(56.dp)) // separation from the topbar

        Text(
            text = city,
            fontSize = 40.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        weatherObject?.let { wo ->
            wo.weather?.get(0)?.let {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${it.icon}@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            wo.weather?.get(0)?.let {
                Text(fontSize = 30.sp, text = it.main)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(fontSize = 30.sp, text = "${wo.main?.temp}°C")
            Text(fontSize = 20.sp, text = "Feels Like ${wo.main?.feels_like}°C")
            Row {
                Text("add to Favorites")

                // Toggleable Star for Favorites
                IconToggleButton(
                    checked = isFavorite,
                    onCheckedChange = {
                        isFavorite = !isFavorite
                        if (isFavorite) {
                            citiesViewModel.insertToDB(City(cityName = city))
                            Toast.makeText(context, "$city added to favorites", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            citiesViewModel.deleteCityByName(city)
                            Toast.makeText(
                                context,
                                "$city removed from favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {

                    Icon(
                        painter = painterResource(if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline),
                        contentDescription = null,
                        tint = if (isFavorite) Color.Yellow else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            FloatingActionButton(
                onClick = { navController.navigate("initialScreen") },
                modifier = Modifier
                    .padding(16.dp),
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Search, contentDescription = "Go to Search")
            }
            FloatingActionButton(
                onClick = { navController.navigate("favoritesScreen") },
                modifier = Modifier
                    .padding(16.dp),
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Go to Favorites")
            }
        }
    }
}