package uz.gita.paynetclone.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.gita.paynetclone.data.remote.api.KycApi
import uz.gita.paynetclone.data.remote.dto.kyc.KycSubmitRequest
import uz.gita.paynetclone.entity.kyc.KycStatus
import uz.gita.paynetclone.entity.kyc.KycSubmitData
import uz.gita.paynetclone.usecase.kyc.KycRepository
import javax.inject.Inject

class KycRepositoryImpl @Inject constructor(
    private val api: KycApi
) : KycRepository {
    override fun getKycStatus(): Flow<Result<KycStatus>> = flow {
        try {
            val response = api.getKycStatus()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    emit(Result.success(KycStatus(body.data.status, body.data.rejectionReason)))
                } else {
                    emit(Result.failure(Exception("Unknown error")))
                }
            } else {
                emit(Result.failure(Exception("HTTP ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun submitKyc(data: KycSubmitData): Flow<Result<Unit>> = flow {
        try {
            val response = api.submitKyc(
                KycSubmitRequest(
                    passportSeries = data.passportSeries,
                    passportNumber = data.passportNumber,
                    birthDate = data.birthDate,
                    selfieBase64 = data.selfieBase64
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    emit(Result.success(Unit))
                } else {
                    emit(Result.failure(Exception(body?.error?.message ?: "Unknown error")))
                }
            } else {
                emit(Result.failure(Exception("HTTP ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
