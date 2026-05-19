package uz.gita.paynetclone.usecase.payment

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.payment.Provider
import javax.inject.Inject

class GetProvidersUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(category: String? = null): Flow<Result<List<Provider>>> {
        return repository.getProviders(category)
    }
}
