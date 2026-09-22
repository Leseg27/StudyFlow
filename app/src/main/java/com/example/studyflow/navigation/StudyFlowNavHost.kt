package com.example.studyflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyflow.ui.screens.home.HomeScreen
import com.example.studyflow.ui.screens.login.LoginScreen
import com.example.studyflow.ui.screens.register.RegisterScreen
import com.example.studyflow.ui.screens.settings.SettingsScreen
import com.example.studyflow.ui.screens.splash.SplashScreen
import com.example.studyflow.ui.screens.study.StudySessionScreen
import com.example.studyflow.ui.screens.subjects.SubjectsScreen
import com.example.studyflow.ui.screens.tasks.TasksScreen

@Composable
fun StudyFlowNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onOpenTasks = { navController.navigate(Routes.TASKS) },
                onOpenSubjects = { navController.navigate(Routes.SUBJECTS) },
                onOpenStudy = { navController.navigate(Routes.STUDY) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.TASKS) { TasksScreen(onBack = { navController.popBackStack() }) }

        composable(Routes.SUBJECTS) { SubjectsScreen(onBack = { navController.popBackStack() }) }

        composable(Routes.STUDY) { StudySessionScreen(onBack = { navController.popBackStack() }) }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}