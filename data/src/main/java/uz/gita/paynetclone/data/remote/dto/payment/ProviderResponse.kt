package uz.gita.paynetclone.data.remote.dto.payment

import uz.gita.paynetclone.entity.payment.Provider

data class ProviderResponse(
    val success: Boolean,
    val data: List<ProviderDto>?
)

data class ProviderDto(
    val id: String?,
    val name: String?,
    val category: String?,
    val logoUrl: String?
) {
    fun toEntity() = Provider(
        id = id ?: "",
        name = name ?: "",
        category = category ?: "",
        logoUrl = logoUrl
    )
}
