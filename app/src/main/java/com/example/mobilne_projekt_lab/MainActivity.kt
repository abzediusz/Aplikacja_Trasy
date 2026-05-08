package com.example.mobilne_projekt_lab

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.VerticalDragHandleDefaults.shapes

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat.animate

import androidx.navigation.NavController
import androidx.navigation.compose.*
import coil.compose.AsyncImage

import com.example.mobilne_projekt_lab.ui.theme.Mobilne_projekt_labTheme

import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    private val viewModel: UserViewModel by viewModels()
    private var splashDone = false
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        //val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        //splashScreen.setKeepOnScreenCondition { !splashDone }
        enableEdgeToEdge()
        setContent {
            //var showSplash by remember { mutableStateOf(true) }
            val splashShown by viewModel.splashShown.collectAsState()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            Mobilne_projekt_labTheme(darkTheme = isDarkTheme) {
            if (!splashShown) {
                SplashScreen(onFinished = { viewModel.setSplashShown() })
            }
            else {


                    MyApp(viewModel)
                }
            }
        }

    }





}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: UserViewModel
) {

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    var searchText by remember { mutableStateOf("") }

    val trasy by viewModel.trasy.observeAsState(emptyList())

    val filtered = trasy.filter {
        val opis = viewModel.getDescription(it).value ?: ""
        it.contains(searchText, true) ||
                opis.contains(searchText, true)
    }

    val kategorie = listOf(
        Triple("Biegowa", "Trasy biegowe", R.drawable.kategoria_biegowa),
        Triple("Rowerowa", "Trasy rowerowe", R.drawable.kategoria_rowerowa),
        Triple("", "Wszystkie trasy", R.drawable.kategoria_wszystko)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {
            ModalDrawerSheet {

                Text(
                    "Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                NavigationDrawerItem(
                    label = { Text("Biegowe") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("category/Biegowa")
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Rowerowe") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("category/Rowerowa")
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Wszystkie") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("category/")
                    }
                )
            }
        }
    ) {

        Scaffold(

            topBar = {
                val isDarkTheme by viewModel.isDarkTheme.collectAsState()

                CenterAlignedTopAppBar(

                    title = {
                        Text(
                            text = "Moja aplikacja",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    navigationIcon = {

                        FilledIconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },

                    actions = {

                        /*IconButton(
                            onClick = { }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Szukaj"
                            )
                        }*/

                        IconButton(
                            onClick = {
                                viewModel.toggleTheme()
                            }
                        ) {
                            Icon(
                                imageVector =
                                    if (isDarkTheme)
                                        Icons.Default.LightMode
                                    else
                                        Icons.Default.DarkMode,

                                contentDescription = "Motyw"
                            )
                        }
                    },

                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Szukaj tras") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null)
                        }
                    )
                }

                if (searchText.isBlank()) {

                    item {
                        Text(
                            "Wybierz kategorię",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    items(kategorie) { (odn,nazwa, obrazek) ->
                        CategoryCard(
                            label = nazwa,
                            imageRes = obrazek,
                            onClick = {
                                navController.navigate("category/$odn")
                            }
                        )
                    }
                } else {

                    item {
                        Text(
                            "Wyniki wyszukiwania",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(filtered) { trasa ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clickable {
                                    navController.navigate("details/$trasa")
                                },
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {

                            Box(modifier = Modifier.fillMaxSize()) {
                                //val imageRes = viewModel.routeImages[trasa]
                                val imageRes = remember(trasa) { viewModel.getRouteImage(trasa) }
                                if (imageRes != null) {
                                    Image(
                                        painter = painterResource(id = imageRes),
                                        contentDescription = trasa,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.secondary)
                                    )
                                }


                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color(0xAA000000)
                                                ),
                                                startY = 100f
                                            )
                                        )
                                )


                                Text(
                                    text = trasa,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp)
                                )
                            }
                        }
                    }


                }
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTras(navController: NavController, viewModel: UserViewModel, typ: String) {
    val trasy by viewModel.trasy.observeAsState(emptyList())
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 1000

    var selectedRouteInTablet by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(typ) {
        viewModel.setType(typ)
    }

    val tytul = when (typ) {
        "Biegowa" -> "Trasy biegowe"
        "Rowerowa" -> "Trasy rowerowe"
        else -> "Wszystkie trasy"
    }
    var searchText by remember { mutableStateOf("") }
    if (isTablet) {

        Row(modifier = Modifier.fillMaxSize()) {

            Box(modifier = Modifier.weight(0.4f)) {
                ContentListaTras(
                    trasy = trasy,
                    searchText = searchText,
                    onSearchChange = { searchText = it },
                    onRouteClick = { selectedRouteInTablet = it },
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel,
                    navController = navController
                )
            }


            Box(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
                if (selectedRouteInTablet != null) {
                    DetailsScreen(
                        name = selectedRouteInTablet!!,
                        viewModel = viewModel,
                        navController = navController,
                        isScreenSplit = true
                    )
                } else {

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Wybierz trasę z listy po lewej", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    } else {

        ContentListaTras(
            trasy = trasy,
            searchText = searchText,
            onSearchChange = { searchText = it },
            onRouteClick = { navController.navigate("details/$it") },
            onBack = { navController.popBackStack() },
            viewModel = viewModel,
            navController = navController
        )
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentListaTras(
    trasy: List<String>,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onRouteClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: UserViewModel,
    navController: NavController
) {
    //var searchText2=searchText
    var searchText2 by remember { mutableStateOf(searchText) }
    val filtered = trasy.filter { it.contains(searchText2, true) }
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AddRouteDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, desc, cat, uri ->
                viewModel.addRoute(name, desc, cat,uri)
                showDialog = false
            }
        )
    }
    Scaffold(
        topBar = { val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            var searchMode by remember { mutableStateOf(false) }


            if (searchMode) {

                TopAppBar(

                    title = {
                        OutlinedTextField(
                            value = searchText2,
                            onValueChange = {
                                searchText2 = it
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Szukaj tras...")
                            }
                        )
                    },

                    navigationIcon = {
                        IconButton(
                            onClick = {
                                searchMode = false
                            }
                        ) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                )

            } else {

                CenterAlignedTopAppBar(

                    title = {
                        Text(
                            "Lista tras",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    navigationIcon = {

                        FilledIconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                null
                            )
                        }
                    },

                    actions = {
                        IconButton(onClick = { showDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Dodaj trasę")
                        }
                        IconButton(
                            onClick = {
                                searchMode = true
                            }
                        ) {
                            Icon(Icons.Default.Search, null)
                        }

                        IconButton(
                            onClick = {
                                viewModel.toggleTheme()
                            }
                        ) {
                            Icon(
                                if (isDarkTheme)
                                    Icons.Default.LightMode
                                else
                                    Icons.Default.DarkMode,
                                null
                            )
                        }
                    }
                )
            } }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered) { trasa ->
                val routeData by viewModel.getRouteData(trasa).observeAsState()
                //val imageRes = remember(trasa) { viewModel.getRouteImage(trasa.nazwa) }
                Card(
                    modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable { onRouteClick(trasa) },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {Box(modifier = Modifier.fillMaxSize()) {
                        /*if (imageRes != null) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = trasa,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }*/RouteImage(
                        routeName = trasa,
                        userPath = routeData,
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp,
                                bottomStart = 16.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp))
                    )
                        /*else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.secondary)
                            )
                        }*/
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0xAA000000)),
                                        startY = 100f
                                    )
                                )
                        )
                        Text(
                            text = trasa,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        )
                    }
                }}
                }
            }
        }


}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(name: String, viewModel: UserViewModel,navController: NavController, isScreenSplit: Boolean) {
    //val viewModel: UserViewModel = viewModel()
    val opis by viewModel.getDescription(name).observeAsState("")
    val time by viewModel.time.collectAsState()
    val czasy by viewModel.getTimes(name).observeAsState(emptyList())
    val isRunning by viewModel.isRunning.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val minutes = (time / 60) % 60
    val seconds = time % 60
    val hours = time / 3600
    val timeFormatted = "%02d:%02d:%02d".format(hours,minutes, seconds)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val routeData by viewModel.getRouteData(name).observeAsState()
    fun createImageUri(): Uri {
        val directory = File(context.externalCacheDir, "images")
        directory.mkdirs()
        val file = File.createTempFile("photo_", ".jpg", directory)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }


    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempImageUri != null) {

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, tempImageUri)
                putExtra("sms_body", "Spójrz na moje zdjęcie z trasy $name!")

                putExtra("address", "")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Wyślij zdjęcie przez..."))
        }
    }


    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val uri = createImageUri()
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }
    //val imageRes = viewModel.routeImages[name]
    val imageRes = remember(name) { viewModel.getRouteImage(name) }
    //val opis by viewModel.getDescription(name).observeAsState("")
    Scaffold(topBar = {
        val isDarkTheme by viewModel.isDarkTheme.collectAsState()

        CenterAlignedTopAppBar(

            title = {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            },

            navigationIcon = {
                if(!isScreenSplit)
                {FilledIconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Wstecz"
                    )
                }}
            },

            actions = {
                IconButton(onClick = {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Zrób zdjęcie")
                }
                IconButton(

                    onClick = {
                        viewModel.toggleTheme()
                    }
                ) {
                    if(!isScreenSplit)
                    {Icon(
                        imageVector =
                            if (isDarkTheme)
                                Icons.Default.LightMode
                            else
                                Icons.Default.DarkMode,

                        contentDescription = "Motyw"
                    )
                }}
            },

            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.primary
            )
        )


    },
        floatingActionButton = {
            val context = LocalContext.current
            LargeFloatingActionButton(
                onClick = {
                    val tekst = "Mój czas na trasie $name: $timeFormatted"
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:")
                        putExtra("sms_body", tekst)
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Udostępnij wynik"
                )
            }
        },
    )

    { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)

        )
        {
            item {
                Box(modifier = Modifier.height(280.dp).fillMaxWidth()) {
                    /*if (imageRes != null) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }*/
                    RouteImage(
                        routeName = name,
                        userPath = routeData,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth().height(280.dp)
                    )
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))
                    ))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
                    )
                }
            }


            item {
                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("TWÓJ CZAS", style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Black,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            OutlinedIconButton(
                                onClick = { viewModel.resetTimer() },
                                modifier = Modifier.size(56.dp),
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Resetuj",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            FilledIconButton(
                                onClick = { if (isRunning) viewModel.pauseTimer() else viewModel.startTimer() },
                                modifier = Modifier.size(64.dp)
                            ) {
                                Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                            }

                            FilledTonalIconButton(
                                onClick = { viewModel.saveTime(name) },
                                modifier = Modifier.size(64.dp)
                            ) {
                                Icon(Icons.Default.Save, null)
                            }
                        }
                    }
                }
            }


            item {
                Text(
                    text = opis,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
            }


            item {
                Text("Historia wyników", modifier = Modifier.padding(24.dp), style = MaterialTheme.typography.titleMedium)
            }
            items(czasy) { wpis ->
                Card(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        //Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("%02d:%02d:%02d".format(wpis.czas/3600, (wpis.czas/60)%60, wpis.czas%60), fontWeight = FontWeight.Bold)
                            Text(wpis.data, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }





}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp(viewModel: UserViewModel) {

    //viewModel.insertIfEmpty()
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    //val isTablet = screenWidthDp >= 1000
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()


        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                SwipeableScreen(navController = navController) {
                    HomeScreen(navController, viewModel)
                }
            }
            composable("category/{typ}") { backStackEntry ->
                val typ = backStackEntry.arguments?.getString("typ") ?: ""
                SwipeableScreen(navController = navController) {
                    ListaTras(navController, viewModel, typ)
                }
            }
            composable("details/{name}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                SwipeableScreen(navController = navController) {
                    DetailsScreen(name, viewModel, navController,false)
                }

        }

        }


}
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()


    val startX = -(screenWidth / 2 + 60f)
    val endX = screenWidth / 2 + 60f

    val offsetX = remember { Animatable(startX) }

    LaunchedEffect(Unit) {

        offsetX.animateTo(
            targetValue = endX,
            animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3700B3)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_playstore2),
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
                .offset(x = offsetX.value.dp)
        )
    }
}
@Composable
fun ThemeSwitch(viewModel: UserViewModel) {

    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = if (isDarkTheme) "Ciemny" else "Jasny",
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(6.dp))

        Switch(
            checked = isDarkTheme,
            onCheckedChange = {
                viewModel.toggleTheme()
            }
        )
    }
}
@Composable
fun SwipeableScreen(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = offsetX.value.dp)
            .draggable(
                state = rememberDraggableState { delta ->
                    scope.launch {
                        offsetX.snapTo(offsetX.value + delta)
                    }
                },
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    scope.launch {
                        if (offsetX.value > 100f) {

                            navController.popBackStack()
                            offsetX.animateTo(
                                targetValue = 500f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )

                        } else {

                            offsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    }
                }
            )
    ) {
        content()
    }
}
@Composable
fun CategoryCard(label: String, imageRes: Int, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().height(160.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent))
            ))
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.CenterStart).padding(24.dp)
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRouteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Uri?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Biegowa") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj nową trasę", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nazwa trasy") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Opis") })


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Kategoria: ")
                    FilterChip(
                        selected = category == "Biegowa",
                        onClick = { category = "Biegowa" },
                        label = { Text("Biegowa") }
                    )
                    Spacer(Modifier.width(8.dp))
                    FilterChip(
                        selected = category == "Rowerowa",
                        onClick = { category = "Rowerowa" },
                        label = { Text("Rowerowa") }
                    )
                }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (imageUri == null) "Wybierz zdjęcie z galerii" else "Zdjęcie wybrane!")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, description, category, imageUri) }) {
                Text("Dodaj")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )
}
@Composable
fun RouteImage(
    routeName: String,
    userPath: String?,
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resId = remember(routeName) { viewModel.getRouteImage(routeName) }

    if (userPath != null) {

        AsyncImage(
            model = userPath,
            contentDescription = routeName,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else if (resId != null) {

        Image(
            painter = painterResource(id = resId),
            contentDescription = routeName,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {

        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Landscape, null, modifier = Modifier.size(48.dp))
        }
    }
}


/*@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
}*/