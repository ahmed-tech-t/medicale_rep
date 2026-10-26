package com.example.medicalrep.data.seed

import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.User
import com.example.medicalrep.domain.models.UserRole
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType

/** Everything the debug seed writes into Room. */
data class SeedData(
    val users: List<User>,
    val accounts: List<Account>,
    val products: List<Product>,
    val custody: List<Custody>,
    val visits: List<Visit>,
)

private const val HOUR = 3_600_000L
private const val DAY = 24 * HOUR

/** Builds the debug sample data relative to [nowMillis], so planned visits are always upcoming and done ones past. */
object SeedDataFactory {

    fun build(nowMillis: Long): SeedData {
        val users = users()
        val accounts = accounts()
        val custody = custody()
        return SeedData(
            users = users,
            accounts = accounts,
            products = products(),
            custody = custody,
            visits = visits(nowMillis, accounts),
        )
    }

    private fun users() = listOf(
        User("sup-1", "د. وائل نصار", "مشرف", role = UserRole.Supervisor, initials = "ون"),
        User("sup-2", "د. سارة أحمد", "مشرف", role = UserRole.Supervisor, initials = "سأ"),
        User("rep-1", "أحمد محمد", "مندوب", employeeCode = "1001", deviceId = "99201", initials = "أم", role = UserRole.Rep, supervisorId = "sup-1"),
        User("rep-2", "كريم فؤاد", "مندوب", employeeCode = "1002", deviceId = "99202", initials = "كف", role = UserRole.Rep, supervisorId = "sup-1"),
        User("rep-3", "منى الشاذلي", "مندوب", employeeCode = "1003", deviceId = "99203", initials = "مش", role = UserRole.Rep, supervisorId = "sup-2"),
    )

    private fun account(
        id: Int,
        name: String,
        type: String,
        specialty: String,
        classCategory: String,
        city: String,
        district: String,
        address: String,
        supervisorId: String,
    ) = Account(
        id = "acc-$id",
        name = name,
        entityType = type,
        classCategory = classCategory,
        specialty = specialty,
        city = city,
        district = district,
        address = address,
        latitude = 30.5589 + id * 0.004,
        longitude = 31.0112 + id * 0.003,
        supervisorId = supervisorId,
        syncStatus = SyncStatus.Synced,
        createdAt = 1_700_000_000_000L + id * DAY,
    )

    private fun accounts() = listOf(
        account(1, "عيادة د. محمود عبد الرحمن", "عيادة", "باطنة وجهاز هضمي", "A", "شبين الكوم", "البر الشرقى", "برج الأطباء - الدور الثالث", "sup-1"),
        account(2, "عيادة د. هاني المسيري", "عيادة", "باطنة", "B", "شبين الكوم", "البر الشرقى", "شارع البحر", "sup-1"),
        account(3, "عيادة د. سارة فتحي", "عيادة", "أطفال", "A", "شبين الكوم", "البر الغربى", "شارع جمال عبد الناصر", "sup-1"),
        account(4, "عيادة د. كريم عادل", "عيادة", "عظام", "C", "منوف", "وسط البلد", "شارع الجيش", "sup-2"),
        account(5, "مستشفى شبين الكوم الجامعي", "مستشفى", "قلب", "A", "شبين الكوم", "البر الغربى", "طريق الجامعة", "sup-1"),
        account(6, "مستشفى الدلتا", "مستشفى", "عام", "B", "منوف", "وسط البلد", "شارع السادات", "sup-2"),
        account(7, "صيدلية الشفاء", "صيدلية", "صيدلة", "A", "شبين الكوم", "وسط البلد", "الميدان الرئيسي", "sup-1"),
        account(8, "صيدلية النور", "صيدلية", "صيدلة", "B", "قويسنا", "وسط البلد", "شارع الجمهورية", "sup-2"),
        account(9, "صيدلية الأمل", "صيدلية", "صيدلة", "C", "منوف", "البر الشرقى", "شارع المحطة", "sup-2"),
        account(10, "مركز الأمل الطبي", "مركز طبي", "تخصصات متعددة", "B", "شبين الكوم", "البر الشرقى", "شارع البحر", "sup-1"),
        account(11, "مركز النيل الطبي", "مركز طبي", "تخصصات متعددة", "C", "قويسنا", "البر الغربى", "شارع المحطة", "sup-2"),
        account(12, "عيادة د. أميرة سعد", "عيادة", "جلدية", "B", "قويسنا", "وسط البلد", "شارع الجلاء", "sup-2"),
    )

    private fun product(id: Int, name: String, price: Int, discountPercent: Int) =
        Product(id = "prod-$id", name = name, description = "", price = price, discountPercent = discountPercent)

    private fun products() = listOf(
        product(1, "بانادول اكسترا", 434, 20),
        product(2, "أوميبرازول فورت 40 مجم", 120, 20),
        product(3, "كاردكس بلس 10 مجم", 88, 15),
        product(4, "أموكسيسيلين 500 مجم", 65, 10),
        product(5, "كونكور 5 مجم", 150, 20),
        product(6, "فلاجيل 500 مجم", 42, 5),
    )

