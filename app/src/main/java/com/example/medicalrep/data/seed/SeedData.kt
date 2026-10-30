package com.example.medicalrep.data.seed

import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.District
import com.example.medicalrep.domain.models.Doctor
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.OrderItem
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.TrackPoint
import com.example.medicalrep.domain.models.Rep
import com.example.medicalrep.domain.models.RepRole
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.domain.models.WorkingTime
import kotlin.math.cos
import kotlin.math.sin

/** A doctor and the accounts the doctor works at. */
data class DoctorPlacement(val doctor: Doctor, val accountIds: List<String>)

/** Everything the debug seed writes into Room. */
data class SeedData(
    val cities: List<BasicModel>,
    val districts: List<District>,
    val reps: List<Rep>,
    val accounts: List<Account>,
    val doctors: List<DoctorPlacement>,
    val products: List<Product>,
    val custody: List<Custody>,
    val visits: List<Visit>,
    val orders: List<Order>,
    val trackPoints: List<TrackPoint>,
)

private const val HOUR = 3_600_000L
private const val DAY = 24 * HOUR
private const val MINUTE = 60_000L
private const val TRACK_DAYS = 5
private const val TRACK_STEP = 90 * MINUTE
private const val VISIT_LENGTH = 15 * MINUTE
private const val ROAD_POINTS = 14
private const val WOBBLE = 0.012

/** Builds the debug sample data relative to [nowMillis], so planned visits are always upcoming and done ones past. */
object SeedDataFactory {

    fun build(nowMillis: Long): SeedData {
        val accounts = accounts()
        val custody = custody()
        val products = products()
        val reps = reps()
        val visits = visits(nowMillis, accounts) + trackVisits(nowMillis, reps, accounts)
        return SeedData(
            cities = cities,
            districts = districts,
            reps = reps,
            accounts = accounts,
            doctors = doctors() + generatedDoctors(),
            products = products,
            custody = custody,
            visits = visits,
            orders = orders(nowMillis, accounts, products),
            trackPoints = trackPoints(visits, accounts),
        )
    }

    /** Three orders of the first rep, numbered 999 to 1001, so the Orders screen has something to show. */
    private fun orders(nowMillis: Long, accounts: List<Account>, products: List<Product>): List<Order> {
        fun line(productIndex: Int, quantity: Int) = products[productIndex].let {
            OrderItem(requireNotNull(it.id), requireNotNull(it.name), it.price, it.discountPercent, quantity)
        }
        fun order(number: Int, account: Account, ageMillis: Long, status: SyncStatus, vararg lines: OrderItem) = Order(
            id = "order-$number",
            number = number,
            repId = "rep-1",
            accountId = account.id,
            createdAtMillis = nowMillis - ageMillis,
            syncStatus = status,
            items = lines.toList(),
        )
        return listOf(
            order(999, accounts[2], 4 * DAY, SyncStatus.Synced, line(2, 5), line(3, 4)),
            order(1000, accounts[1], 2 * DAY, SyncStatus.NotSynced, line(1, 3), line(4, 2), line(5, 6)),
            order(1001, accounts[0], 2 * HOUR, SyncStatus.NotSynced, line(0, 4)),
        )
    }

    private fun reps() = listOf(
        Rep("sup-1", "د. وائل نصار", role = RepRole.Supervisor),
        Rep("sup-2", "د. سارة أحمد", role = RepRole.Supervisor),
        Rep("rep-1", "أحمد محمد", employeeCode = "1001", deviceId = "99201", role = RepRole.Rep, supervisorId = "sup-1"),
        Rep("rep-2", "كريم فؤاد", employeeCode = "1002", deviceId = "99202", role = RepRole.Rep, supervisorId = "sup-1"),
        Rep("rep-3", "منى الشاذلي", employeeCode = "1003", deviceId = "99203", role = RepRole.Rep, supervisorId = "sup-2"),
    )

    private fun option(text: String) = BasicModel(text, text)

