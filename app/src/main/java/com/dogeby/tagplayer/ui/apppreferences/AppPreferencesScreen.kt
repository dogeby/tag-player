package com.dogeby.tagplayer.ui.apppreferences

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.datastore.app.AppThemeMode
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun AppPreferencesRoute(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppPreferencesViewModel = hiltViewModel(),
) {
    val appPreferencesUiState by viewModel.appPreferencesUiState.collectAsState()
    val encodedTagsUiState by viewModel.encodedTags.collectAsState()
    val loadTagResultUiState by viewModel.loadTagsResultUiState.collectAsState(initial = LoadTagsResultUiState.Nothing)

    AppPreferencesScreen(
        onNavigateUp = onNavigateUp,
        appPreferencesUiState = appPreferencesUiState,
        encodedTagsUiState = encodedTagsUiState,
        loadTagResultUiState = loadTagResultUiState,
        onSetAppThemeMode = viewModel::setAppThemeMode,
        onSetAutoRotation = viewModel::setAutoRotation,
        onLoadTags = viewModel::loadTags,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPreferencesScreen(
    onNavigateUp: () -> Unit,
    appPreferencesUiState: AppPreferencesUiState,
    encodedTagsUiState: EncodeTagsUiState,
    loadTagResultUiState: LoadTagsResultUiState,
    onSetAppThemeMode: (appThemeMode: AppThemeMode) -> Unit,
    onSetAutoRotation: (Boolean) -> Unit,
    onLoadTags: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.appPreferences_topAppBar_title))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { contentPadding ->
        val context = LocalContext.current
        LaunchedEffect(key1 = loadTagResultUiState) {
            when (loadTagResultUiState) {
                is LoadTagsResultUiState.Success -> {
                    Toast.makeText(context, context.getString(R.string.appPreferences_backup_load_tags_success), Toast.LENGTH_SHORT).show()
                }
                is LoadTagsResultUiState.Failure -> {
                    Toast.makeText(context, context.getString(R.string.appPreferences_backup_load_tags_failure), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
        when (appPreferencesUiState) {
            AppPreferencesUiState.Loading -> { /*TODO*/ }
            is AppPreferencesUiState.Success -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding)
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                        .verticalScroll(rememberScrollState()),
                ) {
                    AppThemeModePreferencesItem(
                        currentAppThemeMode = { appPreferencesUiState.appThemeMode },
                        onSetAppThemeMode = onSetAppThemeMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    AppLocalePreferencesItem(modifier = Modifier.fillMaxWidth())
                    AutoRotationPreferencesItem(
                        currentAutoRotationValue = { appPreferencesUiState.autoRotation },
                        onSetAutoRotation = onSetAutoRotation,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    BackupPreferencesItem(
                        encodedTagsUiState = encodedTagsUiState,
                        onLoadTags = onLoadTags,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OssLicensesPreferenceItem(Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreferencesScreenPreview() {
    TagPlayerTheme {
        AppPreferencesScreen(
            onNavigateUp = {},
            appPreferencesUiState = AppPreferencesUiState.Success(AppThemeMode.SYSTEM_SETTING, false),
            encodedTagsUiState = EncodeTagsUiState.Success(""),
            loadTagResultUiState = LoadTagsResultUiState.Failure,
            onSetAppThemeMode = {},
            onSetAutoRotation = {},
            onLoadTags = {},
        )
    }
}
