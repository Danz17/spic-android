package com.henrikherzig.playintegritychecker.ui.theme

import androidx.compose.ui.graphics.Color

// Legacy colors (kept for compatibility during migration)
val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

// Material 3 Color System - Security/Trust Theme

// Primary - Deep Blue (Security, Trust)
val md_theme_light_primary = Color(0xFF1565C0)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFD4E3FF)
val md_theme_light_onPrimaryContainer = Color(0xFF001C3A)

val md_theme_dark_primary = Color(0xFFA8C8FF)
val md_theme_dark_onPrimary = Color(0xFF00315E)
val md_theme_dark_primaryContainer = Color(0xFF004785)
val md_theme_dark_onPrimaryContainer = Color(0xFFD4E3FF)

// Secondary - Teal (Verification, Success)
val md_theme_light_secondary = Color(0xFF00897B)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFB2DFDB)
val md_theme_light_onSecondaryContainer = Color(0xFF002020)

val md_theme_dark_secondary = Color(0xFF4DB6AC)
val md_theme_dark_onSecondary = Color(0xFF003735)
val md_theme_dark_secondaryContainer = Color(0xFF00504D)
val md_theme_dark_onSecondaryContainer = Color(0xFFB2DFDB)

// Tertiary - Amber (Warnings, Attention)
val md_theme_light_tertiary = Color(0xFFFFA000)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFE0B2)
val md_theme_light_onTertiaryContainer = Color(0xFF2D1600)

val md_theme_dark_tertiary = Color(0xFFFFCC80)
val md_theme_dark_onTertiary = Color(0xFF4A2800)
val md_theme_dark_tertiaryContainer = Color(0xFF6A3C00)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFE0B2)

// Error - Red (Failures, Critical Issues)
val md_theme_light_error = Color(0xFFD32F2F)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

// Background & Surface
val md_theme_light_background = Color(0xFFFDFBFF)
val md_theme_light_onBackground = Color(0xFF1A1C1E)
val md_theme_light_surface = Color(0xFFFDFBFF)
val md_theme_light_onSurface = Color(0xFF1A1C1E)
val md_theme_light_surfaceVariant = Color(0xFFE0E2EC)
val md_theme_light_onSurfaceVariant = Color(0xFF44474E)

val md_theme_dark_background = Color(0xFF1A1C1E)
val md_theme_dark_onBackground = Color(0xFFE3E2E6)
val md_theme_dark_surface = Color(0xFF1A1C1E)
val md_theme_dark_onSurface = Color(0xFFE3E2E6)
val md_theme_dark_surfaceVariant = Color(0xFF44474E)
val md_theme_dark_onSurfaceVariant = Color(0xFFC4C6CF)

// Outline
val md_theme_light_outline = Color(0xFF74777F)
val md_theme_light_outlineVariant = Color(0xFFC4C6CF)

val md_theme_dark_outline = Color(0xFF8E9099)
val md_theme_dark_outlineVariant = Color(0xFF44474E)

// Inverse
val md_theme_light_inverseSurface = Color(0xFF2F3033)
val md_theme_light_inverseOnSurface = Color(0xFFF1F0F4)
val md_theme_light_inversePrimary = Color(0xFFA8C8FF)

val md_theme_dark_inverseSurface = Color(0xFFE3E2E6)
val md_theme_dark_inverseOnSurface = Color(0xFF2F3033)
val md_theme_dark_inversePrimary = Color(0xFF1565C0)

// Surface Tint
val md_theme_light_surfaceTint = Color(0xFF1565C0)
val md_theme_dark_surfaceTint = Color(0xFFA8C8FF)

// Scrim
val md_theme_light_scrim = Color(0xFF000000)
val md_theme_dark_scrim = Color(0xFF000000)

// Integrity Status Colors (for verdict display)
object IntegrityColors {
  // Strong Integrity - Green
  val strong = Color(0xFF2E7D32)
  val strongContainer = Color(0xFFC8E6C9)
  val onStrong = Color(0xFFFFFFFF)
  val onStrongContainer = Color(0xFF1B5E20)

  // Device Integrity - Blue
  val device = Color(0xFF1976D2)
  val deviceContainer = Color(0xFFBBDEFB)
  val onDevice = Color(0xFFFFFFFF)
  val onDeviceContainer = Color(0xFF0D47A1)

  // Basic Integrity - Amber
  val basic = Color(0xFFF57C00)
  val basicContainer = Color(0xFFFFE0B2)
  val onBasic = Color(0xFFFFFFFF)
  val onBasicContainer = Color(0xFFE65100)

  // No Integrity - Red
  val none = Color(0xFFD32F2F)
  val noneContainer = Color(0xFFFFCDD2)
  val onNone = Color(0xFFFFFFFF)
  val onNoneContainer = Color(0xFFB71C1C)

  // Virtual Integrity - Purple
  val virtual = Color(0xFF7B1FA2)
  val virtualContainer = Color(0xFFE1BEE7)
  val onVirtual = Color(0xFFFFFFFF)
  val onVirtualContainer = Color(0xFF4A148C)
}
