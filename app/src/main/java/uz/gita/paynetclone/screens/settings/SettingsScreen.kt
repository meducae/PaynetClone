package uz.gita.paynetclone.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.gita.paynetclone.R
import uz.gita.paynetclone.core.utils.PrefsManager
import uz.gita.paynetclone.core.utils.ThemeMode
import uz.gita.paynetclone.screens.SatoshiBold
import uz.gita.paynetclone.screens.SatoshiMedium
import uz.gita.paynetclone.ui.theme.RedExit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile),
                        fontFamily = SatoshiBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(R.drawable.back),
                            modifier = Modifier.size(24.dp),
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            ProfileSectionCard(title = stringResource(id = R.string.my_data)) {
                ProfileMenuItem(
                    painter = painterResource(R.drawable.malumotnoma),
                    text = stringResource(id = R.string.personal_cabinet),
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileSectionCard(title = stringResource(id = R.string.support)) {
                ProfileMenuItem(
                    painter = painterResource(R.drawable.chat),
                    text = stringResource(id = R.string.chat_assistant),
                    onClick = { }
                )
                ProfileMenuItem(
                    painter = painterResource(R.drawable.phonecall),
                    text = stringResource(id = R.string.call),
                    onClick = { }
                )
                ProfileMenuItem(
                    painter = painterResource(R.drawable.email),
                    text = stringResource(id = R.string.write_email),
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileSectionCard(title = stringResource(id = R.string.useful_info)) {
                ProfileMenuItem(
                    iconPainter = painterResource(R.drawable.info), iconTint = MaterialTheme.colorScheme.secondary,
                    text = stringResource(id = R.string.about_paynet),
                    onClick = { }
                )
                ProfileMenuItem(
                    painter = painterResource(R.drawable.location),
                    text = stringResource(id = R.string.paynet_branches),
                    onClick = { }
                )
                ProfileMenuItem(
                    iconPainter = painterResource(R.drawable.question),
                    iconTint = MaterialTheme.colorScheme.secondary,
                    text = stringResource(id = R.string.reference),
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Application Settings
            ProfileSectionCard(title = stringResource(id = R.string.app_settings)) {
                ThemeSelectionItem()
                LanguageSelectionItem()
            }

            Spacer(modifier = Modifier.height(16.dp))

            SingleProfileMenuItem(
                painter = painterResource(R.drawable.star),
                text = stringResource(id = R.string.rate_app),
                onClick = {}
            )

            Spacer(modifier = Modifier.height(24.dp))

            ExitButton(onClick = { })

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ThemeSelectionItem() {
    var expanded by remember { mutableStateOf(false) }
    val currentTheme by PrefsManager.themeMode.collectAsState()

    val themeText = when (currentTheme) {
        ThemeMode.System -> stringResource(id = R.string.theme_system)
        ThemeMode.Light -> stringResource(id = R.string.theme_light)
        ThemeMode.Dark -> stringResource(id = R.string.theme_dark)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        ProfileMenuItem(
            text = stringResource(id = R.string.theme),
            onClick = { expanded = true },
            painter = painterResource(R.drawable.settings),
            trailingContent = {
                Text(
                    text = themeText,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = SatoshiMedium,
                    fontSize = 14.sp
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.theme_system), color = MaterialTheme.colorScheme.onSurface) },
                onClick = { PrefsManager.setThemeMode(ThemeMode.System); expanded = false }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.theme_light), color = MaterialTheme.colorScheme.onSurface) },
                onClick = { PrefsManager.setThemeMode(ThemeMode.Light); expanded = false }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.theme_dark), color = MaterialTheme.colorScheme.onSurface) },
                onClick = { PrefsManager.setThemeMode(ThemeMode.Dark); expanded = false }
            )
        }
    }
}

@Composable
fun LanguageSelectionItem() {
    var expanded by remember { mutableStateOf(false) }
    val currentLang by PrefsManager.language.collectAsState()

    val langText = when (currentLang) {
        "uz" -> stringResource(id = R.string.language_uz)
        "ru" -> stringResource(id = R.string.language_ru)
        "en" -> stringResource(id = R.string.language_en)
        else -> stringResource(id = R.string.language_uz)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        ProfileMenuItem(
            text = stringResource(id = R.string.language),
            onClick = { expanded = true },
            painter = painterResource(R.drawable.malumotnoma), // Used a placeholder icon since globe icon is missing
            trailingContent = {
                Text(
                    text = langText,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = SatoshiMedium,
                    fontSize = 14.sp
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.language_uz), color = MaterialTheme.colorScheme.onSurface) },
                onClick = { PrefsManager.setLanguage("uz"); expanded = false }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.language_ru), color = MaterialTheme.colorScheme.onSurface) },
                onClick = { PrefsManager.setLanguage("ru"); expanded = false }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.language_en), color = MaterialTheme.colorScheme.onSurface) },
                onClick = { PrefsManager.setLanguage("en"); expanded = false }
            )
        }
    }
}

@Composable
fun ProfileSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = SatoshiBold,
                fontSize = 16.sp
            )
            HorizontalDivider(Modifier, thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            content()
        }
    }
}

@Composable
fun ProfileMenuItem(
    text: String,
    onClick: () -> Unit,
    painter: Painter? = null,
    iconPainter: Painter? = null,
    iconTint: Color = MaterialTheme.colorScheme.secondary,
    trailingContent: (@Composable () -> Unit)? = null
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (painter != null) {
                Icon(
                    painter = painter,
                    contentDescription = text,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            } else if (iconPainter != null) {
                Icon(
                    painter = iconPainter,
                    contentDescription = text,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontFamily = SatoshiMedium
            )
            
            if (trailingContent != null) {
                trailingContent()
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Icon(
                painter = painterResource(R.drawable.arrowback),
                contentDescription = stringResource(id = R.string.go),
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SingleProfileMenuItem(text: String, painter: Painter, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        ProfileMenuItem(text = text, onClick = onClick, painter = painter)
    }
}

@Composable
fun ExitButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.fireexit),
                contentDescription = stringResource(id = R.string.exit),
                tint = RedExit,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.logout),
                color = RedExit,
                fontFamily = SatoshiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SettingsScreen()
}