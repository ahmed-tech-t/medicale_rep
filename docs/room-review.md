# مراجعة طبقة Room (قاعدة البيانات)

اتراجع: كل الـ Entities، كل الـ 14 DAO، الـ Relations، الـ Repos، الـ Mappers، `SearchNormalizer`، `DatabaseModule`.
اتأكدت من الكود نفسه. معملتش build ولا شغلت تيستات.

**الرموز:**
- 🔴 مهم: لازم يتصلح.
- 🟠 متوسط: يتصلح قريب.
- 🟡 بسيط: تحسين.
- ✅ متأكد منه من الكود.
- ❓ محتاج تتأكد منه (هيبان لو فلو معين اشتغل).

---

## أولاً: أخطاء في المنطق

### 1. 🔴 ✅ الزيارة "الفايتة" (Missed) والزيارة "الملغية" (Cancelled) نفس الحالة
**المكان:** `VisitDao.markMissed`, `VisitDao.cancelVisit`, `VisitDao.missedPaged`, `StatisticsRepoImpl.progressOf`، و`ExecutionStatus` في `VisitStates.kt`.

**المشكلة:** `ExecutionStatus` فيه 3 قيم بس: `Scheduled / Completed / Cancelled`. مفيش `Missed`.
- `markMissed` بيحول الزيارات اللي فات ميعادها لـ `Cancelled`.
- `cancelVisit` (لما المندوب يلغي بنفسه) برضه `Cancelled`.
- قايمة "الفايتة" بتجيب كل `Cancelled`.

**ليه مهمة:** لو المندوب لغى زيارة بسبب، هتظهر في تاب "Missed" وتتحسب في إحصائيات "missed". ده بيشوه الأرقام اللي المشرف بيقيم بيها المندوب.

**الحل:** أضف `Missed` في `ExecutionStatus` واستخدمها في `markMissed` وفي `missedPaged` وفي `countOfRep`. بديل أسرع: فرّق بـ `cancelReason IS NULL` للفايتة، لكنه أقل وضوحاً.

**ملاحظة جانبية:** `markMissed` مبيحطش `syncStatus = 'NotSynced'` بينما `cancelVisit` بيحطه. يعني السيرفر مش هيعرف إن الزيارة فاتت. اتأكد إن ده مقصود.

---

### 2. 🔴 ✅ البحث في تابات Required و Done مش بيعمل normalize للنص
**المكان:** `VisitRepoImpl.requiredVisits / requiredCount / doneVisits / doneCount` (بيبعتوا `filter.query.trim()`), `VisitLogViewModel:97`.

**المشكلة:** عمود `searchText` متخزن بعد التطبيع: حروف صغيرة، أ/إ/آ ← ا، ى ← ي، ة ← ه، الأرقام الهندية ← عربية. باقي الـ repos بتستخدم `normalizeSearch(query)`. لكن هنا النص بيروح خام.

**ليه مهمة:** المستخدم يكتب "احمد" والحساب متخزن "أحمد" ← مفيش نتيجة. نفس الكلام لو كتب أرقام هندية. نفس البحث شغال تمام في شاشات تانية.

**المشكلة التانية في نفس المكان:** الجزء اللي بيبحث في اسم الدكتور (`d.name LIKE`, `d.specialty LIKE`) بيقارن بالاسم الخام لأن `doctors` ملهاش `searchText`. حتى لو طبّعنا النص مش هيلاقي "أحمد". الحل إنك تضيف `searchText` للدكاترة، أو تبعت النصين (مطبّع وخام) للـ SQL.

---

### 3. 🟠 ✅ علامتي `%` و `_` في البحث بتتعامل كـ wildcard
**المكان:** كل استعلامات `LIKE '%' || :query || '%'` (في كل الـ DAOs).

**المشكلة:** `normalizeSearch` مبيهربش `%` و`_`. لو كتب المستخدم `_` أو `%` بيطابق كل الصفوف.

**الحل:** استخدم `ESCAPE '\'` وهرّب الحروف دي في `normalizeSearch` (أو في دالة بجانبه).

---

### 4. 🟠 ✅ مسح الزيارة بيمسح نقط الـ GPS اللي لسه ما اترفعتش
**المكان:** `TrackPointEntity` ← `ForeignKey(VisitEntity, onDelete = CASCADE)`.