    private fun account(
        id: Int,
        name: String,
        type: String,
        city: String,
        district: String,
        address: String,
        supervisorId: String,
    ) = Account(
        id = "acc-$id",
        name = name,
        accountType = option(type),
        city = cityModel(city),
        district = districtModel(city, district),
        workingTime = when (id % 4) {
            0 -> WorkingTime.Am
            1 -> WorkingTime.Pm
            else -> WorkingTime.Both
        },
        address = address,
        latitude = 30.5589 + id * 0.004,
        longitude = 31.0112 + id * 0.003,
        supervisorId = supervisorId,
        syncStatus = SyncStatus.Synced,
        createdAt = 1_700_000_000_000L + id * DAY,
    )

    private fun accounts() = baseAccounts() + generatedAccounts()

    private fun doctor(id: Int, name: String, classCategory: String, specialty: String) = Doctor(
        id = "doc-$id",
        name = name,
        classCategory = option(classCategory),
        specialty = option(specialty),
        syncStatus = SyncStatus.Synced,
    )

    /** Doctors of the hospitals, clinics and medical centers; some work at two places, so the link is many-to-many. */
    private fun doctors() = listOf(
        DoctorPlacement(doctor(1, "د. ياسمين فاروق", "B", "قلب"), listOf("acc-5")),
        DoctorPlacement(doctor(2, "د. طارق عز الدين", "A", "باطنة"), listOf("acc-5", "acc-6")),
        DoctorPlacement(doctor(3, "د. منى الشاذلي", "C", "أطفال"), listOf("acc-6")),
        DoctorPlacement(doctor(4, "د. أحمد سمير", "B", "عظام"), listOf("acc-10")),
        DoctorPlacement(doctor(5, "د. هالة مصطفى", "A", "جلدية"), listOf("acc-10", "acc-11")),
        DoctorPlacement(doctor(6, "د. محمود عبد الرحمن", "A", "باطنة وجهاز هضمي"), listOf("acc-1")),
        DoctorPlacement(doctor(7, "د. هاني المسيري", "B", "باطنة"), listOf("acc-2", "acc-10")),
        DoctorPlacement(doctor(8, "د. سارة فتحي", "A", "أطفال"), listOf("acc-3")),
        DoctorPlacement(doctor(9, "د. كريم عادل", "C", "عظام"), listOf("acc-4")),
        DoctorPlacement(doctor(10, "د. أميرة سعد", "B", "جلدية"), listOf("acc-12")),
    )

    private fun baseAccounts() = listOf(
        account(1, "عيادة د. محمود عبد الرحمن", "عيادة", "شبين الكوم", "البر الشرقى", "برج الأطباء - الدور الثالث", "sup-1"),
        account(2, "عيادة د. هاني المسيري", "عيادة", "شبين الكوم", "البر الشرقى", "شارع البحر", "sup-1"),
        account(3, "عيادة د. سارة فتحي", "عيادة", "شبين الكوم", "البر الغربى", "شارع جمال عبد الناصر", "sup-1"),
        account(4, "عيادة د. كريم عادل", "عيادة", "منوف", "وسط البلد", "شارع الجيش", "sup-2"),
        account(5, "مستشفى شبين الكوم الجامعي", "مستشفى", "شبين الكوم", "البر الغربى", "طريق الجامعة", "sup-1"),
        account(6, "مستشفى الدلتا", "مستشفى", "منوف", "وسط البلد", "شارع السادات", "sup-2"),
        account(7, "صيدلية الشفاء", "صيدلية", "شبين الكوم", "وسط البلد", "الميدان الرئيسي", "sup-1"),
        account(8, "صيدلية النور", "صيدلية", "قويسنا", "وسط البلد", "شارع الجمهورية", "sup-2"),
        account(9, "صيدلية الأمل", "صيدلية", "منوف", "البر الشرقى", "شارع المحطة", "sup-2"),
        account(10, "مركز الأمل الطبي", "مركز طبي", "شبين الكوم", "البر الشرقى", "شارع البحر", "sup-1"),
        account(11, "مركز النيل الطبي", "مركز طبي", "قويسنا", "البر الغربى", "شارع المحطة", "sup-2"),
        account(12, "عيادة د. أميرة سعد", "عيادة", "قويسنا", "وسط البلد", "شارع الجلاء", "sup-2"),
    )

