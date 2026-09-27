package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.DjCyan
import com.example.ui.theme.DjPurple
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.NavigationTab

@Composable
fun SpotifyBottomNav(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .background(DeepBlack)
            .testTag("spotify_bottom_navigation"),
        containerColor = DeepBlack,
        tonalElevation = 0.dp
    ) {
        // Home
        NavigationBarItem(
            selected = selectedTab == NavigationTab.HOME,
            onClick = { onTabSelected(NavigationTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == NavigationTab.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                selectedTextColor = TextPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextMuted,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        // Search
        NavigationBarItem(
            selected = selectedTab == NavigationTab.SEARCH,
            onClick = { onTabSelected(NavigationTab.SEARCH) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Search",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == NavigationTab.SEARCH) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                selectedTextColor = TextPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextMuted,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_tab_search")
        )

        // Your Library
        NavigationBarItem(
            selected = selectedTab == NavigationTab.LIBRARY,
            onClick = { onTabSelected(NavigationTab.LIBRARY) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.LIBRARY) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                    contentDescription = "Your Library",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Your Library",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == NavigationTab.LIBRARY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                selectedTextColor = TextPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextMuted,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_tab_library")
        )

        // AI DJ (Highlighted tab with special glowing orb icon)
        NavigationBarItem(
            selected = selectedTab == NavigationTab.AI_DJ,
            onClick = { onTabSelected(NavigationTab.AI_DJ) },
            icon = {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(listOf(DjPurple, DjCyan)),
                            shape = CircleShape
                        )
                        .padding(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "AI DJ",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            label = {
                Text(
                    text = "AI DJ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == NavigationTab.AI_DJ) DjCyan else TextSecondary
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DjCyan,
                selectedTextColor = DjCyan,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextMuted,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_tab_ai_dj")
        )
    }
}