**المشكلة:** `VisitRepoImpl.delete` بيمسح الصف، والـ CASCADE بيمسح معاه كل نقط التتبع بتاعته، حتى اللي `NotSynced`.

**الحالة دلوقتي:** مفيش حد في الكود بينادي `visitRepo.delete`، فالمشكلة كامنة. بس أول ما حد يستخدمها هتبان.

**الحل:** `onDelete = SET_NULL` (العمود `visitId` أصلاً nullable). النقط تفضل لحد ما تترفع و`purgeOlderThan` يمسحها.

---

### 5. 🟠 ✅ جدول `track_points` بيكبر ومحدش بينضفه
**المكان:** `TrackPointDao.purgeOlderThan`.

**المشكلة:** الدالة موجودة في الـ DAO والـ Repo، لكن مفيش أي مكان في الكود بينادي عليها (بحثت في `app` و`core`).

**ليه مهمة:** نقطة GPS كل كام ثانية × أشهر = جدول كبير يبطّأ الخريطة ويكبّر حجم الـ DB.

**الحل:** نادِ عليها بعد كل رفع ناجح (أو من WorkManager يومي).

---

### 6. 🟠 ✅ `searchText` للحساب بيحتوي اسم المدينة والمنطقة، لكن مفيش تحديث لو اتغيروا
**المكان:** `AccountMapper:44` (`searchTextOf(name, type, city?.name, district?.name, address)`)، و`GeoRepoImpl.save` / `GeoDao.upsertDistricts`.

**المشكلة:** لو السيرفر غيّر اسم مدينة أو منطقة، الحسابات بتفضل بالنص القديم، والبحث بالاسم الجديد مش بيلاقيها.

**الحل:** إما تحدّث `accounts.searchText` بعد `upsertCities/upsertDistricts`، أو تشيل أسماء المناطق من `searchText` وتعتمد على join. (نادر الحدوث، فالأولوية متوسطة.)

---

### 7. 🟠 ❓ تحميل المنتجات من السيرفر بيكتب فوق المخزون المحلي
**المكان:** `CatalogDao.saveItems` ← `upsertInventory` (`@Upsert` على `UserInventoryEntity`).

**المشكلة:** `VisitDao.save` بينقّص `current` محلياً لأي زيارة `NotSynced` (`useStock`). لو حصل تحميل للمنتجات قبل رفع الزيارة، `current` بيرجع لقيمة السيرفر (اللي لسه ما خصمتش الزيارة). المخزون يظهر أكبر من الحقيقي لحد ما الزيارة تترفع ويتحمل تاني.

**اتأكد:** هل التحميل بيحصل قبل الرفع في الـ sync؟ لو الرفع دايماً الأول، المشكلة مش هتبان.

---

### 8. 🟡 ✅ مسح زيارة `NotSynced` مش بيرجّع المخزون
**المكان:** `VisitDao.deleteById` / `VisitRepoImpl.delete`.

**المشكلة:** `save` بيخصم الأصناف من المخزون (`useStock`). `deleteById` بيمسح `visit_items` (cascade) من غير ما يرجّع الكمية.

**الحالة دلوقتي:** كامنة، لأن مفيش حد بينادي `delete`. لو اتنادى هيضيع المخزون.

**الحل:** جوه `@Transaction`: جيب الـ lines، رجّعها بـ `useStock(item, -qty)`، وبعدين امسح.

---

### 9. 🟡 ✅ `VisitDao.upsertFromServer` بيحفظ الزيارة بس من غير الدكاترة والأصناف
**المكان:** `VisitDao.upsertFromServer(visits: List<VisitEntity>)`.

**المشكلة:** بياخد `VisitEntity` بس. الزيارة اللي تنزل من السيرفر مش هيتكتب لها صفوف في `visit_doctors` ولا `visit_items`.

**الحالة دلوقتي:** مش موصّلة بأي Repo (كود ميت). لو وصلتها هتلاقي الزيارات المحمّلة من غير دكاترة.

---

### 10. 🟡 ✅ حدود الوقت مش موحدة (`BETWEEN` مقابل `< :toMillis`)
**المكان:** `VisitDao.pagedFiltered`, `OrderDao.pagedFiltered`, `TrackPointDao.getByRepAndDay`, `TrackPointDao.deleteSyncedOfRepBetween` ← `BETWEEN` (النهاية مشمولة).
باقي الاستعلامات والإحصائيات ← `< :toMillis` (النهاية مش مشمولة).