    private fun product(id: Int, name: String, price: Int, discountPercent: Int, received: Int) =
        Product(id = "prod-$id", name = name, description = "", price = price, discountPercent = discountPercent, received = received)

    private fun products() = baseProducts() + generatedProducts()

    private fun baseProducts() = listOf(
        product(1, "بانادول اكسترا", 434, 20, 300),
        product(2, "أوميبرازول فورت 40 مجم", 120, 20, 150),
        product(3, "كاردكس بلس 10 مجم", 88, 15, 120),
        product(4, "أموكسيسيلين 875 مجم", 65, 10, 100),
        product(5, "كونكور 2.5 مجم", 150, 20, 200),
        product(6, "فلاجيل 400 مجم", 42, 5, 80),
    )

    private val doctorNames = listOf(
        "أحمد حسن", "محمد علي", "سارة محمود", "كريم فؤاد", "منى الشاذلي", "هاني عبد الفتاح", "ياسمين سمير", "طارق عز الدين",
        "نادية رشاد", "عمر خليل", "هبة إبراهيم", "وليد مصطفى", "ريم عادل", "خالد منصور", "دينا فاروق", "إسلام جمال",
    )

    private val cityNames = listOf("شبين الكوم", "منوف", "قويسنا", "الباجور")
    private val districtNames = listOf("البر الشرقى", "البر الغربى", "وسط البلد")
    private val cities = cityNames.mapIndexed { index, name -> BasicModel("city-${index + 1}", name) }
    private val districts = cities.flatMap { city ->
        districtNames.mapIndexed { index, name -> District("${city.id}-district-${index + 1}", name, city.id) }
    }

    private fun cityModel(name: String) = cities.first { it.name == name }

    private fun districtModel(cityName: String, name: String) =
        districts.first { it.cityId == cityModel(cityName).id && it.name == name }.let { BasicModel(it.id, it.name) }
    private val specialties = listOf("باطنة", "أطفال", "عظام", "جلدية", "قلب", "نساء وتوليد", "أسنان", "عيون")
    private val kinds = listOf("عيادة" to "عيادة د.", "صيدلية" to "صيدلية", "مستشفى" to "مستشفى", "مركز طبي" to "مركز")

    /**
     * Accounts 13..50. The combinations of city, district and type that the first twelve do not cover come first, so
     * choosing any city, district and type in the visit form finds at least one account.
     */
    private data class GeneratedPlace(val number: Int, val city: String, val district: String, val type: String)

    private fun generatedPlaces(): List<GeneratedPlace> {
        val covered = baseAccounts().map { Triple(it.city?.name.orEmpty(), it.district?.name.orEmpty(), it.accountType?.name.orEmpty()) }.toSet()
        val all = cityNames.flatMap { city -> districtNames.flatMap { district -> kinds.map { (type, _) -> Triple(city, district, type) } } }
        val combos = ((all - covered) + all).take(38)
        return combos.mapIndexed { index, (city, district, type) -> GeneratedPlace(13 + index, city, district, type) }
    }

    private fun generatedAccounts(): List<Account> = generatedPlaces().map { place ->
        val n = place.number
        account(
            id = n,
            name = "${kinds.first { it.first == place.type }.second} ${doctorNames[n % doctorNames.size]}",
            type = place.type,
            city = place.city,
            district = place.district,
            address = "شارع ${n * 7 % 90 + 1}",
            supervisorId = if (n % 2 == 0) "sup-1" else "sup-2",
        )
    }

