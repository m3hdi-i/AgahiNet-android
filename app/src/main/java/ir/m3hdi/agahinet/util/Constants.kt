package ir.m3hdi.agahinet.util

import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.model.Category

class Constants {
    companion object{

        const val PAGE_SIZE=10

        val CATEGORIES = listOf(
            Category(0,"کل دسته ها", R.drawable.ic_category),
            Category(1,"املاک", R.drawable.ic_category),
            Category(2,"وسایل نقلیه", R.drawable.ic_category),
            Category(3,"کالای دیجیتال", R.drawable.ic_category),
            Category(4,"ملزومات خانه و آشپزخانه", R.drawable.ic_category),
            Category(5,"خدمات", R.drawable.ic_category),
            Category(6,"وسایل شخصی", R.drawable.ic_category),
            Category(7,"سرگرمی و فراغت", R.drawable.ic_category),
            Category(8,"تجهیزات و صنعتی", R.drawable.ic_category),
            Category(9, "استخدام و کاریابی", R.drawable.ic_category),
            Category(10,"اجتماعی", R.drawable.ic_category),
            Category(11,"سایر", R.drawable.ic_category),
        )

        val PROVINCES = listOf(
            "آذربایجان شرقی",
            "آذربایجان غربی",
            "اردبیل",
            "اصفهان",
            "البرز",
            "ایلام",
            "بوشهر",
            "تهران",
            "چهارمحال و بختیاری",
            "خراسان جنوبی",
            "خراسان رضوی",
            "خراسان شمالی",
            "خوزستان",
            "زنجان",
            "سیستان و بلوچستان",
            "سمنان",
            "سمنگان",
            "فارس",
            "قزوین",
            "قم",
            "کردستان",
            "کرمان",
            "کرمانشاه",
            "کهکیلویه و بویراحمد",
            "گلستان",
            "گیلان",
            "لرستان",
            "مازندران",
            "مرکزی",
            "هرمزگان",
            "همدان",
            "یزد"
        )

    }

}