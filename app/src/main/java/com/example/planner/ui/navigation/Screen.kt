package com.example.planner.ui.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object SetupFamily : Screen("setup_family")

    // Main (with bottom nav)
    object Dashboard : Screen("dashboard")
    object Tasks : Screen("tasks")
    object Calendar : Screen("calendar")
    object Members : Screen("members")
    object Profile : Screen("profile")

    // Task flows
    object TaskDetail : Screen("task/{taskId}") {
        fun createRoute(taskId: Long) = "task/$taskId"
    }
    object CreateTask : Screen("task/create")
    object EditTask : Screen("task/edit/{taskId}") {
        fun createRoute(taskId: Long) = "task/edit/$taskId"
    }

    // Member flows
    object MemberDetail : Screen("member/{memberId}") {
        fun createRoute(memberId: Long) = "member/$memberId"
    }
    object AddMember : Screen("member/add")

    // Calendar flows
    object EventDetail : Screen("event/{eventId}") {
        fun createRoute(eventId: Long) = "event/$eventId"
    }
    object CreateEvent : Screen("event/create")

    // Achievements
    object Achievements : Screen("achievements")
    object Leaderboard : Screen("leaderboard")

    // Profile
    object EditProfile : Screen("profile/edit")
}
