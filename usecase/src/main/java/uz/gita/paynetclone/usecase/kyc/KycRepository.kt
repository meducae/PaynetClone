package uz.gita.paynetclone.usecase.kyc

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.kyc.KycStatus
import uz.gita.paynetclone.entity.kyc.KycSubmitData

interface KycRepository {
    fun getKycStatus(): Flow<Result<KycStatus>>
    fun submitKyc(data: KycSubmitData): Flow<Result<Unit>>
}
