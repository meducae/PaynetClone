package uz.gita.paynetclone.data.remote.dto.kyc

data class KycStatusResponse(
    val success: Boolean,
    val data: KycStatusData?
)

data class KycStatusData(
    val status: String,
    val rejectionReason: String?
)

data class KycSubmitRequest(
    val passportSeries: String,
    val passportNumber: String,
    val birthDate: String,
    val selfieBase64: String
)

data class KycSubmitResponse(
    val success: Boolean,
    val error: KycError? = null
)

data class KycError(
    val code: String,
    val message: String
)
