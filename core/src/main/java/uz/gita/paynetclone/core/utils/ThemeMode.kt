package uz.gita.paynetclone.core.utils

sealed class ThemeMode {
    data object System : ThemeMode()
    data object Light : ThemeMode()
    data object Dark : ThemeMode()
}
