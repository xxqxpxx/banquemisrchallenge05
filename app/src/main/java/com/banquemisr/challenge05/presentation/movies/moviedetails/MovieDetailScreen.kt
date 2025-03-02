package com.banquemisr.challenge05.presentation.movies.moviedetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.banquemisr.challenge05.BuildConfig
import com.banquemisr.challenge05.domain.models.MovieDetail
import com.banquemisr.challenge05.presentation.common.ErrorView
import com.banquemisr.challenge05.presentation.common.LoadingView
import com.banquemisr.challenge05.presentation.movies.moviedetails.MovieDetailContract.*

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MovieDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    // Handle UI effects
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateBack -> {
                    onNavigateBack()
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
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = state.movieDetail?.title ?: "Movie Detail") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onEvent(Event.NavigateBack)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.onEvent(Event.RefreshMovieDetail)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    LoadingView(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    ErrorView(
                        message = state.error ?: "Unknown error",
                        onRetry = {
                            viewModel.onEvent(Event.RefreshMovieDetail)
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.movieDetail != null -> {
                    MovieDetailContent(
                        movieDetail = state.movieDetail!!,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovieDetailContent(
    movieDetail: MovieDetail,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
    ) {
        // Backdrop image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (movieDetail.backdropPath != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(BuildConfig.IMAGE_BASE_URL + movieDetail.backdropPath)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "Movie Backdrop",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.5f))
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Poster image
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.surface)
            ) {
                if (movieDetail.posterPath != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(BuildConfig.IMAGE_BASE_URL + movieDetail.posterPath)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Movie Poster",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.primary.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No\nPoster",
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }

            // Movie info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = movieDetail.title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Release date and runtime
                Row {
                    Text(
                        text = "Released: ${movieDetail.releaseDate}",
                        style = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    movieDetail.runtime?.let {
                        Text(
                            text = "• ${it} min",
                            style = MaterialTheme.typography.body2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${movieDetail.voteAverage}/10 (${movieDetail.voteCount} votes)",
                        style = MaterialTheme.typography.body2
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Genres
                FlowRow(
                    mainAxisSpacing = 4 ,//4.dp,
                    crossAxisSpacing = 4 // 4.dp
                ) {
                    movieDetail.genres.forEach { genre ->
                        Chip(
                            onClick = { /* No action */ },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(text = genre)
                        }
                    }
                }
            }
        }

        Divider()

        // Tagline
        if (!movieDetail.tagline.isNullOrBlank()) {
            Text(
                text = movieDetail.tagline,
                style = MaterialTheme.typography.subtitle1,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Divider()
        }

        // Overview
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movieDetail.overview,
                style = MaterialTheme.typography.body1
            )
        }

        Divider()

        // Additional info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Status", value = movieDetail.status)

            if (movieDetail.budget > 0) {
                InfoRow(label = "Budget", value = "$${formatAmount(movieDetail.budget)}")
            }

            if (movieDetail.revenue > 0) {
                InfoRow(label = "Revenue", value = "$${formatAmount(movieDetail.revenue)}")
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: Int = 0,
    crossAxisSpacing: Int = 0,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val rowConstraints = constraints.copy(minHeight = 0)
        val rows = mutableListOf<MutableList<androidx.compose.ui.layout.Placeable>>()
        var rowWidth = 0
        var rowHeight = 0
        var rowIndex = 0

        rows.add(mutableListOf())

        for (measurable in measurables) {
            val placeable = measurable.measure(rowConstraints)

            if (rowWidth + placeable.width + mainAxisSpacing > constraints.maxWidth) {
                // New row is needed
                rows.add(mutableListOf())
                rowIndex++
                rowWidth = 0
                rowHeight = 0
            }

            rows[rowIndex].add(placeable)
            rowWidth += placeable.width + if (rows[rowIndex].size > 1) mainAxisSpacing.dp.roundToPx() else 0
            rowHeight = maxOf(rowHeight, placeable.height)
        }

        val rowHeights = rows.map { rowPlaceables ->
            rowPlaceables.maxOfOrNull { it.height } ?: 0
        }

        val totalHeight = rowHeights.sum() + (crossAxisSpacing.dp.roundToPx() * (rows.size - 1))

        layout(constraints.maxWidth, totalHeight) {
            var y = 0

            rows.forEachIndexed { i, rowPlaceables ->
                var x = 0

                rowPlaceables.forEach { placeable ->
                    placeable.place(x, y)
                    x += placeable.width + mainAxisSpacing.dp.roundToPx()
                }

                y += rowHeights[i] + if (i < rows.size - 1) crossAxisSpacing.dp.roundToPx() else 0
            }
        }
    }
}

private fun formatAmount(amount: Long): String {
    return when {
        amount >= 1_000_000_000 -> String.format("%.1fB", amount / 1_000_000_000.0)
        amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000.0)
        amount >= 1_000 -> String.format("%.1fK", amount / 1_000.0)
        else -> amount.toString()
    }
}