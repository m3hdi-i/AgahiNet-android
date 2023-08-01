package ir.m3hdi.agahinet.util

import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.domain.model.ad.Category

class Constants {
    companion object{

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

        val CATEGORIES_FOR_NEW_AD = CATEGORIES.drop(1)

        val ENTIRE_IRAN_CITY= City(0,null,"کل ایران")

    }

}