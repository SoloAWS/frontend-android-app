package com.misw.abcalls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.abcalls.data.model.Incident
import com.misw.abcalls.ui.viewmodel.IncidentListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.misw.abcalls.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentListScreen(
    onCreateIncident: () -> Unit,
    viewModel: IncidentListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.incients_title)) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateIncident,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.incident_create_title),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.search)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search)) },
                singleLine = true
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(uiState.error!!)
                            Button(onClick = { viewModel.retryLoading() }) {
                                Text(stringResource(R.string.try_again))
                            }
                        }
                    }
                }

                else -> {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = isRefreshing.value),
                        onRefresh = {
                            isRefreshing.value = true
                            viewModel.refresh()
                            isRefreshing.value = false
                        }
                    ) {
                        when {
                            uiState.filteredIncidents.isEmpty() && uiState.searchQuery.isNotEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(stringResource(R.string.no_incidents_found))
                                }
                            }

                            uiState.incidents.isEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(stringResource(R.string.no_inicidents_registered))
                                }
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(uiState.filteredIncidents) { incident ->
                                        IncidentCard(incident = incident)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IncidentCard(incident: Incident) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = incident.description,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PriorityChip(priority = incident.priority)
                Text(
                    text = when(incident.state.lowercase()) {
                        "open" -> stringResource(R.string.open)
                        "closed" -> stringResource(R.string.resolved)
                        "escalated" -> stringResource(R.string.escalated)
                        "in_progress" -> stringResource(R.string.in_progress)
                        else -> incident.state
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = incident.formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PriorityChip(priority: String) {
    val (backgroundColor, contentColor) = when (priority.lowercase()) {
        "high" -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        "medium" -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = when (priority.lowercase()) {
                "high" -> stringResource(R.string.high)
                "medium" -> stringResource(R.string.medium)
                "low" -> stringResource(R.string.low)
                else -> priority
            },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}