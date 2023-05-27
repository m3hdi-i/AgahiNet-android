package ir.m3hdi.agahinet.domain.model

import ir.m3hdi.agahinet.data.local.entity.City

data class SearchFilters(var keyword: String?=null,
                         var category: Category?=null,
                         var cities: List<City>?=null,
                         var minPrice: String?=null,
                         var maxPrice: String?=null)