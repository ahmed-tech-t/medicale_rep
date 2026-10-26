package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import kotlinx.coroutines.flow.Flow

/** Items for sale, and the samples and gifts the rep carries (custody). */
interface ProductRepo {
    fun products(query: String = ""): Flow<PagingData<Product>>
    suspend fun getProduct(id: String): Product?
    suspend fun saveProducts(products: List<Product>)
    suspend fun deleteProduct(id: String)

    /** [kind] null means both samples and gifts. `remaining` already subtracts what visits not uploaded yet gave. */
    fun custody(query: String = "", kind: ProductType? = null): Flow<PagingData<Custody>>
    suspend fun getCustody(id: String): Custody?
    suspend fun saveCustody(custody: List<Custody>)
}
