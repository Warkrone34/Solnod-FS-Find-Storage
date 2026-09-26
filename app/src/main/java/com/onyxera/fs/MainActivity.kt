package com.onyxera.fs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.edit
import com.onyxera.fs.data.MaterialEntity
import com.onyxera.fs.ui.screens.AddMaterialScreen
import com.onyxera.fs.ui.screens.EditMaterialScreen
import com.onyxera.fs.ui.screens.MainScreen
import com.onyxera.fs.ui.screens.MaterialDetailScreen
import com.onyxera.fs.ui.screens.SettingsScreen
import com.onyxera.fs.ui.screens.TrashScreen
import com.onyxera.fs.ui.theme.AppThemeColor
import com.onyxera.fs.ui.theme.AppThemeMode
import com.onyxera.fs.ui.theme.FindAndStorageTheme
import com.onyxera.fs.util.CameraHelper
import com.onyxera.fs.util.SolnodConstants
import com.onyxera.fs.util.SpeechHelper
import com.onyxera.fs.viewmodel.AddMaterialViewModel
import com.onyxera.fs.viewmodel.EditMaterialViewModel
import com.onyxera.fs.viewmodel.MaterialViewModel
import com.onyxera.fs.viewmodel.MaterialViewModelFactory

/**
 * Solnod F&S Ana Aktivitesi.
 * Dinamik tema ve sistem varsayılanı açık/koyu mod yönetimi ile anlık ekran değişimi.
 */
class MainActivity : ComponentActivity() {

    private lateinit var speechHelper: SpeechHelper

    private val mainViewModel: MaterialViewModel by viewModels {
        MaterialViewModelFactory((application as FAndSApplication).repository)
    }

    private val addViewModel: AddMaterialViewModel by viewModels()
    private val editViewModel: EditMaterialViewModel by viewModels()

    private var currentScreenState = mutableStateOf("Main")
    private var selectedMaterial = mutableStateOf<MaterialEntity?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        speechHelper = SpeechHelper(this)

        val sharedPreferences = getSharedPreferences(SolnodConstants.PREFS_NAME, MODE_PRIVATE)

        setContent {
            val systemInDark = isSystemInDarkTheme()

            // İlk açılışta varsayılan "SYSTEM" gelir, bu sayede cihazın açık/koyu modu doğrudan seçilir
            val savedModeStr = sharedPreferences.getString(SolnodConstants.KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
            var themeMode by remember {
                mutableStateOf(
                    try {
                        AppThemeMode.valueOf(savedModeStr ?: AppThemeMode.SYSTEM.name)
                    } catch (e: Exception) {
                        AppThemeMode.SYSTEM
                    }
                )
            }

            val savedColorStr = sharedPreferences.getString(SolnodConstants.KEY_THEME_COLOR, AppThemeColor.SOLNOD_MARINE.name)
            var themeColor by remember {
                mutableStateOf(
                    try {
                        AppThemeColor.valueOf(savedColorStr ?: AppThemeColor.SOLNOD_MARINE.name)
                    } catch (e: Exception) {
                        AppThemeColor.SOLNOD_MARINE
                    }
                )
            }

            val isDarkTheme = when (themeMode) {
                AppThemeMode.SYSTEM -> systemInDark
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            FindAndStorageTheme(
                darkTheme = isDarkTheme,
                themeColor = themeColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val ships by mainViewModel.allShips.collectAsState()

                    when (currentScreenState.value) {
                        "Main" -> MainScreen(
                            viewModel = mainViewModel,
                            onAddClick = { preselectedShip ->
                                if (preselectedShip != null) {
                                    addViewModel.updateShipName(preselectedShip)
                                }
                                currentScreenState.value = "AddMaterial"
                            },
                            onItemClick = { item ->
                                selectedMaterial.value = item
                                currentScreenState.value = "Detail"
                            },
                            onSettingsClick = { currentScreenState.value = "Settings" }
                        )
                        "Detail" -> {
                            BackHandler {
                                selectedMaterial.value = null
                                currentScreenState.value = "Main"
                            }
                            selectedMaterial.value?.let { item ->
                                MaterialDetailScreen(
                                    item = item,
                                    onNavigateBack = {
                                        selectedMaterial.value = null
                                        currentScreenState.value = "Main"
                                    },
                                    onEditClick = { currentScreenState.value = "EditMaterial" }
                                )
                            }
                        }
                        "AddMaterial" -> {
                            BackHandler {
                                addViewModel.resetData()
                                currentScreenState.value = "Main"
                            }
                            AddMaterialScreen(
                                viewModel = addViewModel,
                                ships = ships,
                                onAddNewShip = { name, details, onCreated ->
                                    mainViewModel.insertShip(name, details) { newShip ->
                                        onCreated(newShip)
                                    }
                                },
                                speechHelper = speechHelper,
                                cameraHelper = CameraHelper(this@MainActivity),
                                onSave = { entity ->
                                    mainViewModel.insertDirectly(entity)
                                    addViewModel.resetData()
                                    currentScreenState.value = "Main"
                                },
                                onCancel = {
                                    addViewModel.resetData()
                                    currentScreenState.value = "Main"
                                }
                            )
                        }
                        "EditMaterial" -> {
                            BackHandler {
                                editViewModel.resetData()
                                currentScreenState.value = "Detail"
                            }
                            selectedMaterial.value?.let { itemToEdit ->
                                EditMaterialScreen(
                                    materialToEdit = itemToEdit,
                                    viewModel = editViewModel,
                                    ships = ships,
                                    onAddNewShip = { name, details, onCreated ->
                                        mainViewModel.insertShip(name, details) { newShip ->
                                            onCreated(newShip)
                                        }
                                    },
                                    speechHelper = speechHelper,
                                    cameraHelper = CameraHelper(this@MainActivity),
                                    onSave = { updatedEntity ->
                                        mainViewModel.updateDirectly(updatedEntity)
                                        editViewModel.resetData()
                                        selectedMaterial.value = updatedEntity
                                        currentScreenState.value = "Detail"
                                    },
                                    onCancel = {
                                        editViewModel.resetData()
                                        currentScreenState.value = "Detail"
                                    }
                                )
                            }
                        }
                        "Settings" -> {
                            BackHandler { currentScreenState.value = "Main" }
                            SettingsScreen(
                                viewModel = mainViewModel,
                                onNavigateBack = { currentScreenState.value = "Main" },
                                onNavigateToTrash = { currentScreenState.value = "Trash" },
                                themeMode = themeMode,
                                onThemeModeChange = { newMode ->
                                    themeMode = newMode
                                    sharedPreferences.edit { putString(SolnodConstants.KEY_THEME_MODE, newMode.name) }
                                },
                                themeColor = themeColor,
                                onThemeColorChange = { newColor ->
                                    themeColor = newColor
                                    sharedPreferences.edit { putString(SolnodConstants.KEY_THEME_COLOR, newColor.name) }
                                }
                            )
                        }
                        "Trash" -> {
                            BackHandler { currentScreenState.value = "Settings" }
                            TrashScreen(
                                viewModel = mainViewModel,
                                onNavigateBack = { currentScreenState.value = "Settings" }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechHelper.destroy()
    }
}