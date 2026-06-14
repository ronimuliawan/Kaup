package app.kaup.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val KaupShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp), // None
    small = RoundedCornerShape(4.dp), // Secondary buttons, chips
    medium = RoundedCornerShape(8.dp), // Primary buttons
    large = RoundedCornerShape(12.dp), // Cards
    extraLarge = RoundedCornerShape(16.dp) // Shells, overlays
)
