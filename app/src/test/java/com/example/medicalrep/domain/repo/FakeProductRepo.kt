package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory products and custody for ViewModel tests; it records what the screens asked for. */
class FakeProductRepo(
    products: List<Product> = emptyList(),
    custody: List<Custody> = emptyList(),
) : ProductRepo {
    val productList = MutableStateFlow(products)
    val custodyList = MutableStateFlow(custody)

    /** The search text of every `products` call, in order. */
    val requestedQueries = mutableListOf<String>()

    /** The kind of every `custody` call, in order (null = both). */
    val requestedKinds = mutableListOf<ProductType?>()

    override fun products(query: String): Flow<PagingData<Product>> {
        requestedQueries += query
        return productList.map { list -> PagingData.from(list.filter { it.name.orEmpty().contains(query, ignoreCase = true) }) }
    }

    override suspend fun getProduct(id: String): Product? = productList.value.firstOrNull { it.id == id }

    override suspend fun saveProducts(products: List<Product>) {
        productList.value = products
    }

    override suspend fun deleteProduct(id: String) {
        productList.value = productList.value.filter { it.id != id }
    }

    override fun custody(query: String, kind: ProductType?): Flow<PagingData<Custody>> {
        requestedKinds += kind
        return custodyList.map { list ->
            PagingData.from(list.filter { (kind == null || it.kind == kind) && it.name.contains(query, ignoreCase = true) })
        }
    }

    override fun custodyList(kind: ProductType): Flow<List<Custody>> = custodyList.map { list -> list.filter { it.kind == kind } }

    override suspend fun getCustody(id: String): Custody? = custodyList.value.firstOrNull { it.id == id }

    override suspend fun saveCustody(custody: List<Custody>) {
        custodyList.value = custody
    }
}
