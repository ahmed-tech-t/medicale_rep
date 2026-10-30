package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import kotlinx.coroutines.flow.Flow

/** Items for sale, and the samples and gifts the rep carries (custody). Saving also sets the rep's stock of them. */
interface ProductRepo {
    fun products(query: String = ""): Flow<PagingData<Product>>
    suspend fun getProduct(id: String): Product?
    suspend fun saveProducts(repId: String, products: List<Product>)
    suspend fun deleteProduct(id: String)

    /** Samples or gifts ([kind]); `remaining` is what is left after the visits. */
    fun custody(kind: ProductType, query: String = ""): Flow<PagingData<Custody>>
    /** All custody of one kind (for pickers). */
    fun custodyList(kind: ProductType): Flow<List<Custody>>

    suspend fun getCustody(id: String): Custody?
    suspend fun saveCustody(repId: String, custody: List<Custody>)
}
