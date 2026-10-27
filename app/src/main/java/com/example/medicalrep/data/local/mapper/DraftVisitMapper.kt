package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.DraftVisitEntity
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val draftJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }

@Serializable
private data class DraftVisitJson(
    val id: String? = null,
    val accountId: String? = null,
    val type: String = "Unplanned",
    val city: String = "",
    val region: String = "",
    val address: String = "",
    val account: String = "",
    val placeName: String = "",
    val specialty: String = "",
    val classCategory: String = "",
    val supervisorName: String = "",
    val shift: String = "Am",
    val isDouble: Boolean = false,
    val noIssueFound: Boolean = true,
    val imageNote: String? = null,
    val voiceNotePath: String? = null,
    val visitLatitude: Double = 0.0,
    val visitLongitude: Double = 0.0,
    val accountLatitude: Double = 0.0,
    val accountLongitude: Double = 0.0,
    val travelSeconds: Int = 0,
    val gifts: List<DraftLineJson> = emptyList(),
    val products: List<DraftLineJson> = emptyList(),
)

@Serializable
private data class DraftLineJson(
    val id: String,
    val name: String = "",
    val quantity: Int = 0,
    val kind: String = "Gift",
    val remaining: Int = 0,
    val description: String = "",
    val price: Int = 0,
    val discountPercent: Int = 0,
)

/** [voiceNotePath] is where the repo wrote the visit's voice note file (null when there is none). */
fun DraftVisit.toEntity(voiceNotePath: String?): DraftVisitEntity {
    val v = visit
    val form = DraftVisitJson(
        id = v.id,
        accountId = v.accountId,
        type = when (v.type) {
            VisitType.Planning -> "Planning"
            VisitType.Planned -> "Planned"
            else -> "Unplanned"
        },
        city = v.city.orEmpty(),
        region = v.region.orEmpty(),
        address = v.address.orEmpty(),
        account = v.account.orEmpty(),
        placeName = v.placeName.orEmpty(),
        specialty = v.specialty.orEmpty(),
        classCategory = v.classCategory.orEmpty(),
        supervisorName = v.supervisorName.orEmpty(),
        shift = (v.shift ?: Shift.Am).name,
        isDouble = v.visitMode == VisitMode.Double,
        noIssueFound = v.noIssueFound,
        imageNote = v.imageNote,
        voiceNotePath = voiceNotePath,
        visitLatitude = v.visitLatitude,
        visitLongitude = v.visitLongitude,
        accountLatitude = v.accountLatitude,
        accountLongitude = v.accountLongitude,
        travelSeconds = v.travelSeconds,
        gifts = v.gifts.orEmpty().map { it.toLine() },
        products = v.products.orEmpty().map { it.toLine() },
    )
    return DraftVisitEntity(step = step, json = draftJson.encodeToString(form))
}

/** [readVoice] loads the voice note file by its path. */
fun DraftVisitEntity.toDomain(readVoice: (String) -> ByteArray?): DraftVisit {
    val form = draftJson.decodeFromString<DraftVisitJson>(json)
    return DraftVisit(
        step = step,
        visit = Visit(
            id = form.id,
            accountId = form.accountId,
            type = when (form.type) {
                "Planning" -> VisitType.Planning
                "Planned" -> VisitType.Planned
                else -> VisitType.Unplanned
            },
            city = form.city,
            region = form.region,
            address = form.address,
            account = form.account,
            placeName = form.placeName,
            specialty = form.specialty,
            classCategory = form.classCategory,
            supervisorName = form.supervisorName,
            shift = Shift.entries.firstOrNull { it.name == form.shift } ?: Shift.Am,
            visitMode = if (form.isDouble) VisitMode.Double else VisitMode.Single,
            noIssueFound = form.noIssueFound,
            imageNote = form.imageNote,
            voiceNote = form.voiceNotePath?.let(readVoice),
            voiceNotePath = form.voiceNotePath,
            visitLatitude = form.visitLatitude,
            visitLongitude = form.visitLongitude,
            accountLatitude = form.accountLatitude,
            accountLongitude = form.accountLongitude,
            travelSeconds = form.travelSeconds,
            gifts = form.gifts.map { it.toProduct() },
            products = form.products.map { it.toProduct() },
        ),
    )
}

/** The voice note file path kept inside the JSON, so the file can be deleted with the draft. */
fun DraftVisitEntity.voiceNotePath(): String? = draftJson.decodeFromString<DraftVisitJson>(json).voiceNotePath

private fun Product.toLine() = DraftLineJson(
    id = id.orEmpty(),
    name = name.orEmpty(),
    quantity = quantity,
    kind = (kind ?: ProductType.Gift).name,
    remaining = remaining,
    description = description.orEmpty(),
    price = price,
    discountPercent = discountPercent,
)

private fun DraftLineJson.toProduct() = Product(
    id = id,
    name = name,
    quantity = quantity,
    kind = ProductType.entries.firstOrNull { it.name == kind } ?: ProductType.Gift,
    description = description,
    remaining = remaining,
    price = price,
    discountPercent = discountPercent,
)
