package uz.gita.paynetclone.entity.kyc

data class KycStatus(
    val status: String,
    val rejectionReason: String?
)

data class KycSubmitData(
    val passportSeries: String,
    val passportNumber: String,
    val birthDate: String,
    val selfieBase64: String
)