**المشكلة:** عنصر بالظبط عند منتصف الليل التالي يظهر في يومين.

**الحل:** وحّد الكل على `>= from AND < to`. اتأكد الأول إن الشاشات مش بتبعت `نهاية اليوم - 1`.

---

### 11. 🟡 ✅ `TrackPointDao.insertOrReplace` (REPLACE) بيغيّر الـ id
**المشكلة:** فيه index فريد على `(repId, timeMillis)`. لو نقطة بنفس الاتنين اتحفظت تاني، REPLACE بيمسح القديمة ويدخل جديدة بـ id جديد. لو الـ sync كان ماسك الـ id القديم، `markSynced(oldId)` مش هيلاقي صف.
نادرة الحدوث، لكن `INSERT ... ON CONFLICT DO UPDATE` أنضف.

---

### 12. 🟡 ✅ ترتيب الملفات والداتابيز في الحفظ والمسح
**المكان:** `VisitRepoImpl.save` و`VisitRepoImpl.delete`.

- `save` بيكتب الصور والصوت قبل معاملة الداتابيز. لو الداتابيز فشلت، الملفات تفضل يتيمة. وصور الزيارة اللي اتعدلت بعدد أقل بتسيب ملفات قديمة (`image_<id>_N`) من غير مسح.
- `delete` بيمسح الملفات قبل الصف. لو مسح الصف فشل، الملفات راحت والصف فاضل.
**الحل:** في `delete` امسح الصف الأول وبعدين الملفات.

---

### 13. 🟡 ✅ أسماء الـ enum مكتوبة نصاً جوه SQL
**المكان:** كل DAO: `'Scheduled'`, `'Approved'`, `'NotSynced'`, `'Cancelled'`, `'Pending'`, `'Completed'`...

**المشكلة:** لو حد غيّر اسم قيمة في الـ enum، الاستعلام بيفضل يتكمبايل ويرجّع صفر نتايج من غير ما يطلع خطأ.

**الحل:** تيست instrumented على DB in-memory يغطي كل استعلام، أو مرّر الـ enum كباراميتر بدل النص.

---

### 14. 🟡 ✅ الداتابيز `version = 1` ومفيش Migrations
**المكان:** `AppDatabase`, `DatabaseModule`.

**المشكلة:** أول تغيير في أي Entity من غير `Migration` هيعمل crash عند تحديث التطبيق. و`fallbackToDestructiveMigration` خطر عندكم لأنه بيمسح بيانات لسه ما اترفعتش.

**الحل:** اعمل `Migration` لأي تغيير (الـ schema متصدّر في `schemas/1.json`، فتقدر تكتب `MigrationTestHelper` تيست).

---

## ثانياً: الأداء (Performance)

### 15. 🟠 ✅ جدول `visits` عليه 10 indexes، وأغلبها ملهوش لازمة
**المكان:** `VisitEntity` ← `indices`.

**المشكلة:**
- الأعمدة `type`, `approvalStatus`, `executionStatus`, `syncStatus` عندها 2-4 قيم بس. SQLite نادراً يستخدمها لوحدها.
- كل `INSERT/UPDATE` لازم يحدّث الـ 10 كلهم. جدول `visits` اللي بيتكتب فيه كتير (حفظ، رفع، قرارات الأدمن).
- الاستعلامات اللي فيها `(:x IS NULL OR col = :x)` بتمنع استخدام الـ index على العمود ده.

**الحل المقترح:** استبدل الفردية المنخفضة بـ composite:
- `(approvalStatus, executionStatus, dateTimeMillis)` ← لـ Required / startable / scheduledFrom / markMissed / statistics.
- `(executionStatus, completedAtMillis)` ← لـ Done.
- `(requestedAtMillis)` ← لقايمة الطلبات (دلوقتي مفيش index عليه).
- سيب `backendId` و`accountId` و`repId` و`dateTimeMillis`.

لو الجدول صغير (آلاف الصفوف) الفرق مش هيبان. مهم لو كبر.

---

### 16. 🟠 ✅ المزامنة بتحدّث `searchText` لكل صف حتى لو مفيش تغيير
**المكان:** `AccountDao.upsertFromServer` ← `save` ← `refreshVisitSearchText` + `refreshOrderSearchText` (UPDATE لكل حساب). `VisitDao.upsertFromServer` ← `refreshSearchText` لكل زيارة.

