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
private const val MINUTE = 60_000L

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

    private fun accounts() = baseAccounts() + generatedAccounts()

    private fun baseAccounts() = listOf(
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

    private fun products() = baseProducts() + generatedProducts()

    private fun baseProducts() = listOf(
        product(1, "بانادول اكسترا", 434, 20),
        product(2, "أوميبرازول فورت 40 مجم", 120, 20),
        product(3, "كاردكس بلس 10 مجم", 88, 15),
        product(4, "أموكسيسيلين 875 مجم", 65, 10),
        product(5, "كونكور 2.5 مجم", 150, 20),
        product(6, "فلاجيل 400 مجم", 42, 5),
    )

    private val doctorNames = listOf(
        "أحمد حسن", "محمد علي", "سارة محمود", "كريم فؤاد", "منى الشاذلي", "هاني عبد الفتاح", "ياسمين سمير", "طارق عز الدين",
        "نادية رشاد", "عمر خليل", "هبة إبراهيم", "وليد مصطفى", "ريم عادل", "خالد منصور", "دينا فاروق", "إسلام جمال",
    )

    private val cities = listOf("شبين الكوم", "منوف", "قويسنا", "الباجور")
    private val districts = listOf("البر الشرقى", "البر الغربى", "وسط البلد")
    private val specialties = listOf("باطنة", "أطفال", "عظام", "جلدية", "قلب", "نساء وتوليد", "أسنان", "عيون")
    private val kinds = listOf("عيادة" to "عيادة د.", "صيدلية" to "صيدلية", "مستشفى" to "مستشفى", "مركز طبي" to "مركز")

    /** Accounts 13..50: every type, class, city and district, so lists, search and filters have plenty to show. */
    private fun generatedAccounts(): List<Account> = (13..50).map { n ->
        val (type, prefix) = kinds[n % kinds.size]
        account(
            id = n,
            name = "$prefix ${doctorNames[n % doctorNames.size]}",
            type = type,
            specialty = if (type == "صيدلية") "صيدلة" else specialties[n % specialties.size],
            classCategory = listOf("A", "B", "C")[n % 3],
            city = cities[(n * 3) % cities.size],
            district = districts[(n * 5) % districts.size],
            address = "شارع ${n * 7 % 90 + 1}",
            supervisorId = if (n % 2 == 0) "sup-1" else "sup-2",
        )
    }

    private val drugs = listOf(
        "بانادول", "أوميبرازول", "كاردكس", "أموكسيسيلين", "كونكور", "فلاجيل", "نيكسيوم", "أوجمنتين", "كتافلام", "فولتارين",
        "زيرتك", "فيتامين د", "ميوكوسولفان", "لانزور", "كونتروللوك", "جلوكوفاج", "بروفين", "سيبروفلوكساسين", "لوسك", "أنتينال",
    )
    private val strengths = listOf("5 مجم", "10 مجم", "20 مجم", "40 مجم", "100 مجم", "250 مجم", "500 مجم", "1 جم", "شراب 120 مل", "أمبول")

    /** Products 7..200: a drug and a strength each, all different. */
    private fun generatedProducts(): List<Product> = (7..200).map { n ->
        val index = n - 1
        product(n, "${drugs[index % drugs.size]} ${strengths[index / drugs.size % strengths.size]}", 20 + index * 3 % 480, index % 5 * 5)
    }

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
        completedAtMillis: Long? = null,
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
        completedAtMillis = completedAtMillis,
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

    /** 85 more visits spread over weeks: still to do, done (uploaded or not, some double) and plan requests. */
    private fun generatedVisits(now: Long, accounts: List<Account>): List<Visit> = (1..85).map { n ->
        val account = accounts[n * 5 % accounts.size]
        val shift = if (n % 2 == 0) Shift.Am else Shift.Pm
        val hours = (n % 10) * HOUR
        when (n % 5) {
            0 -> visit("v-gen-$n", account, VisitType.Planned, shift, now + (n % 14 + 1) * DAY + hours)
            1 -> doneVisit("v-gen-$n", account, shift, now - (n % 30 + 1) * DAY - hours, SyncStatus.Synced, double = false)
            2 -> doneVisit("v-gen-$n", account, shift, now - (n % 30 + 1) * DAY - hours, SyncStatus.NotSynced, double = false)
            3 -> doneVisit("v-gen-$n", account, shift, now - (n % 30 + 1) * DAY - hours, SyncStatus.Synced, double = true)
            else -> visit(
                "v-gen-$n", account, VisitType.Planning, shift, now + (n % 20 + 1) * DAY,
                syncStatus = if (n % 2 == 0) SyncStatus.NotSynced else SyncStatus.Synced,
                approvalStatus = if (n % 4 == 0) ApprovalStatus.Rejected else ApprovalStatus.Pending,
                requestedAtMillis = now - (n % 6 + 1) * DAY,
                decisionNote = if (n % 4 == 0) "تم رفض الطلب" else null,
            )
        }
    }

    private fun doneVisit(id: String, account: Account, shift: Shift, at: Long, sync: SyncStatus, double: Boolean) = visit(
        id, account, if (double) VisitType.Planned else VisitType.Unplanned, shift, at, sync, double = double,
        durationLabel = "10:00 د", completedAtMillis = at + 10 * MINUTE,
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
            visit("v-done-1", acc(1), VisitType.Unplanned, Shift.Am, now - 3 * HOUR, SyncStatus.NotSynced, double = true, durationLabel = "04:35 د", completedAtMillis = now - 3 * HOUR + 5 * MINUTE, gifts = listOf(gift), samples = listOf(sample)),
            visit("v-done-2", acc(4), VisitType.Unplanned, Shift.Pm, now - DAY, SyncStatus.NotSynced, durationLabel = "12:10 د", completedAtMillis = now - DAY + 12 * MINUTE, gifts = listOf(gift)),
            visit("v-done-3", acc(8), VisitType.Unplanned, Shift.Am, now - 2 * DAY, SyncStatus.Synced, durationLabel = "06:20 د", completedAtMillis = now - 2 * DAY + 6 * MINUTE, samples = listOf(sample)),
            visit("v-done-4", acc(6), VisitType.Planned, Shift.Pm, now - 3 * DAY, SyncStatus.Synced, double = true, durationLabel = "15:00 د", completedAtMillis = now - 3 * DAY + 15 * MINUTE),
            visit("v-done-5", acc(9), VisitType.Unplanned, Shift.Am, now - 4 * DAY, SyncStatus.Synced, durationLabel = "08:45 د", completedAtMillis = now - 4 * DAY + 9 * MINUTE, gifts = listOf(gift)),
            visit("v-done-6", acc(12), VisitType.Unplanned, Shift.Pm, now - 5 * DAY, SyncStatus.Synced, durationLabel = "05:05 د", completedAtMillis = now - 5 * DAY + 5 * MINUTE),
        )
        val requests = listOf(
            visit("v-request-1", acc(11), VisitType.Planning, Shift.Am, now + 4 * DAY, SyncStatus.NotSynced, approvalStatus = ApprovalStatus.Pending, requestedAtMillis = now - HOUR),
            visit("v-request-2", acc(3), VisitType.Planning, Shift.Pm, now + 5 * DAY, SyncStatus.Synced, approvalStatus = ApprovalStatus.Pending, requestedAtMillis = now - DAY),
            visit("v-request-3", acc(2), VisitType.Planning, Shift.Am, now + 2 * DAY, SyncStatus.Synced, approvalStatus = ApprovalStatus.Rejected, requestedAtMillis = now - 2 * DAY, decisionNote = "هذه العيادة تمت زيارتها هذا الأسبوع"),
        )
        return planned + done + requests + generatedVisits(now, accounts)
    }
}
