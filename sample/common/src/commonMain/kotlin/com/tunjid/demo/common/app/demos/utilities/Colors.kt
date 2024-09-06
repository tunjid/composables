package com.tunjid.demo.common.app.demos.utilities

import androidx.compose.ui.graphics.Color

data class ColorItem(
    val name: String,
    val color: Color,
    val id: Int,
)

private val colors = listOf(
    ColorItem("Baby Pink", Color(0xFFE5B8C8), 1),
    ColorItem("Lavender", Color(0xFFD5CCE6), 2),
    ColorItem("Baby Blue", Color(0xFF96C7D6), 3),
    ColorItem("Mint Green", Color(0xFF81CE94), 4),
    ColorItem("Butter Yellow", Color(0xFFF6E5B7), 5),
    ColorItem("Peach", Color(0xFFF2C6A5), 6),
    ColorItem("Lilac", Color(0xFFB592B5), 7),
    ColorItem("Seafoam Green", Color(0xFF9BD5E2), 8),
    ColorItem("Coral", Color(0xFFFF7F50), 9),
    ColorItem("Pale Turquoise", Color(0xFF96D4D4), 10),
    ColorItem("Dusty Rose", Color(0xFFA86D87), 11),
    ColorItem("Periwinkle", Color(0xFFB3B3D6), 12),
    ColorItem("Apricot", Color(0xFFEAB596), 13),
    ColorItem("Pistachio", Color(0xFFA3D95C), 14),
    ColorItem("Light Salmon", Color(0xFFFFA07A), 15),
    ColorItem("Wisteria", Color(0xFFB289BC), 16),
    ColorItem("Pale Yellow", Color(0xFFF7E1AA), 17),
    ColorItem("Celadon", Color(0xFF93C59F), 18),
    ColorItem("Blush Pink", Color(0xFFC7486C), 19),
    ColorItem("Sky Blue", Color(0xFF72B5CF), 20),
    ColorItem("Cream", Color(0xFFF2F2E0), 21),
    ColorItem("Pale Lavender", Color(0xFFC3B6E2), 22),
    ColorItem("Pale Green", Color(0xFF7BC67B), 23),
    ColorItem("Honeydew", Color(0xFFE0E0D1), 24),
    ColorItem("Light Coral", Color(0xFFE06363), 25),
    ColorItem("Aqua", Color(0xFF00CED1), 26),
    ColorItem("Mauve", Color(0xFFC698D9), 27),
    ColorItem("Canary Yellow", Color(0xFFF4E691), 28),
    ColorItem("Pearl", Color(0xFFD6CCD3), 29),
    ColorItem("Misty Rose", Color(0xFFF2D3CD), 30),
    ColorItem("Lavender Blush", Color(0xFFF2DCCD), 31),
    ColorItem("Azure", Color(0xFFE0F2F2), 32),
    ColorItem("Vanilla", Color(0xFFE6CDA3), 33),
    ColorItem("Pale Violet", Color(0xFFD16FD1), 34),
    ColorItem("Lemon Chiffon", Color(0xFFF5E0A2), 35),
    ColorItem("Seashell", Color(0xFFF2E5D9), 36),
    ColorItem("Powder Blue", Color(0xFF96C7D6), 37),
    ColorItem("Pale Goldenrod", Color(0xFFD9D292), 38),
    ColorItem("Thistle", Color(0xFFC1A5C1), 39),
    ColorItem("Alice Blue", Color(0xFFE0E8F2), 40),
    ColorItem("Flax", Color(0xFFD3BC6D), 41),
    ColorItem("Old Lace", Color(0xFFEBDDCD), 42),
    ColorItem("Light Cyan", Color(0xFFD1F2F2), 43),
    ColorItem("Ivory", Color(0xFFF2F2E0), 44),
    ColorItem("Beige", Color(0xFFE8E8C9), 45),
)

val pastelColors = (0..<9)
    .map { colors }
    .flatMapIndexed { index: Int, items: List<ColorItem> ->
        items.mapIndexed { innerIndex, item ->
            item.copy(id = (index * items.size) + innerIndex)
        }
    }

fun distinctPastelColors() = pastelColors
    .distinctBy(ColorItem::name)
    .sortedBy(ColorItem::name)
    .mapIndexed { index, colorItem -> colorItem.copy(id = index) }

fun List<ColorItem>.groupedByFirstLetter() = groupBy { it.name.first().uppercaseChar() }