**المشكلة:** تحميل 5,000 حساب = 10,000 `UPDATE` بـ subqueries مترابطة، حتى لو ولا حساب اتغير.

**الحل:** قارن `searchText` القديم بالجديد وحدّث الزيارات والأوردرات بس لو اتغير. أو اعمل `UPDATE ... WHERE accountId IN (<الحسابات اللي اتغيرت>)` مرة واحدة.

---

### 17. 🟠 ✅ تحميل علاقات الزيارة بيعمل استعلامات زيادة
**المكان:** `VisitWithDetails`.

**المشكلة:** كل صفحة (20 زيارة) بتشغّل ~8 استعلامات إضافية:
- `items` (Junction على `visit_items`) و`lines` (نفس الجدول `visit_items`) = قراءتين لنفس الجدول.
- `account` ← `AccountWithPlace` بيحمّل **كل دكاترة الحساب** لكل زيارة في القايمة، وكمان `doctors` الزيارة نفسها.

**اتأكد:** هل الـ mapper والشاشات بيستخدموا `items` و`account.doctors` فعلاً؟ لو لأ، امسحهم أو اعمل Relation أخف للقوايم.

---

### 18. 🟡 ✅ `notSynced()` فيها N+1 وبعدها `markSynced` لكل صف
**المكان:** `VisitRepoImpl.notSynced`, `OrderRepoImpl.notSynced`: بيجيب IDs وبعدين `getWithDetails` لكل واحد.
**وكمان:** كل `markSynced/markFailed` استعلام لوحده، وكل واحد بيعمل invalidation لكل الـ Flows اللي بتراقب `visits`. رفع 50 زيارة = 50 إعادة تشغيل لكل القوايم والإحصائيات.

**الحل:** استعلام واحد `@Transaction` يرجّع `VisitWithDetails`. ولف دورة الرفع كلها في `withTransaction` (invalidation واحد).

---

### 19. 🟡 ✅ الترتيب بالأقرب (`NEAREST_FIRST`) بيتحسب لكل الصفوف في كل صفحة
**المكان:** `VisitDao.requiredPaged`, `scheduledFrom`.

**المشكلة:** `ORDER BY` بسبيكويري مترابط (بيجيب إحداثيات الحساب لكل صف). Paging بيعيد الاستعلام لكل صفحة (OFFSET/LIMIT)، فبيرتب كل النتيجة كل مرة.
مقبول لحد آلاف الزيارات. لو كبرت، فكر في عمود مخزن أو إحداثيات منقولة على `visits`.

---

### 20. 🟡 ✅ `LIKE '%query%'` بيعمل Full Scan
**المكان:** كل استعلامات `searchText LIKE`.

**المشكلة:** الـ `%` في الأول يمنع أي index. مقبول لحد ~10-20 ألف صف. لو كبر عن كده، FTS4/FTS5 هو الحل. (مش محتاجه دلوقتي، بلاش over-engineering.)

---

### 21. 🟡 ✅ قوايم بدون حد أقصى ومحدش بينضفها
**المكان:** `NotificationDao.ofRep` (كل الإشعارات في List واحدة)، `RepRequestDao.ofRep`.

**المشكلة:** جدول `notifications` مفيش حاجة بتمسحه، فبيكبر على طول، وكل ما يتغير بيحمّل كله في الذاكرة.

**الحل:** `LIMIT 100` أو Paging، وامسح الإشعارات المقروءة الأقدم من X يوم.

---

### 22. 🟡 ✅ استعلام `WITH_STOCK` بيحسب `sold` للـ Gifts والـ Samples بلا لازمة
**المكان:** `CatalogDao.WITH_STOCK` و`StatisticsRepoImpl.between`.

**المشكلة:** الـ Gifts والـ Samples مفيهمش مبيعات (الأوردرات للمنتجات بس)، لكن الـ subquery `SUM(order_items.quantity)` بتتحسب لكل صف. وكل تغيير في `order_items` بيعيد تشغيل الاستعلامين.
صغيرة، لكن ممكن تعمل استعلام تاني للـ custody من غير `sold`.

---

