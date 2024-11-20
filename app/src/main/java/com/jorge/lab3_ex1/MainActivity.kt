package com.jorge.lab3_ex1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

        /*val database = CityDataBase.getInstance(applicationContext)
        val repository = AppRepository(database.getCityDao())
        val myviewModelFactory = ViewModelFactory(repository)
        val myViewModel = ViewModelProvider(this,myviewModelFactory)[CitiesViewModel::class.java]*/

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
                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }*/
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
            Text(text = "WeatherApp", style = MaterialTheme.typography.bodySmall)
            IconButton(onClick = { navController.navigate("favoritesScreen") }) {
                Icon(Icons.Default.Star, contentDescription = "Favorites")
            }
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
                citiesViewModel.getCities(it)
            },
            label = { Text("Search City") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        val list = citiesViewModel.cities
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




@Composable
fun WeatherViewScreen(
    navController: NavHostController,
    city: String,
    weatherViewModel: WeatherViewModel,
    citiesViewModel: CitiesViewModel
) {
    weatherViewModel.getWeather(city)
    val weatherObject = weatherViewModel.weatherO

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        weatherObject?.let { wo ->
            Text(text = wo.name, fontSize = 40.sp)
            Spacer(Modifier.fillMaxHeight(0.2f))
            wo.weather?.get(0)?.let {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${it.icon}@2x.png",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.4f)
                )
            }
            Spacer(Modifier.fillMaxHeight(0.2f))
            wo.weather?.get(0)?.let {
                Text(fontSize = 30.sp, text = it.main)
            }
            Spacer(Modifier.fillMaxHeight(0.1f))
            Text(fontSize = 30.sp, text = wo.main?.temp.toString())
            Text(fontSize = 30.sp, text = "Feels Like " + wo.main?.feels_like.toString())
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                citiesViewModel.insertToDB(City(cityName = city))
            }) {
                Text("Save to Favorites")
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.navigateUp() }) {
            Text("Back")
        }
    }
}

@Composable
fun ListOfDBCities(
    citiesViewModel: CitiesViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val list = citiesViewModel.dbcities
    LazyColumn(
        modifier = modifier
    ) {
        items(list.size) { id ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = list[id].cityName)
                IconButton(onClick = {
                    citiesViewModel.deleteOneCity(list[id])
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}