    /** One doctor for every generated place that is not a pharmacy. */
    private fun generatedDoctors(): List<DoctorPlacement> = generatedPlaces().filter { it.type != "صيدلية" }.map { place ->
        val n = place.number
        DoctorPlacement(
            doctor(100 + n, "د. ${doctorNames[n % doctorNames.size]}", listOf("A", "B", "C")[n % 3], specialties[n % specialties.size]),
            listOf("acc-$n"),
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
        product(n, "${drugs[index % drugs.size]} ${strengths[index / drugs.size % strengths.size]}", 20 + index * 3 % 480, index % 5 * 5, received = 50 + index % 8 * 25)
    }

    private fun custody() = listOf(
        Custody("gift-1", "مج حراري + قلم معدني بشعار الشركة", ProductType.Gift, received = 40, remaining = 28),
        Custody("gift-2", "دفتر ملاحظات جلد فاخر", ProductType.Gift, received = 30, remaining = 20),
        Custody("gift-3", "حقيبة قماش بشعار الشركة", ProductType.Gift, received = 25, remaining = 20),
        Custody("sample-1", "GastroCure 40mg (عينة)", ProductType.Sample, received = 60, remaining = 38),
        Custody("sample-2", "Pediamol Syrup (عينة)", ProductType.Sample, received = 40, remaining = 22),
        Custody("sample-3", "Cardex Plus 10mg (عينة)", ProductType.Sample, received = 30, remaining = 24),
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
        repId: String = "rep-1",
        durationLabel: String = "",
        completedAtMillis: Long? = null,
        status: VisitStatus = VisitStatus.Scheduled,
        requestedAtMillis: Long? = null,
        rejectionReason: String? = null,
        gifts: List<Product> = emptyList(),
        samples: List<Product> = emptyList(),
    ) = Visit(
        id = id,
        accountId = account.id,
        repId = repId,
        supervisorId = if (double) account.supervisorId else null,
        type = type,
        shift = shift,
        visitMode = if (double) VisitMode.Double else VisitMode.Single,
        dateTimeMillis = dateTimeMillis,
        syncStatus = syncStatus,
        durationLabel = durationLabel,
        completedAtMillis = completedAtMillis,
        status = status,
        requestedAtMillis = requestedAtMillis,
        rejectionReason = rejectionReason,
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
            // Most are still to come; every fourth of them was scheduled for a day that has passed.
            0 -> if (n % 4 != 1) visit("v-gen-$n", account, VisitType.Planned, shift, now + (n % 14 + 1) * DAY + hours)
            else visit("v-gen-$n", account, VisitType.Planned, shift, now - (n % 14 + 1) * DAY - hours, status = VisitStatus.Missed)
            1 -> doneVisit("v-gen-$n", account, shift, now - (n % 30 + 1) * DAY - hours, SyncStatus.Synced, double = false)
            2 -> doneVisit("v-gen-$n", account, shift, now - (n % 30 + 1) * DAY - hours, SyncStatus.NotSynced, double = false)
            3 -> doneVisit("v-gen-$n", account, shift, now - (n % 30 + 1) * DAY - hours, SyncStatus.Synced, double = true)
            else -> visit(
                "v-gen-$n", account, VisitType.Planning, shift, now + (n % 20 + 1) * DAY,
                syncStatus = if (n % 2 == 0) SyncStatus.NotSynced else SyncStatus.Synced,
                status = if (n % 4 == 0) VisitStatus.Rejected else VisitStatus.PendingApproval,
                requestedAtMillis = now - (n % 6 + 1) * DAY,
                rejectionReason = if (n % 4 == 0) "The daily visit limit of this line was exceeded." else null,
            )
        }
    }

    private fun doneVisit(id: String, account: Account, shift: Shift, at: Long, sync: SyncStatus, double: Boolean) = visit(
        id, account, if (double) VisitType.Planned else VisitType.Unplanned, shift, at, sync, double = double,
        durationLabel = "10:00 د", completedAtMillis = at + 10 * MINUTE, status = VisitStatus.Completed,
    )

