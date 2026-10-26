package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.CustodyDao
import com.example.medicalrep.data.local.dao.ProductDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.repo.ProductRepo
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(
    private val productDao: ProductDao,
    private val custodyDao: CustodyDao,
) : ProductRepo {

    override fun products(query: String) = pagedFlow({ productDao.search(normalizeSearch(query)) }) { it.toDomain() }

    override suspend fun getProduct(id: String): Product? = productDao.getById(id)?.toDomain()

    override suspend fun saveProducts(products: List<Product>) {
        productDao.upsertAll(products.map { it.toEntity() })
    }

    override suspend fun deleteProduct(id: String) {
        productDao.deleteById(id)
    }

    override fun custody(query: String, kind: ProductType?) = pagedFlow(
        source = {
            val text = normalizeSearch(query)
            if (kind == null) custodyDao.search(text) else custodyDao.searchByKind(kind, text)
        },
        toDomain = { it.toDomain() },
    )

    override suspend fun getCustody(id: String): Custody? = custodyDao.getWithPending(id)?.toDomain()

    override suspend fun saveCustody(custody: List<Custody>) {
        custodyDao.upsertAll(custody.map { it.toEntity() })
    }
}
