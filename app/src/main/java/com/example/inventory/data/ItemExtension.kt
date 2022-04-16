package com.example.inventory.data

import java.text.NumberFormat

fun Item.getFormattedPrice(): String =
    NumberFormat.getCurrencyInstance().format(price)