package uz.gita.paynetclone.usecase.kyc

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.kyc.KycSubmitData
import javax.inject.Inject

class SubmitKycUseCase @Inject constructor(
    private val repository: KycRepository
) {
    operator fun invoke(data: KycSubmitData): Flow<Result<Unit>> {
        return repository.submitKyc(data)
    }
}