    private fun custody() = listOf(
        Custody("gift-1", "مج حراري + قلم معدني بشعار الشركة", ProductType.Gift, received = 40, dispatched = 12),
        Custody("gift-2", "دفتر ملاحظات جلد فاخر", ProductType.Gift, received = 30, dispatched = 10),
        Custody("gift-3", "حقيبة قماش بشعار الشركة", ProductType.Gift, received = 25, dispatched = 5),
        Custody("sample-1", "GastroCure 40mg (عينة)", ProductType.Product, received = 60, dispatched = 22),
        Custody("sample-2", "Pediamol Syrup (عينة)", ProductType.Product, received = 40, dispatched = 18),
        Custody("sample-3", "Cardex Plus 10mg (عينة)", ProductType.Product, received = 30, dispatched = 6),
    )

    private fun line(id: String, kind: ProductType, name: String, quantity: Int) =
        Product(id = id, name = name, quantity = quantity, kind = kind)

    private fun visit(
        id: String,
        account: Account,
        type: VisitType,
        shift: Shift,
        dateTimeMillis: Long,
        syncStatus: SyncStatus = SyncStatus.Synced,
        double: Boolean = false,
        durationLabel: String = "",
        approvalStatus: ApprovalStatus? = null,
        requestedAtMillis: Long? = null,
        decisionNote: String? = null,
        gifts: List<Product> = emptyList(),
        samples: List<Product> = emptyList(),
    ) = Visit(
        id = id,
        accountId = account.id,
        repId = "rep-1",
        supervisorId = if (double) account.supervisorId else null,
        type = type,
        shift = shift,
        visitMode = if (double) VisitMode.Double else VisitMode.Single,
        dateTimeMillis = dateTimeMillis,
        syncStatus = syncStatus,
        durationLabel = durationLabel,
        approvalStatus = approvalStatus,
        requestedAtMillis = requestedAtMillis,
        decisionNote = decisionNote,
        visitLatitude = account.latitude,
        visitLongitude = account.longitude,
        accountLatitude = account.latitude,
        accountLongitude = account.longitude,
        travelSeconds = 600,
        gifts = gifts,
        products = samples,
    )

    private fun visits(now: Long, accounts: List<Account>): List<Visit> {
        fun acc(n: Int) = accounts[n - 1]
        val gift = line("gift-1", ProductType.Gift, "مج حراري + قلم معدني بشعار الشركة", 1)
        val sample = line("sample-1", ProductType.Product, "GastroCure 40mg (عينة)", 2)
        val planned = listOf(
            visit("v-planned-1", acc(1), VisitType.Planned, Shift.Am, now + 2 * HOUR),
            visit("v-planned-2", acc(2), VisitType.Planned, Shift.Pm, now + 6 * HOUR),
            visit("v-planned-3", acc(5), VisitType.Planned, Shift.Am, now + DAY),
            visit("v-planned-4", acc(7), VisitType.Planned, Shift.Pm, now + DAY + 5 * HOUR),
            visit("v-planned-5", acc(10), VisitType.Planned, Shift.Am, now + 2 * DAY),
            visit("v-planned-6", acc(3), VisitType.Planned, Shift.Pm, now + 3 * DAY),
        )
        val done = listOf(
            visit("v-done-1", acc(1), VisitType.Unplanned, Shift.Am, now - 3 * HOUR, SyncStatus.NotSynced, double = true, durationLabel = "04:35 د", gifts = listOf(gift), samples = listOf(sample)),
            visit("v-done-2", acc(4), VisitType.Unplanned, Shift.Pm, now - DAY, SyncStatus.NotSynced, durationLabel = "12:10 د", gifts = listOf(gift)),
            visit("v-done-3", acc(8), VisitType.Unplanned, Shift.Am, now - 2 * DAY, SyncStatus.Synced, durationLabel = "06:20 د", samples = listOf(sample)),
            visit("v-done-4", acc(6), VisitType.Unplanned, Shift.Pm, now - 3 * DAY, SyncStatus.Synced, double = true, durationLabel = "15:00 د"),
            visit("v-done-5", acc(9), VisitType.Unplanned, Shift.Am, now - 4 * DAY, SyncStatus.Synced, durationLabel = "08:45 د", gifts = listOf(gift)),
            visit("v-done-6", acc(12), VisitType.Unplanned, Shift.Pm, now - 5 * DAY, SyncStatus.Synced, durationLabel = "05:05 د"),
        )
        val requests = listOf(
            visit("v-request-1", acc(11), VisitType.Planning, Shift.Am, now + 4 * DAY, SyncStatus.NotSynced, approvalStatus = ApprovalStatus.Pending, requestedAtMillis = now - HOUR),
            visit("v-request-2", acc(3), VisitType.Planning, Shift.Pm, now + 5 * DAY, SyncStatus.Synced, approvalStatus = ApprovalStatus.Pending, requestedAtMillis = now - DAY),
            visit("v-request-3", acc(2), VisitType.Planning, Shift.Am, now + 2 * DAY, SyncStatus.Synced, approvalStatus = ApprovalStatus.Rejected, requestedAtMillis = now - 2 * DAY, decisionNote = "هذه العيادة تمت زيارتها هذا الأسبوع"),
        )
        return planned + done + requests
    }
}
