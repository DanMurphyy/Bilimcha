package com.danmurphyy.bilimcha.numbers

object NumbersRepository {
    val numbers = listOf(
        Number(0, "Zero", "Ноль", "sounds/en/zero.mp3", "sounds/ru/0.mp3"),
        Number(1, "One", "Один", "sounds/en/one.mp3", "sounds/ru/1.mp3"),
        Number(2, "Two", "Два", "sounds/en/two.mp3", "sounds/ru/2.mp3"),
        Number(3, "Three", "Три", "sounds/en/three.mp3", "sounds/ru/3.mp3"),
        Number(4, "Four", "Четыре", "sounds/en/four.mp3", "sounds/ru/4.mp3"),
        Number(5, "Five", "Пять", "sounds/en/five.mp3", "sounds/ru/5.mp3"),
        Number(6, "Six", "Шесть", "sounds/en/six.mp3", "sounds/ru/6.mp3"),
        Number(7, "Seven", "Семь", "sounds/en/seven.mp3", "sounds/ru/7.mp3"),
        Number(8, "Eight", "Восемь", "sounds/en/eight.mp3", "sounds/ru/8.mp3"),
        Number(
            9,
            "Nine",
            "Девять",
            "sounds/en/nine.mp3",
            "sounds/ru/9.mp3",
        ),
        Number(
            10,
            "Ten",
            "Десять",
            "sounds/en/ten.mp3",
            "sounds/ru/10.mp3",
        ),
        Number(
            11,
            "Eleven",
            "Одиннадцать",
            "sounds/en/eleven.mp3",
            "sounds/ru/11.mp3",
        ),
        Number(
            12,
            "Twelve",
            "Двенадцать",
            "sounds/en/twelve.mp3",
            "sounds/ru/12.mp3",
        ),
        Number(
            13,
            "Thirteen",
            "Тринадцать",
            "sounds/en/thirteen.mp3",
            "sounds/ru/13.mp3",
        ),
        Number(
            14,
            "Fourteen",
            "Четырнадцать",
            "sounds/en/fourteen.mp3",
            "sounds/ru/14.mp3",
        ),
        Number(
            15,
            "Fifteen",
            "Пятнадцать",
            "sounds/en/fifteen.mp3",
            "sounds/ru/15.mp3",
        ),
        Number(
            16,
            "Sixteen",
            "Шестьнадцать",
            "sounds/en/sixteen.mp3",
            "sounds/ru/16.mp3",
        ),
        Number(
            17,
            "Seventeen",
            "Семнадцать",
            "sounds/en/seventeen.mp3",
            "sounds/ru/17.mp3",
        ),
        Number(
            18,
            "Eighteen",
            "Восемнадцать",
            "sounds/en/eighteen.mp3",
            "sounds/ru/18.mp3",
        ),
        Number(
            19,
            "Nineteen",
            "Девятнадцать",
            "sounds/en/nineteen.mp3",
            "sounds/ru/19.mp3",
        ),
        Number(
            20,
            "Twenty",
            "Двадцать",
            "sounds/en/twenty.mp3",
            "sounds/ru/20.mp3",
        )
    )

    fun getNumber(value: Int): Number? = numbers.find { it.value == value }
}
