package com.banquemisr.challenge05.presentation.movielist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banquemisr.challenge05.BuildConfig
import com.banquemisr.challenge05.R

import com.banquemisr.challenge05.domain.models.Movie
import com.banquemisr.challenge05.presentation.common.ErrorView
import com.banquemisr.challenge05.presentation.common.LoadingView
import com.banquemisr.challenge05.presentation.common.MovieItem
import com.banquemisr.challenge05.presentation.movielist.MovieListContract.Effect
import com.banquemisr.challenge05.presentation.movielist.MovieListContract.Event
import com.banquemisr.challenge05.presentation.movielist.MovieListContract.Tab
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    onNavigateToMovieDetail: (Int) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    
    // Handle UI effects
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateToMovieDetail -> {
                    onNavigateToMovieDetail(effect.movieId)
                }
                is Effect.ShowSnackbar -> {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = effect.message
                        )
                    }
                }
            }
        }
    }
    
    Scaffold(
      //  scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        viewModel.onEvent(Event.RefreshMovies(state.selectedTab))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                Tab(
                    selected = state.selectedTab == Tab.NOW_PLAYING,
                    onClick = { viewModel.onEvent(Event.SelectTab(Tab.NOW_PLAYING)) },
                    text = { Text("Now Playing") }
                )
                Tab(
                    selected = state.selectedTab == Tab.POPULAR,
                    onClick = { viewModel.onEvent(Event.SelectTab(Tab.POPULAR)) },
                    text = { Text("Popular") }
                )
                Tab(
                    selected = state.selectedTab == Tab.UPCOMING,
                    onClick = { viewModel.onEvent(Event.SelectTab(Tab.UPCOMING)) },
                    text = { Text("Upcoming") }
                )
            }
            
            // Content based on selected tab
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading() -> {
                        LoadingView(modifier = Modifier.align(Alignment.Center))
                    }
                    state.getCurrentError() != null -> {
                        ErrorView(
                            message = state.getCurrentError() ?: "Unknown error",
                            onRetry = {
                                viewModel.onEvent(Event.RefreshMovies(state.selectedTab))
                            },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        MovieContent(
                            movies = state.getCurrentMovies(),
                            onMovieClick = { movieId ->
                                viewModel.onEvent(Event.MovieClicked(movieId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieContent(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (movies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No movies available",
                    style =  MaterialTheme.typography.headlineMedium
                )
            }
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movies) { movie ->
                    MovieItem(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
            }
        }
    }
}