    /**
     * The last five days (today included) of every rep: three or four visits done one after the other, each 90 minutes
     * after the one before, ending before [now]. Ids are `v-track-{rep}-{days ago}-{n}`.
     */
    private fun trackVisits(now: Long, reps: List<Rep>, accounts: List<Account>): List<Visit> {
        val gift = line("gift-1", ProductType.Gift, "مج حراري + قلم معدني بشعار الشركة", 1)
        val sample = line("sample-1", ProductType.Sample, "GastroCure 40mg (عينة)", 2)
        return reps.filter { it.role == RepRole.Rep }.flatMapIndexed { repIndex, rep ->
            (0 until TRACK_DAYS).flatMap { day ->
                val count = 3 + (repIndex + day) % 2
                (0 until count).map { n ->
                    val start = now - day * DAY - (count - n) * TRACK_STEP
                    visit(
                        id = "v-track-${rep.id}-$day-$n",
                        account = accounts[(repIndex * 15 + day * 3 + n * 2) % accounts.size],
                        type = if (n % 2 == 0) VisitType.Planned else VisitType.Unplanned,
                        shift = if (n < count / 2) Shift.Am else Shift.Pm,
                        dateTimeMillis = start,
                        repId = rep.id,
                        durationLabel = "15:00 د",
                        completedAtMillis = start + VISIT_LENGTH,
                        status = VisitStatus.Completed,
                        gifts = if (n % 2 == 0) listOf(gift) else emptyList(),
                        samples = if (n % 3 == 0) listOf(sample) else emptyList(),
                    )
                }
            }
        }
    }

    /**
     * The GPS track of each rep-day of [trackVisits]: four points while leaving, three at the place of each visit (with its
     * id), and a point every five minutes on the way to the next one, a little off the straight line.
     */
    private fun trackPoints(visits: List<Visit>, accounts: List<Account>): List<TrackPoint> {
        val places = accounts.associateBy { it.id }
        fun Visit.place() = places.getValue(requireNotNull(accountId))
        return visits.filter { requireNotNull(it.id).startsWith("v-track-") }
            .groupBy { requireNotNull(it.id).substringBeforeLast('-') }
            .values
            .flatMap { day ->
                val stops = day.sortedBy { it.dateTimeMillis }
                val first = stops.first().place()
                // The rep leaves from a point about two kilometres away from the first place.
                val leaving = (1..4).map { i ->
                    point(stops.first(), first.latitude - 0.02, first.longitude - 0.015, first.latitude, first.longitude, i / 5.0, stops.first().dateTimeMillis!! - (5 - i) * 5 * MINUTE, visitId = null)
                }
                val atPlaces = stops.flatMap { visit ->
                    val place = visit.place()
                    listOf(0L, 7 * MINUTE, VISIT_LENGTH).map { offset ->
                        point(visit, place.latitude, place.longitude, place.latitude, place.longitude, 1.0, visit.dateTimeMillis!! + offset, visitId = visit.id)
                    }
                }
                val onTheWay = stops.zipWithNext().flatMap { (from, to) ->
                    val start = from.place()
                    val end = to.place()
                    (1..ROAD_POINTS).map { i ->
                        val share = i / (ROAD_POINTS + 1.0)
                        val time = from.completedAtMillis!! + i * 5 * MINUTE
                        // A small wobble, so the track is not a straight line.
                        val wobble = share * (1 - share)
                        point(from, start.latitude, start.longitude, end.latitude, end.longitude, share, time, visitId = null, wobble = wobble)
                    }
                }
                // After the last visit the rep drives off, ending about two kilometres from it.
                val last = stops.last()
                val lastPlace = last.place()
                val returning = (1..4).map { i ->
                    point(last, lastPlace.latitude, lastPlace.longitude, lastPlace.latitude + 0.02, lastPlace.longitude + 0.015, i / 4.0, last.completedAtMillis!! + i * 5 * MINUTE, visitId = null)
                }
                leaving + atPlaces + onTheWay + returning
            }
    }

    /** A point [share] of the way from ([fromLatitude], [fromLongitude]) to ([toLatitude], [toLongitude]). */
    private fun point(
        visit: Visit,
        fromLatitude: Double,
        fromLongitude: Double,
        toLatitude: Double,
        toLongitude: Double,
        share: Double,
        timeMillis: Long,
        visitId: String?,
        wobble: Double = 0.0,
    ) = TrackPoint(
        repId = requireNotNull(visit.repId),
        visitId = visitId,
        latitude = fromLatitude + (toLatitude - fromLatitude) * share + wobble * WOBBLE * sin(timeMillis / MINUTE.toDouble()),
        longitude = fromLongitude + (toLongitude - fromLongitude) * share + wobble * WOBBLE * cos(timeMillis / MINUTE.toDouble()),
        timeMillis = timeMillis,
        syncStatus = SyncStatus.Synced,
    )

