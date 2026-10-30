package com.example.medicalrep.ui.screens.planvisit.utils

import com.example.medicalrep.domain.models.Rep
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.models.DoctorUi
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.preview.VisitSamples
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitState

/** Fake data until the API exists; also used by previews and tests. Matches Figma 33:6597. */
object PlanVisitFakes {

    private fun gift(id: String, name: String, quantity: Int, remaining: Int) = ProductUi(
        id = id, name = name, quantity = quantity, kind = ProductType.Gift, remaining = remaining, description = "",
    )

    private fun product(id: String, name: String, quantity: Int, remaining: Int) = ProductUi(
        id = id, name = name, quantity = quantity, kind = ProductType.Sample, remaining = remaining, description = "",
    )

    val giftCatalog: List<ProductUi> = listOf(
        gift("gift-mug", "مج حراري + قلم معدني بشعار الشركة", quantity = 1, remaining = 5),
        gift("gift-notebook", "دفتر ملاحظات جلد فاخر", quantity = 1, remaining = 12),
    )

    val productCatalog: List<ProductUi> = listOf(
        product("product-gastro", "GastroCure 40mg (Sample)", quantity = 1, remaining = 20),
        product("product-pediamol", "Pediamol Syrup (Sample)", quantity = 1, remaining = 10),
    )

    private val doctors = listOf(
        DoctorUi("d1", "Dr. Yasmin Farouk", BasicModelUi("B", "B")),
        DoctorUi("d2", "Dr. Tarek Ezz El-Din"),
    )

    private fun option(name: String) = BasicModelUi(name, name)

    fun state(): PlanVisitState = PlanVisitState(
        visit = VisitSamples.unplanned(
            id = "unplanned-draft",
            placeName = "عيادة د. محمود عبد الرحمن",
            account = "عيادة خاصة",
            specialty = "باطنة وجهاز هضمي",
        ).toUi().copy(
            type = VisitType.Unplanned,
            date = "17/10/2026",
            time = "10:30 ص",
            city = option("شبين الكوم"),
            region = option("البر الشرقى"),
            address = "برج الأطباء",
            classCategory = option("A"),
            visitLatitude = 30.0444,
            visitLongitude = 31.2357,
            accountLatitude = 30.1444,
            accountLongitude = 31.2357,
            shift = Shift.Am,
            isDoubleVisit = true,
            supervisor = BasicModelUi("sup-1", "د. وائل نصار"),
            doctors = doctors,
            gifts = listOf(giftCatalog[0].copy(quantity = 1)),
            products = listOf(productCatalog[0].copy(quantity = 2), productCatalog[1].copy(quantity = 1)),
        ),
        giftCatalog = giftCatalog,
        productCatalog = productCatalog,
        cityOptions = listOf("شبين الكوم", "منوف", "قويسنا").map(::option),
        districtOptions = listOf("البر الشرقى", "البر الغربى", "وسط البلد").map(::option),
        entityTypeOptions = listOf("عيادة خاصة", "مستشفى", "صيدلية").map(::option),
        placeOptions = listOf("عيادة د. محمود عبد الرحمن", "عيادة د. هاني المسيري", "مستشفى الجامعة").map(::option),
        doctorOptions = doctors,
        supervisors = listOf(Rep("sup-1", "د. وائل نصار"), Rep("sup-2", "د. سارة أحمد")),
    )
}
