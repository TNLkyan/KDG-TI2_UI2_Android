package com.example.groeiproject_ui2

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.groeiproject_ui2.ui.PersonViewModel
import com.example.groeiproject_ui2.ui.screens.PersonAddForum
import com.example.groeiproject_ui2.ui.screens.PersonCardLayout
import com.example.groeiproject_ui2.ui.screens.VerticalPeopleList
import com.example.groeiproject_ui2.ui.screens.components.FinancialTrackerTopAppBar
import com.example.groeiproject_ui2.ui.theme.FinancialTrackerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current.applicationContext as Application
            val personViewModel: PersonViewModel = viewModel(
                factory = PersonViewModel.provideFactory(context)
            )

            val isDarkModeEnabled by personViewModel.isDarkMode.collectAsStateWithLifecycle()

            FinancialTrackerTheme(darkTheme = isDarkModeEnabled) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FinancialTrackerApp(personViewModel = personViewModel)
                }
            }
        }
    }
}

@Composable
fun FinancialTrackerApp(
    navController: NavHostController = rememberNavController(),
    personViewModel: PersonViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        topBar = {
            FinancialTrackerTopAppBar(
                navBackStackEntry = navBackStackEntry,
                navController = navController,
                personViewModel = personViewModel
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "vertical_list_people",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = "vertical_list_people") {
                VerticalPeopleList(
                    onItemClick = {
                        personViewModel.selectPerson(it)
                        navController.navigate("person_player_card")
                    },
                    personViewModel = personViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(route = "person_player_card") {
                PersonCardLayout(
                    personViewModel = personViewModel,
                    onAddForumClick = {
                        navController.navigate("person_add_forum")
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(route = "person_add_forum") {
                PersonAddForum(
                    personViewModel = personViewModel,
                    onCloseClick = { navController.navigateUp() },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    FinancialTrackerApp(personViewModel = viewModel())
}