### 23. 🟡 ✅ شروط مكررة وغير لازمة
- `bookedDayRows`: الشرط `dateTimeMillis IS NOT NULL` ملهوش لازمة (العمود `Long` غير nullable).
- `replaceLines` / `replaceDoctors` / `OrderDao.save`: بيمسحوا الأول وبعدين `INSERT ... REPLACE`. الـ REPLACE مش مطلوب بعد المسح.
- `lastNumber()` (`MAX(number)`) من غير index على `number`. جدول صغير فمش بيفرق.

---

## ثالثاً: كود ميت واستعلامات مكررة

### 24. 🟠 ✅ دوال DAO مفيش أي حد في الكود الأساسي (main) بينادي عليها
اتحققت إن `data/repo` مش بينادي عليها (بعضها مستخدم في التيستات بس):

| DAO | الدوال |
|---|---|
| `VisitDao` | `getAll`, `paged`, `clear`, `pendingPaged`, `pendingFilteredCount`, `upsertFromServer` |
| `AccountDao` | `getAll`, `paged`, `clear` |
| `OrderDao` | `getAll`, `paged`, `clear`, `deleteById`, `setStatus` |
| `RepDao` | `getAll`, `searchByRole`, `clear` |
| `TrackPointDao` | `getAll`, `getById`, `search`, `deleteById`, `clear` |
| `SyncInfoDao` | `search`, `deleteById`, `clear` |
| `DoctorDao` | `getById`, `accountIdsOf`, `unlink` |
| `GeoDao` | `deleteCity` |
| `CatalogDao` | `clear` |

**الخلاصة:** حوالي 30 دالة ميتة. كل `clear()` بلا لازمة لأن `DatabaseOwner` بيستخدم `clearAllTables()`.
القاعدة عندكم (YAGNI): امسحها. اللي مستخدم في التيستات بس، انقل استخدامه لـ `insert` مباشر أو احذفه مع التيست.

---

### 25. 🟠 ✅ استعلامات متداخلة ومكررة في الـ `VisitDao`
- `pendingPaged` / `pendingFilteredCount` (ميتين) ≈ `requestsPaged(Pending)` / `requestsCount(Pending)`.
- `countPending()` = `pendingFilteredCount(null, null)`.
- `rejectedPaged` ≈ `requestsPaged(Rejected)` + الفلتر. وفي فرق خفي: `requestsPaged` فيها `requestedAtMillis IS NOT NULL` و`pendingPaged/rejectedPaged` مفيهمش. سلوكين مختلفين لنفس المفهوم.
- `startablePaged` ≈ `requiredPaged` من غير فلاتر (ترتيب مختلف بس).
- `getById` + `getWithDetails` + `getAll` + `paged` + `search`: ثلاث طرق لقراءة نفس الصفوف.

**الحل:** وحّد "الطلبات" في استعلام واحد بباراميتر `approval` + فلاتر، وامسح الباقي.

---

## رابعاً: اللي سليم ✅

- كل عمود Foreign Key عليه index.
- `@Upsert` بدل `REPLACE` في الـ DAOs، فمفيش مسح cascade غير مقصود للأبناء عند التحميل.
- `save` في الزيارات والأوردرات والحسابات والدكاترة والجغرافيا كلها `@Transaction`.
- `upsertFromServer` بيحمي الصفوف اللي لسه عليها تعديلات ما اترفعتش.
- `SQLITE_IN_LIMIT = 900` عشان `IN (...)` ما تكسرش الحد.
- `searchText` بيتجدد تلقائياً لما الحساب أو المشرف يتغير.
- ترقيم الأوردر `lastNumber() + 1` جوه المعاملة (مفيش تعارض).
- مخزون الأوردرات (`sold`) محسوب من `order_items` فبيرجع لوحده لما الأوردر يتلغي.
- `Index(repId, createdAtMillis)` على `notifications` مطابق للاستعلام.
- `schemas/1.json` متصدّر.
- `DatabaseOwner` بيمنع اختلاط بيانات مستخدمين.
- حجم الصفحة 20 مناسب.

---

## الأولويات المقترحة

1. #1 (Missed مقابل Cancelled): بيأثر على الأرقام اللي بتظهر للمستخدم.
2. #2 + #3 (البحث): المستخدم هيلاحظه بسرعة.
3. #5 (تنضيف `track_points`) + #4.
4. #24 + #25 (مسح الكود الميت): بيسهّل باقي التعديلات.
5. #15 + #16 + #17 (الأداء): لما الداتا تكبر.
