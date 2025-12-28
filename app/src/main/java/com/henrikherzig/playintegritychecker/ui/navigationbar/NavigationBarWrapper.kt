package com.henrikherzig.playintegritychecker.ui.navigationbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.henrikherzig.playintegritychecker.R
import com.henrikherzig.playintegritychecker.attestation.PlayIntegrityStatement
import com.henrikherzig.playintegritychecker.attestation.safetynet.SafetyNetStatement
import com.henrikherzig.playintegritychecker.dataStore
import com.henrikherzig.playintegritychecker.ui.CustomCardTitle
import com.henrikherzig.playintegritychecker.ui.about.AboutPage
import com.henrikherzig.playintegritychecker.ui.playintegrity.PlayIntegrity
import com.henrikherzig.playintegritychecker.ui.safetynet.SafetyNet
import com.henrikherzig.playintegritychecker.ui.ResponseType
import com.henrikherzig.playintegritychecker.ui.settings.Settings
import com.henrikherzig.playintegritychecker.ui.CustomViewModel
import com.henrikherzig.playintegritychecker.ui.dashboard.Dashboard
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(
  safetyNetResult: State<ResponseType<SafetyNetStatement>>,
  onSafetyNetRequest: (String, String, String?) -> Unit,
  playIntegrityResult: State<ResponseType<PlayIntegrityStatement>>,
  onPlayIntegrityRequest: (String, String, String?) -> Unit,
  playServiceVersion: String?
) {
  val navController = rememberNavController()
  val appPages = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.PlayIntegrity,
    BottomNavItem.SafetyNet,
    BottomNavItem.Settings,
  )
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  // State for check and nonce settings
  var selectedIndexCheckPlayIntegrity by remember { mutableStateOf("local") }
  var selectedIndexCheckSafetyNet by remember { mutableStateOf("local") }

  val local: String = stringResource(id = R.string.requestSettings_local)
  val server: String = stringResource(id = R.string.requestSettings_server)
  val google: String = stringResource(id = R.string.requestSettings_google)
  val itemsCheck: List<List<String>> = listOf(listOf("local", local), listOf("server", server))

  val changedCheckPlayIntegrity: (idx: String) -> Unit = {
    selectedIndexCheckPlayIntegrity = it
  }
  val changedCheckSafetyNet: (idx: String) -> Unit = {
    selectedIndexCheckSafetyNet = it
  }

  var selectedIndexNoncePlayIntegrity by remember { mutableStateOf("local") }
  var selectedIndexNonceSafetyNet by remember { mutableStateOf("local") }

  val itemsNonce: List<List<String>> =
    listOf(listOf("local", local), listOf("server", server), listOf("google", google))
  val changedNoncePlayIntegrity: (idx: String) -> Unit = {
    selectedIndexNoncePlayIntegrity = it
  }
  val changedNonceSafetyNet: (idx: String) -> Unit = {
    selectedIndexNonceSafetyNet = it
  }

  // ViewModel for URL
  val context = LocalContext.current
  val viewModel = remember { CustomViewModel(context.dataStore) }
  LaunchedEffect(viewModel) { viewModel.requestURL() }
  val urlValue = viewModel.stateURL.observeAsState().value

  // Scroll behavior for collapsing top bar
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            Text(
              text = stringResource(id = R.string.app_name_short),
              style = MaterialTheme.typography.titleMedium
            )
            Text(
              text = stringResource(id = R.string.app_name),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          if (currentRoute == "licence") {
            IconButton(onClick = { navController.navigateUp() }) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
              )
            }
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = scrollBehavior
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
      ) {
        val currentDestination = navBackStackEntry?.destination
        appPages.forEach { screen ->
          NavigationBarItem(
            icon = {
              Icon(
                imageVector = screen.icon,
                contentDescription = stringResource(screen.title)
              )
            },
            label = {
              Text(
                text = stringResource(screen.title),
                style = MaterialTheme.typography.labelMedium
              )
            },
            selected = currentDestination?.hierarchy?.any { it.route == screen.screen_route } == true,
            onClick = {
              navController.navigate(screen.screen_route) {
                popUpTo(navController.graph.findStartDestination().id) {
                  saveState = true
                }
                launchSingleTop = true
              }
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
              selectedTextColor = MaterialTheme.colorScheme.onSurface,
              indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = BottomNavItem.Dashboard.screen_route,
      modifier = Modifier.padding(innerPadding)
    ) {
      composable(BottomNavItem.Dashboard.screen_route) {
        Dashboard(
          onCheckNow = {
            navController.navigate(BottomNavItem.PlayIntegrity.screen_route) {
              popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
              }
              launchSingleTop = true
            }
          }
        )
      }
      composable(BottomNavItem.PlayIntegrity.screen_route) {
        PlayIntegrity(
          playIntegrityResult,
          {
            onPlayIntegrityRequest(
              selectedIndexCheckPlayIntegrity,
              selectedIndexNoncePlayIntegrity,
              urlValue
            )
          },
          selectedIndexCheckPlayIntegrity,
          itemsCheck,
          changedCheckPlayIntegrity,
          selectedIndexNoncePlayIntegrity,
          itemsNonce,
          changedNoncePlayIntegrity
        )
      }
      composable(BottomNavItem.SafetyNet.screen_route) {
        SafetyNet(
          safetyNetResult,
          {
            onSafetyNetRequest(
              selectedIndexCheckSafetyNet,
              selectedIndexNonceSafetyNet,
              urlValue
            )
          },
          selectedIndexCheckSafetyNet,
          itemsCheck,
          changedCheckSafetyNet,
          selectedIndexNonceSafetyNet,
          itemsNonce.subList(0, 2),
          changedNonceSafetyNet
        )
      }
      composable(BottomNavItem.Settings.screen_route) {
        Settings(playServiceVersion)
      }
      composable(BottomNavItem.About.screen_route) {
        AboutPage(navController)
      }
      composable("licence") {
        Column(
          modifier = Modifier.padding(all = 12.dp)
        ) {
          CustomCardTitle(stringResource(id = R.string.about_licenseButton))
          LibrariesContainer()
        }
      }
    }
  }
}
