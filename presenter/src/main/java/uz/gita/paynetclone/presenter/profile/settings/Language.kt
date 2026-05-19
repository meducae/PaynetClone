package uz.gita.paynetclone.presenter.profile.settings

sealed class Language(val code: String, val name: String) {
    data class Uzbek(val isCyrillic: Boolean = false) : Language("uz", "O'zbek")
    data class English(val region: String = "US") : Language("en", "English")
    data object Russian : Language("ru", "Русский")
}
