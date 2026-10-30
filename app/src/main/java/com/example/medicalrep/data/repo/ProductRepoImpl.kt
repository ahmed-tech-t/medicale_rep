package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.CatalogDao
import com.example.medicalrep.data.local.mapper.toCustody
import com.example.medicalrep.data.local.mapper.toInventory
import com.example.medicalrep.data.local.mapper.toItem
import com.example.medicalrep.data.local.mapper.toProduct
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.repo.ProductRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(private val dao: CatalogDao) : ProductRepo {

    override fun products(query: String) =
        pagedFlow({ dao.search(ProductType.Product, normalizeSearch(query)) }) { it.toProduct() }

    override suspend fun getProduct(id: String): Product? =
        dao.getById(id)?.takeIf { it.item.type == ProductType.Product }?.toProduct()

    override suspend fun saveProducts(repId: String, products: List<Product>) {
        dao.saveItems(products.map { it.toItem() }, products.map { it.toInventory(repId) })
    }

    override suspend fun deleteProduct(id: String) {
        dao.deleteById(id)
    }

    override fun custody(kind: ProductType, query: String) =
        pagedFlow({ dao.search(kind, normalizeSearch(query)) }) { it.toCustody() }

    override fun custodyList(kind: ProductType): Flow<List<Custody>> =
        dao.ofType(kind).map { list -> list.map { it.toCustody() } }

    override suspend fun getCustody(id: String): Custody? =
        dao.getById(id)?.takeIf { it.item.type != ProductType.Product }?.toCustody()

    override suspend fun saveCustody(repId: String, custody: List<Custody>) {
        dao.saveItems(custody.map { it.toItem() }, custody.map { it.toInventory(repId) })
    }
}
