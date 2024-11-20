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
import com.jorge.lab3_ex1.Composables.ListOfCities
import com.jorge.lab3_ex1.Composables.ListOfDBCities
import com.jorge.lab3_ex1.Composables.WeatherViewScreen
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