    private fun visits(now: Long, accounts: List<Account>): List<Visit> {
        fun acc(n: Int) = accounts[n - 1]
        val gift = line("gift-1", ProductType.Gift, "مج حراري + قلم معدني بشعار الشركة", 1)
        val sample = line("sample-1", ProductType.Sample, "GastroCure 40mg (عينة)", 2)
        val planned = listOf(
            visit("v-planned-1", acc(1), VisitType.Planned, Shift.Am, now + 2 * HOUR),
            visit("v-planned-2", acc(2), VisitType.Planned, Shift.Pm, now + 6 * HOUR),
            visit("v-planned-3", acc(5), VisitType.Planned, Shift.Am, now + DAY),
            visit("v-planned-4", acc(7), VisitType.Planned, Shift.Pm, now + DAY + 5 * HOUR),
            visit("v-planned-5", acc(10), VisitType.Planned, Shift.Am, now + 2 * DAY),
            visit("v-planned-6", acc(3), VisitType.Planned, Shift.Pm, now + 3 * DAY),
        )
        val done = listOf(
            visit("v-done-1", acc(1), VisitType.Unplanned, Shift.Am, now - 3 * HOUR, SyncStatus.NotSynced, double = true, durationLabel = "04:35 د", completedAtMillis = now - 3 * HOUR + 5 * MINUTE, status = VisitStatus.Completed, gifts = listOf(gift), samples = listOf(sample)),
            visit("v-done-2", acc(4), VisitType.Unplanned, Shift.Pm, now - DAY, SyncStatus.Failed, durationLabel = "12:10 د", completedAtMillis = now - DAY + 12 * MINUTE, status = VisitStatus.Completed, gifts = listOf(gift)),
            visit("v-done-3", acc(8), VisitType.Unplanned, Shift.Am, now - 2 * DAY, SyncStatus.Synced, durationLabel = "06:20 د", completedAtMillis = now - 2 * DAY + 6 * MINUTE, status = VisitStatus.Completed, samples = listOf(sample)),
            visit("v-done-4", acc(6), VisitType.Planned, Shift.Pm, now - 3 * DAY, SyncStatus.Synced, double = true, durationLabel = "15:00 د", completedAtMillis = now - 3 * DAY + 15 * MINUTE, status = VisitStatus.Completed),
            visit("v-done-5", acc(9), VisitType.Unplanned, Shift.Am, now - 4 * DAY, SyncStatus.Synced, durationLabel = "08:45 د", completedAtMillis = now - 4 * DAY + 9 * MINUTE, status = VisitStatus.Completed, gifts = listOf(gift)),
            visit("v-done-6", acc(12), VisitType.Unplanned, Shift.Pm, now - 5 * DAY, SyncStatus.Synced, durationLabel = "05:05 د", completedAtMillis = now - 5 * DAY + 5 * MINUTE, status = VisitStatus.Completed),
        )
        val requests = listOf(
            visit("v-request-1", acc(11), VisitType.Planning, Shift.Am, now + 4 * DAY, SyncStatus.NotSynced, status = VisitStatus.PendingApproval, requestedAtMillis = now - HOUR),
            visit("v-request-2", acc(3), VisitType.Planning, Shift.Pm, now + 5 * DAY, SyncStatus.Synced, status = VisitStatus.PendingApproval, requestedAtMillis = now - DAY),
            visit("v-request-3", acc(2), VisitType.Planning, Shift.Am, now + 2 * DAY, SyncStatus.Synced, status = VisitStatus.Rejected, requestedAtMillis = now - 2 * DAY, rejectionReason = "The daily visit limit of this line was exceeded."),
            visit("v-request-4", acc(4), VisitType.Planned, Shift.Am, now + DAY, SyncStatus.Synced, status = VisitStatus.Scheduled, requestedAtMillis = now - 3 * DAY),
        )
        return planned + done + requests + generatedVisits(now, accounts)
    }
}
