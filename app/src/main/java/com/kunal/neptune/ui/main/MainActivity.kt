package com.kunal.neptune.ui.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.fragment.app.FragmentManager
import com.kunal.neptune.R
import com.kunal.neptune.utils.NewPostBottomSheetDialogFragment
import com.kunal.neptune.utils.QuitDialog


@ExperimentalMaterialApi
@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {
    private val isLoading = mutableStateOf(true)
    private val currentPage = mutableStateOf("")
    private val title = mutableStateOf("Home")
    var imageWidth = 0f
    lateinit var fm: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val displayMetrics = resources.displayMetrics
        val dpHeight = displayMetrics.heightPixels / displayMetrics.density
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        imageWidth = (dpWidth * 42)/100
        fm = this.supportFragmentManager

        setContent {
            MainTheme()
        }
    }

    @Composable
    fun MainTheme() {
        val context = LocalContext.current
        val navController = rememberNavController()
        MaterialTheme {
            Surface(Modifier.fillMaxSize()) {
                BackHandler(navController = navController)
                Scaffold(topBar = { Toolbar() },bottomBar = { NeptuneAppBottomNavigation(navController) }) {innerPadding ->
                    // Apply the padding globally to the whole BottomNavScreensController
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainScreenNavigationConfigurations(navController,context)
                    }
                }
                Column {

                }
            }
        }
    }

    @Composable
    fun Toolbar(){
        Surface(color = colorResource(id = R.color.white),modifier = Modifier.fillMaxWidth()){
            Column(modifier = Modifier
                .fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically
                    ,modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)){
                    Row {
                        Text(text = title.value,
                            style = TextStyle(
                                color = colorResource(id = R.color.purple_200),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(start = 20.dp, top = 10.dp,end = 0.dp,bottom = 10.dp))
                    }
                    Card(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(12.dp)
                            .clickable {

                            },
                        shape = CircleShape,
                        elevation = 2.dp,
                        border = BorderStroke(1.dp, colorResource(id = R.color.purple_200))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_account),
                            contentDescription = "",
                            tint = colorResource(id = R.color.purple_200),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                                .clickable { }
                        )
                    }

                }

            }

        }
    }

    @Composable
    fun NeptuneAppBottomNavigation(
        navController: NavHostController,
    ) {
        val items = listOf(
            BottomNavigationScreens.Home,
            BottomNavigationScreens.Categories,
            BottomNavigationScreens.Messages,
            BottomNavigationScreens.Notification
        )
        Card(
            shape = RoundedCornerShape(18.dp,18.dp,18.dp,18.dp),
            backgroundColor = Color.White,
            elevation = 10.dp,
            modifier = Modifier.padding(12.dp,2.dp,12.dp,12.dp)
        ) {
            BottomNavigation(
                backgroundColor = Color.White,
                contentColor = Color.White,
                elevation = 8.dp,
                modifier = Modifier.height(60.dp)
            ) {
                val currentRoute = CurrentRoute(navController = navController)
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            Icon(painter = painterResource(id = screen.icon), contentDescription = stringResource(id = screen.resourceId),modifier = Modifier
                                .height(35.dp)
                                .width(35.dp)
                                .padding(start = 5.dp, top = 8.dp, 5.dp, bottom = 8.dp))
                        },
                        selected = currentRoute == screen.route,
                        selectedContentColor = colorResource(id = R.color.design_default_color_primary),
                        unselectedContentColor = Color.Black,
                        alwaysShowLabel= false, // This hides the title for the unselected items
                        modifier = Modifier.fillMaxHeight(),
                        onClick = {
                            // This if check gives us a "singleTop" behavior where we do not create a
                            // second instance of the composable if we are already on that destination
                            if (currentRoute != screen.route){
                                navController.navigate(screen.route)
                            }
                        }
                    )
                }
            }
        }

    }

    @Composable
    fun MainScreenNavigationConfigurations(
        navController: NavHostController,
        context: Context
    ) {
        NavHost(navController, startDestination = BottomNavigationScreens.Home.route) {
            composable(BottomNavigationScreens.Home.route) {
                currentPage.value = BottomNavigationScreens.Home.route
                title.value = "Home"
                HomeScreen(context)
            }
            composable(BottomNavigationScreens.Categories.route) {
                currentPage.value = BottomNavigationScreens.Categories.route
                title.value = "Categories"
                CategoriesScreen(context)
            }
            composable(BottomNavigationScreens.Messages.route) {
                currentPage.value = BottomNavigationScreens.Messages.route
                title.value = "Messages"
                MessagesScreen(context)
            }
            composable(BottomNavigationScreens.Notification.route) {
                currentPage.value = BottomNavigationScreens.Notification.route
                title.value = "Notifications"
                NotificationsScreen(context)
            }
        }
    }

    @Composable
    private fun MessagesScreen(context: Context) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Messages", fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, color = colorResource(
                id = R.color.purple_200
            ))
        }
    }

    @Composable
    private fun NotificationsScreen(context: Context) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, color = colorResource(
                id = R.color.purple_200
            ))
        }
    }

    @Composable
    private fun CategoriesScreen(context: Context) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Categories", fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, color = colorResource(
                id = R.color.purple_200
            ))
        }
    }

    @Composable
    private fun HomeScreen(context: Context) {
        Box(modifier = Modifier.fillMaxSize()){
            Column() {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(20.dp, 6.dp, 20.dp, 8.dp),
                    backgroundColor = Color.White,
                    shape = RoundedCornerShape(10.dp,10.dp,10.dp,10.dp),
                    elevation = 10.dp
                ) {
                    Row {
                        IconButton(modifier = Modifier
                            .size(44.dp)
                            .padding(10.dp, 0.dp, 0.dp, 0.dp),onClick = { /*TODO*/ }) {
                            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription ="" , tint = colorResource(
                                id = R.color.purple_200
                            ), modifier = Modifier.size(30.dp))
                        }
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp), contentAlignment = Alignment.Center) {
                            var text by rememberSaveable{ mutableStateOf("") }
                            BasicTextField(modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp, 0.dp, 0.dp, 0.dp)
                                .background(
                                    MaterialTheme.colors.surface,
                                    MaterialTheme.shapes.small,
                                ),
                                value = text,
                                onValueChange = {
                                    text = it
                                },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colors.onSurface,
                                    fontSize = 16.sp
                                ),
                                decorationBox = { innerTextField ->
                                    Row(
                                        Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(Modifier.weight(1f)) {
                                            if (text.isEmpty()) Text(
                                                "Search",
                                                style = LocalTextStyle.current.copy(
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                                                    fontSize = 16.sp
                                                )
                                            )
                                            innerTextField()
                                        }
                                    }
                                }
                            )
                        }

                    }
                }
                LazyColumn {
                    items((0..5).toList()){
                        PostCard(context)
                    }
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 10.dp, 10.dp)
                    .align(alignment = Alignment.BottomEnd),
                onClick = {
                    val dialog = NewPostBottomSheetDialogFragment()
                    dialog.isCancelable = true
                    dialog.show(fm,"")
                },
                backgroundColor = colorResource(id = R.color.purple_200),
                contentColor = Color.White
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_newpost), contentDescription = "")
            }
        }
    }

    @Composable
    private fun PostCard(context: Context) {

        Card(shape = RoundedCornerShape(10.dp),elevation = 10.dp,modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {

            }
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier
                        .fillMaxWidth()){
                        Column(Modifier.fillMaxSize()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 0.dp, 10.dp, 0.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                                Box{
                                    Row{
                                        Card(shape = CircleShape,modifier = Modifier
                                            .size(30.dp)
                                            .clickable {},
                                            border = BorderStroke(1.dp, colorResource(id = R.color.purple_200))
                                        ) {
                                            Icon(
                                                modifier = Modifier
                                                    .padding(2.dp)
                                                    .clickable { }, tint = colorResource(id = R.color.purple_200),painter = painterResource(id = R.drawable.ic_account), contentDescription = "")
                                        }
                                        Column(Modifier.padding(10.dp,0.dp,0.dp,0.dp)) {
                                            Row() {
                                                Text(text = "username", fontSize = 12.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Serif)
                                            }
                                            Row() {
                                                Text(text = "1y", fontSize = 10.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Serif)
                                            }
                                        }
                                    }
                                }
                                IconButton(modifier = Modifier
                                    .size(50.dp),onClick = {  }) {
                                    Icon(painter = painterResource(id = R.drawable.ic_more), contentDescription ="" , tint = colorResource(
                                        id = R.color.purple_200
                                    ), modifier = Modifier
                                        .size(30.dp)
                                        .clickable { })
                                }
                            }
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .width((0.05).dp)
                                    .padding(10.dp, 0.dp, 10.dp, 5.dp)
                            )
                            Row{
                                Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                                    fontSize = 12.sp,modifier = Modifier.padding(10.dp, 0.dp, 10.dp, 0.dp), textAlign = TextAlign.Start)
                            }
                        }
                    }

                    Row (
                        horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                            .padding(10.dp, 10.dp, 10.dp, 5.dp)){
                        PreviewImage()
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )
                        PreviewImage()
                    }
                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .width((0.05).dp)
                            .padding(12.dp, 0.dp, 12.dp, 0.dp)
                    )
                    Row (
                        horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                            .padding(10.dp, 5.dp, 10.dp, 10.dp)){
                        PreviewImage()
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )
                        PreviewImage()
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)) {
                        IconButton(modifier = Modifier
                            .size(50.dp)
                            .padding(14.dp, 12.dp, 12.dp, 12.dp),onClick = {  }) {
                            Icon(painter = painterResource(id = R.drawable.ic_like), contentDescription ="" , tint = Color.Red, modifier = Modifier
                                .size(30.dp)
                                .clickable { })
                        }
                        IconButton(modifier = Modifier
                            .size(50.dp)
                            .padding(12.dp),onClick = {  }) {
                            Icon(painter = painterResource(id = R.drawable.ic_save), contentDescription ="" , tint = colorResource(
                                id = R.color.black
                            ), modifier = Modifier
                                .size(30.dp)
                                .clickable { })
                        }
                        IconButton(modifier = Modifier
                            .size(50.dp)
                            .padding(12.dp),onClick = {  }) {
                            Icon(painter = painterResource(id = R.drawable.ic_comment), contentDescription ="" , tint = colorResource(
                                id = R.color.black
                            ), modifier = Modifier
                                .size(30.dp)
                                .clickable { })
                        }
                        IconButton(modifier = Modifier
                            .size(50.dp)
                            .padding(12.dp),onClick = {  }) {
                            Icon(painter = painterResource(id = R.drawable.ic_share), contentDescription ="" , tint = colorResource(
                                id = R.color.black
                            ), modifier = Modifier
                                .size(30.dp)
                                .clickable { })
                        }
                    }
                }
            }
        }

    }

    @Composable
    fun PreviewImage() {
        Image(painter = rememberImagePainter("https://cookingfromheart.com/wp-content/uploads/2017/03/Paneer-Tikka-Masala-4.jpg"),
            contentDescription = "", modifier = Modifier
                .fillMaxHeight()
                .width(imageWidth.dp), contentScale = ContentScale.FillBounds)
    }


    @Composable
    fun CurrentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }

    @Composable
    fun BackHandler(enabled: Boolean = true, navController: NavHostController) {
        val context = (LocalContext.current as? Activity)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        if (currentRoute == "Home"){
//        context?.finish()
        }
    }

    override fun onBackPressed() {
        if (currentPage.value == BottomNavigationScreens.Home.route){
            val quitDialog = QuitDialog()
            val fm: FragmentManager = this.supportFragmentManager
            quitDialog.show(fm,"Quick View")
        }else{
            super.onBackPressed()
        }
    }

}
