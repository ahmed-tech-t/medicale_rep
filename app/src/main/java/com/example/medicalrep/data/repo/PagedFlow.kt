package com.example.medicalrep.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val PAGE_SIZE = 20

/** Every list in the app loads [PAGE_SIZE] rows at a time; entities are mapped to domain models here. */
fun <E : Any, T : Any> pagedFlow(source: () -> PagingSource<Int, E>, toDomain: (E) -> T): Flow<PagingData<T>> =
    Pager(PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE, enablePlaceholders = false), pagingSourceFactory = source)
        .flow
        .map { page -> page.map(toDomain) }
