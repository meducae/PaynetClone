package uz.gita.paynetclone.usecase.kyc

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.kyc.KycStatus
import javax.inject.Inject

class GetKycStatusUseCase @Inject constructor(
    private val repository: KycRepository
) {
    operator fun invoke(): Flow<Result<KycStatus>> {
        return repository.getKycStatus()
    }
}
