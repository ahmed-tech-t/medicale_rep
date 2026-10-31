# Room database ERD

Source: `data/local/entity/*`. All dates are epoch-millis `Long`. Enums are stored as text.
Arrows: parent ||--o{ child. Delete rule shown in the label (CASCADE removes children, NULL = FK set to null).

```mermaid
erDiagram
    reps {
        string id PK "المعرف"
        string role "الدور (مندوب / مشرف)"
        string supervisorId FK "المشرف المسؤول عنه"
        string displayName "الاسم الظاهر"
        string employeeCode "كود الموظف"
        string deviceId "معرف الجهاز"
        string searchText "نص مساعد للبحث"
    }
    cities {
        string id PK "المعرف"
        string name "اسم المدينة"
    }
    districts {
        string id PK "المعرف"
        string cityId FK "المدينة التابعة لها"
        string name "اسم الحي / المنطقة"
    }
    accounts {
        string id PK "المعرف"
        string backendId "معرف السيرفر"
        string supervisorId FK "المشرف المسؤول"
        string districtId FK "المنطقة"
        string name "اسم الحساب (عيادة / صيدلية)"
        string entityType "نوع الجهة"
        string address "العنوان"
        double latitude "خط العرض"
        double longitude "خط الطول"
        string syncStatus "حالة المزامنة"
        string signPhoto "صورة اللافتة"
        long createdAt "وقت الإنشاء"
        string accountType "نوع الحساب"
        string workingTime "وقت العمل (صباحي / مسائي / الاثنين)"
        string searchText "نص مساعد للبحث"
    }
    doctors {
        string id PK "المعرف"
        string backendId "معرف السيرفر"
        string name "اسم الدكتور"
        string classCategory "تصنيف الدكتور (A / B / C)"
        string specialty "التخصص"
        string syncStatus "حالة المزامنة"
    }
    account_doctors {
        string accountId PK,FK "الحساب"
        string doctorId PK,FK "الدكتور"
    }
    visits {
        string id PK "المعرف"
        string backendId "معرف السيرفر"
        string repId "المندوب صاحب الزيارة"
        string accompanyingRepId FK "المشرف المرافق"
        string accountId FK "الحساب المزار"
        long dateTimeMillis "موعد الزيارة (0 = غير معروف)"
        string shift "الفترة (صباحي / مسائي)"
        string type "النوع (Planning / Planned / Unplanned)"
        string visitMode "فردية أو مزدوجة"
        double visitLatitude "خط عرض موقع المندوب وقت الزيارة"
        double visitLongitude "خط طول موقع المندوب وقت الزيارة"
        int travelSeconds "مدة المشوار بالثواني"
        string imageNotes "مسارات صور الملاحظات"
        string voiceNotePath "مسار الملاحظة الصوتية"
        string syncStatus "حالة المزامنة"
        string durationLabel "نص مدة الزيارة"
        string approvalStatus "رد الأدمن (Pending / Approved / Rejected)"
        string executionStatus "حالة التنفيذ"
        string cancelReason "سبب الإلغاء"
        string rejectionReason "سبب رفض الأدمن للطلب"
        string textNote "ملاحظة نصية"
        string requestId "الطلب الذي نتجت عنه الزيارة"
        long requestedAtMillis "وقت إرسال طلب التخطيط (null = زيارة عادية)"
        long decidedAtMillis "وقت رد الأدمن"
        long completedAtMillis "وقت إنهاء الزيارة (null = لم تنته)"
        string searchText "نص مساعد للبحث"
    }
    visit_doctors {
        string visitId PK,FK "الزيارة"
        string doctorId PK,FK "الدكتور الذي تمت زيارته"
    }
    visit_items {
        string visitId PK,FK "الزيارة"
        string itemId PK "الصنف (عينة / هدية)"
        int quantity "الكمية"
    }
    track_points {
        long id PK "معرف تلقائي"
        string backendId "معرف السيرفر"
        string repId "المندوب"
        string visitId FK "الزيارة المرتبطة"
        double latitude "خط العرض"
        double longitude "خط الطول"
        long timeMillis "وقت تسجيل النقطة"
        string syncStatus "حالة المزامنة"
    }
    catalog_items {
        string id PK "المعرف"
        string name "اسم الصنف"
        string description "الوصف"
        string type "نوع المنتج"
        int price "السعر (null = بدون سعر)"
        int discountPercent "نسبة الخصم"
        string searchText "نص مساعد للبحث"
    }
    orders {
        string id PK "المعرف"
        string backendId "معرف السيرفر"
        string repId "المندوب"
        string accountId FK "الحساب"
        string doctorId FK "الدكتور (اختياري)"
        long createdAtMillis "وقت إنشاء الأوردر"
        string syncStatus "حالة المزامنة"
        string status "حالة الأوردر"
        double totalAmount "الإجمالي"
        int number "رقم الأوردر"
        string searchText "نص مساعد للبحث"
    }
    order_items {
        string orderId PK,FK "الأوردر"
        string productId PK "المنتج"
        string productName "اسم المنتج وقت الطلب"
        int unitPrice "سعر الوحدة"
        int discountPercent "نسبة الخصم"
        int quantity "الكمية"
    }
    user_districts {
        string repId PK,FK "المندوب"
        string districtId PK,FK "المنطقة المسندة له"
    }
    user_inventory {
        string repId PK,FK "المندوب"
        string itemId PK,FK "الصنف"
        int original "الكمية الأصلية المستلمة"
        int current "الكمية الحالية المتبقية"
    }
    rep_requests {
        string id PK "المعرف"
        string repId FK "المندوب"
        long dateMillis "تاريخ الطلب (إجازة / عمل مكتبي)"
        string category "تصنيف الطلب"
        string subType "النوع الفرعي"
        string shift "الفترة"
        string status "حالة الطلب"
        string notes "ملاحظات"
        long createdAtMillis "وقت الإنشاء"
        long decidedAtMillis "وقت رد الأدمن"
    }
    notifications {
        string id PK "المعرف"
        string repId FK "المندوب المستلم"
        string title "العنوان"
        string message "نص الإشعار"
        string type "نوع الإشعار"
        boolean isRead "تمت قراءته؟"
        long createdAtMillis "وقت الإشعار"
    }
    visit_draft {
        int id PK "ثابت = 1 (مسودة واحدة فقط)"
        int step "الخطوة الحالية في الفورم"
        string json "بيانات المسودة JSON"
    }
    sync_info {
        string key PK "اسم الجدول / الجزء المتزامن"
        long lastUpdatedMillis "آخر وقت تحديث"
    }
    system_settings {
        string key PK "اسم الإعداد"
        string value "قيمة الإعداد"
    }

    reps ||--o{ reps : "supervisor (NULL)"
    reps ||--o{ accounts : "supervisor (NULL)"
    reps ||--o{ visits : "accompanying (NULL)"
    reps ||--o{ user_districts : "CASCADE"
    reps ||--o{ user_inventory : "CASCADE"
    reps ||--o{ rep_requests : "CASCADE"
    reps ||--o{ notifications : "CASCADE"
    cities ||--o{ districts : "CASCADE"
    districts ||--o{ accounts : "NULL"
    districts ||--o{ user_districts : "CASCADE"
    accounts ||--o{ account_doctors : "CASCADE"
    doctors ||--o{ account_doctors : "CASCADE"
    accounts ||--o{ visits : "NULL"
    accounts ||--o{ orders : "NULL"
    doctors ||--o{ orders : "NULL"
    visits ||--o{ visit_doctors : "CASCADE"
    doctors ||--o{ visit_doctors : "CASCADE"
    visits ||--o{ visit_items : "CASCADE"
    visits ||--o{ track_points : "CASCADE"
    orders ||--o{ order_items : "CASCADE"
    catalog_items ||--o{ user_inventory : "CASCADE"
```

## Notes
- A planning visit request is a `visits` row with `type = Planning` and `requestedAtMillis` not null.
- `visits.repId`, `track_points.repId`, `orders.repId`, `visits.requestId`, `visit_items.itemId` and `order_items.productId` are plain columns with no foreign key.
- `visit_draft`, `sync_info` and `system_settings` stand alone.
