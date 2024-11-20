package com.jorge.lab3_ex1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.jorge.lab3_ex1.RoomDB.City
import com.jorge.lab3_ex1.RoomDB.CityDataBase
import com.jorge.lab3_ex1.ViewModel.AppRepository
import com.jorge.lab3_ex1.ViewModel.CitiesViewModel
import com.jorge.lab3_ex1.ViewModel.ViewModelFactory
import com.jorge.lab3_ex1.ViewModel.WeatherViewModel
import com.jorge.lab3_ex1.ViewModel.WeatherViewModelFactory
import com.jorge.lab3_ex1.ui.theme.JorgeOviedo_comp304Lab3_Ex1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and repository
        val database = CityDataBase.getInstance(applicationContext)
        val repository = AppRepository(database.getCityDao())

        // Initialize ViewModel factories
        val viewModelFactory = ViewModelFactory(repository)
        val weatherViewModelFactory = WeatherViewModelFactory(repository)

        // Obtain ViewModels using their respective factories
        val citiesViewModel = ViewModelProvider(this, viewModelFactory)[CitiesViewModel::class.java]
        val weatherViewModel = ViewModelProvider(this, weatherViewModelFactory)[WeatherViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            JorgeOviedo_comp304Lab3_Ex1Theme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = { TopBar(navController) },
                ) { innerPadding ->
                    NavHostScreen(navController, citiesViewModel, weatherViewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TopBar(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "WeatherApp - by Jorge Oviedo", style = MaterialTheme.typography.bodySmall)
            Row {
                IconButton(onClick = { navController.navigate("initialScreen") })
                { Icon(Icons.Default.Search, contentDescription = "Go to Search") }
            }
                IconButton(onClick = { navController.navigate("favoritesScreen") })
                    { Icon(Icons.Default.Star, contentDescription = "Favorites") }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
    }
}

@Composable
fun NavHostScreen(
    navController: NavHostController,
    citiesViewModel: CitiesViewModel,
    weatherViewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "listOfCities") {
        composable("listOfCities") {
            ListOfCities(citiesViewModel, navController, modifier)
        }
        composable("weatherScreen/{city}") { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city")
            if (city != null) {
                WeatherViewScreen(navController, city, weatherViewModel, citiesViewModel)
            }
        }
        composable("favoritesScreen") {
            ListOfDBCities(citiesViewModel, navController, modifier)
        }
        composable("initialScreen") {
            ListOfCities(citiesViewModel, navController, modifier)
        }
    }
}

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