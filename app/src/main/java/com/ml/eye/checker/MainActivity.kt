package com.ml.eye.checker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ml.eye.checker.ui.theme.EyeDiseaseCheckerTheme
import com.ml.eye.checker.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            EyeDiseaseCheckerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PurpleGrey80
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "splash_screen"
                    ) {
                        composable("splash_screen") {
                            SplashScreen(navController)
                        }
                        composable("home"){
                            MainScreen()
                        }


                    }


                }
            }
        }
    }
}