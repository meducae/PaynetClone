package uz.gita.paynetclone.entity.payment

data class Provider(
    val id: String,
    val name: String,
    val category: String,
    val logoUrl: String?
)
