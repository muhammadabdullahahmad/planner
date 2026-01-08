package com.example.planner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.planner.ui.screens.achievements.AchievementsScreen
import com.example.planner.ui.screens.achievements.LeaderboardScreen
import com.example.planner.ui.screens.auth.LoginScreen
import com.example.planner.ui.screens.auth.SetupFamilyScreen
import com.example.planner.ui.screens.auth.WelcomeScreen
import com.example.planner.ui.screens.dashboard.child.ChildDashboardScreen
import com.example.planner.ui.screens.calendar.CalendarScreen
import com.example.planner.ui.screens.calendar.CreateEventScreen
import com.example.planner.ui.screens.calendar.EventDetailScreen
import com.example.planner.ui.screens.dashboard.DashboardScreen
import com.example.planner.ui.screens.members.AddMemberScreen
import com.example.planner.ui.screens.members.MemberDetailScreen
import com.example.planner.ui.screens.members.MembersScreen
import com.example.planner.ui.screens.profile.EditProfileScreen
import com.example.planner.ui.screens.profile.ProfileScreen
import com.example.planner.ui.screens.tasks.CreateEditTaskScreen
import com.example.planner.ui.screens.tasks.TaskDetailScreen
import com.example.planner.ui.screens.tasks.TaskListScreen

@Composable
fun PlannerNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth screens
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(Screen.SetupFamily.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { isAdmin ->
                    val destination = if (isAdmin) Screen.Dashboard.route else Screen.ChildDashboard.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSetupFamily = {
                    navController.navigate(Screen.Welcome.route)
                }
            )
        }

        composable(Screen.SetupFamily.route) {
            SetupFamilyScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.SetupFamily.route) { inclusive = true }
                    }
                }
            )
        }

        // Main screens
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onCreateTask = {
                    navController.navigate(Screen.CreateTask.route)
                },
                onViewAllTasks = {
                    navController.navigate(Screen.Tasks.route)
                }
            )
        }

        composable(Screen.ChildDashboard.route) {
            ChildDashboardScreen()
        }

        composable(Screen.Tasks.route) {
            TaskListScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onCreateTask = {
                    navController.navigate(Screen.CreateTask.route)
                }
            )
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                onEventClick = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                },
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        composable(Screen.Members.route) {
            MembersScreen(
                onMemberClick = { memberId ->
                    navController.navigate(Screen.MemberDetail.createRoute(memberId))
                },
                onAddMember = {
                    navController.navigate(Screen.AddMember.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onViewAchievements = {
                    navController.navigate(Screen.Achievements.route)
                },
                onViewLeaderboard = {
                    navController.navigate(Screen.Leaderboard.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Task flows
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.EditTask.createRoute(taskId)) }
            )
        }

        composable(Screen.CreateTask.route) {
            CreateEditTaskScreen(
                taskId = null,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            CreateEditTaskScreen(
                taskId = taskId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        // Member flows
        composable(
            route = Screen.MemberDetail.route,
            arguments = listOf(navArgument("memberId") { type = NavType.LongType })
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getLong("memberId") ?: return@composable
            MemberDetailScreen(
                memberId = memberId,
                onBack = { navController.popBackStack() },
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        composable(Screen.AddMember.route) {
            AddMemberScreen(
                onBack = { navController.popBackStack() },
                onMemberAdded = { navController.popBackStack() }
            )
        }

        // Calendar flows
        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.LongType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: return@composable
            EventDetailScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateEvent.route) {
            CreateEventScreen(
                onBack = { navController.popBackStack() },
                onEventCreated = { navController.popBackStack() }
            )
        }

        // Achievements
        composable(Screen.Achievements.route) {
            AchievementsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Profile flows
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
