package com.example.groeiproject_ui2.ui.screens.components

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groeiproject_ui2.R
import com.example.groeiproject_ui2.ui.PersonViewModel


fun setApplicationLocale(context: Context, localeCode: String) {
    val locale = java.util.Locale(localeCode)
    java.util.Locale.setDefault(locale)
    val config = Configuration()
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val applicationContext = context.applicationContext
        applicationContext.createConfigurationContext(config)
    }

    val activity = context as? Activity
    activity?.recreate()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialTrackerTopAppBar(
    navBackStackEntry: NavBackStackEntry?,
    navController: NavHostController,
    personViewModel: PersonViewModel,
    modifier: Modifier = Modifier
) {
    val currentRoute = navBackStackEntry?.destination?.route

    val titleResId = when (currentRoute) {
        "vertical_list_people" -> R.string.app_name
        "person_player_card" -> R.string.details_page
        "person_add_forum" -> R.string.add_page
        else -> R.string.app_name
    }

    val canNavigateBack = navController.previousBackStackEntry != null

    var showMenu by remember { mutableStateOf(false) }

    val isDarkMode by personViewModel.isDarkMode.collectAsStateWithLifecycle()
    val currentLanguageCode by personViewModel.currentLanguageCode.collectAsStateWithLifecycle()

    val context = LocalContext.current

    TopAppBar(
        title = { Text(stringResource(titleResId)) },
        navigationIcon = {
            AnimatedVisibility(
                visible = canNavigateBack,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button_description)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.language)) },
                    onClick = {
                        val newLanguage = if (currentLanguageCode == "nl") "en" else "nl"
                        personViewModel.setLanguage(newLanguage)
                        showMenu = false
                        setApplicationLocale(context, newLanguage)
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(stringResource(
                            if (isDarkMode) R.string.light_mode else R.string.dark_mode
                        ))
                    },
                    onClick = {
                        personViewModel.toggleDarkMode()
                        showMenu = false
                    }
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier
    )
}