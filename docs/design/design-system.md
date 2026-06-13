# Kaup Design System

Visual design language, component guidelines, and adaptive layout rules for Kaup.

---

## Table of Contents

- [Philosophy](#philosophy)
- [Color Palette](#color-palette)
- [Typography](#typography)
- [Shape System](#shape-system)
- [Spacing & Layout](#spacing-layout)
- [Dark Mode](#dark-mode)
- [Navigation](#navigation)
- [Adaptive Layouts](#adaptive-layouts)
- [Key Screens](#key-screens)
- [Accessibility](#accessibility)
- [Components](#components)

---

## Philosophy

Kaup is built for store owners who need to complete transactions quickly,
often in bright sunlight, with thumb-sized taps, sometimes while wearing
gloves. The design must be:

- **Dense but not cluttered** — maximise information per screen without
  overwhelming the user
- **High contrast** — legible in sunlight, WCAG AA compliant
- **Thumb-friendly** — primary actions at bottom 40% of screen
- **Fast to scan** — clear visual hierarchy, no decorative noise
- **Offline-first in spirit** — no loading states that block the UI;
  show data immediately, sync in background

---

## Color Palette

### Primary (Material 3 Default)

| Role | Light | Dark |
|---|---|---|
| Primary | `#6750A4` | `#D0BCFF` |
| On Primary | `#FFFFFF` | `#000000` |
| Primary Container | `#EADDFF` | `#4F378B` |
| On Primary Container | `#21005D` | `#EADDFF` |

### Secondary (Neutral Accent)

| Role | Light | Dark |
|---|---|---|
| Secondary | `#625B71` | `#CCC2DC` |
| On Secondary | `#FFFFFF` | `#000000` |

### Error (Red)

| Role | Light | Dark |
|---|---|---|
| Error | `#B3261E` | `#F2B8B5` |
| On Error | `#FFFFFF` | `#000000` |

### Background (Neutral)

| Role | Light | Dark |
|---|---|---|
| Background | `#FFFBFE` | `#1C1B1F` |
| On Background | `#1C1B1F` | `#E6E1E5` |
| Surface | `#FFFBFE` | `#1C1B1F` |
| On Surface | `#1C1B1F` | `#E6E1E5` |

### Success (Green) — Custom for POS

| Role | Light | Dark |
|---|---|---|
| Success | `#2E7D32` | `#81C784` |
| On Success | `#FFFFFF` | `#000000` |

### Warning (Orange) — Custom for Low Stock

| Role | Light | Dark |
|---|---|---|
| Warning | `#F57C00` | `#FFB74D` |
| On Warning | `#FFFFFF` | `#000000` |

---

## Typography

### Font Family

- **Primary**: Google Sans (for headers, buttons)
- **Body**: Roboto (for all text)
- **Numbers**: Roboto Mono (for totals, prices, quantities)

### Sizes

| Role | Light | Dark |
|---|---|---|
| Display Large | 57sp / 44px | 57sp / 44px |
| Display Medium | 45sp / 36px | 45sp / 36px |
| Display Small | 36sp / 28px | 36sp / 28px |
| Headline Large | 32sp / 28px | 32sp / 28px |
| Headline Medium | 28sp / 24px | 28sp / 24px |
| Headline Small | 24sp / 20px | 24sp / 20px |
| Title Large | 22sp / 20px | 22sp / 20px |
| Title Medium | 16sp / 16px | 16sp / 16px |
| Title Small | 14sp / 14px | 14sp / 14px |
| Body Large | 16sp / 15px | 16sp / 15px |
| Body Medium | 14sp / 14px | 14sp / 14px |
| Body Small | 12sp / 12px | 12sp / 12px |
| Label Large | 14sp / 14px | 14sp / 14px |
| Label Medium | 12sp / 12px | 12sp / 12px |
| Label Small | 11sp / 11px | 11sp / 11px |

### Line Height

- Headers: 1.2× size
- Body: 1.5× size
- Labels: 1.33× size

---

## Shape System

### Corner Radius

| Size | Radius |
|---|---|
| None | 0dp |
| Small | 4dp |
| Medium | 8dp |
| Large | 12dp |
| Extra Large | 16dp |
| Circle | 50% |

### Button Shapes

- Primary buttons: Medium (8dp)
- Secondary buttons: Small (4dp)
- Cards: Large (12dp)
- Chips: Small (4dp)

---

## Spacing & Layout

### Grid

- Base unit: 4dp
- Common spacing: 4dp, 8dp, 12dp, 16dp, 24dp, 32dp

### Margins

| Screen Part | Phone | Tablet |
|---|---|---|
| Horizontal margin | 16dp | 24dp |
| Top margin | 16dp | 24dp |
| Bottom margin | 16dp | 24dp |
| Between cards | 12dp | 16dp |
| Between list items | 8dp | 12dp |

### Touch Targets

- Minimum: 48×48dp (Material 3)
- Primary actions: 56×56dp (POS buttons)
- Numpad keys: 64×64dp (large thumbs)

---

## Dark Mode

- Toggle in Settings → Display → Dark Mode
- Default: Follow system setting
- Contrast: WCAG AA compliant in both modes

### Dark Mode Rules

- Background: `#1C1B1F`
- Surface: `#1C1B1F` (same as background)
- Text: `#E6E1E5` (on dark)
- Primary: `#D0BCFF` (lighter purple for dark)
- Cards: Slightly lighter surface `#2D2A30`

---

## Navigation

### Phone (Compact Width < 600dp)

```
┌─────────────────────────────┐
│ Top App Bar                 │
│ [Title]              [...] │
├─────────────────────────────┤
│                             │
│   Screen Content            │
│                             │
│                             │
├─────────────────────────────┤
│ Navigation Bar (Bottom)     │
│ [POS] [Inventory] [...]    │
└─────────────────────────────┘
```

- **Navigation Bar**: 3–5 items, icons + labels
- **Top App Bar**: Title on left, overflow menu on right
- **Primary actions**: Bottom 40% of screen

### Tablet (Expanded Width ≥ 840dp)

```
┌─────────────────────────────────────────────┐
│ Navigation Rail (Left)                      │
│ ┌───┐                                      │
│ │POS│  ┌───────────────────────────────┐  │
│ ├───┤  │                               │  │
│ │Inv│  │   Screen Content              │  │
│ ├───┤  │                               │  │
│ │...│  │                               │  │
│ └───┘  │                               │  │
│        │                               │  │
│        └───────────────────────────────┘  │
└─────────────────────────────────────────────┘
```

- **Navigation Rail**: Left side, icons + labels, auto-hides on small tablet
- **Two-pane layout**: ListDetailPaneScaffold for inventory, customers, reports
- **Primary actions**: Right side or bottom of content pane

---

## Adaptive Layouts

### Window Size Classes

| Class | Width | Device |
|---|---|---|
| Compact | < 600dp | Phone portrait |
| Medium | 600–840dp | Phone landscape, small tablet |
| Expanded | 840–1200dp | Tablet, large foldable |
| Large | 1200–1600dp | Large tablet, ChromeOS |
| Extra Large | > 1600dp | Desktop windowing |

### Layout Rules Per Screen

| Screen | Compact | Expanded |
|---|---|---|
| POS Register | Full-screen cart, numpad at bottom | Cart left pane, numpad + item grid right pane |
| Inventory | List → navigate to detail | ListDetailPaneScaffold (side by side) |
| Customers | List → navigate to detail | ListDetailPaneScaffold |
| Reports | Scrollable cards | Charts and table side by side |
| Settings | Single column | Two-column form |
| Lock Screen | Full-screen grid | Larger grid, 3 columns |

---

## Key Screens

### POS Register

**Compact (Phone):**
```
┌─────────────────────────────┐
│ Top App Bar                 │
│ [Store Name]      [Shift]  │
├─────────────────────────────┤
│ Item Search [==========] 🔍│
├─────────────────────────────┤
│ Cart                        │
│ ┌───────────────────────┐  │
│ │ Item 1        £10.00 │  │
│ │ Item 2        £5.00  │  │
│ ├───────────────────────┤  │
│ │ Total          £15.00│  │
│ └───────────────────────┘  │
├─────────────────────────────┤
│ Numpad (64×64dp keys)       │
│      [Clear]                │ 
│      [Discount]             │
│      [Void]                 │
│ [.][⬅]     [Pay]           │
└─────────────────────────────┘
```

**Expanded (Tablet):**
```
┌─────────────────────────────────────────────┐
│ Top App Bar                                 │
│ [Store Name]                    [Shift]     │
├─────────────────────────────────────────────┤
│ Item Search [==========] 🔍                 │
├───────────────────────┬─────────────────────┤
│ Cart                  │ Item Grid           │
│ ┌───────────────────┐ │ ┌───┐ ┌───┐ ┌───┐  │
│ │ Item 1    £10.00 │ │ │📦1│ │📦2│ │📦3│  │
│ │ Item 2    £5.00  │ │ └───┘ └───┘ └───┘  │
│ ├───────────────────┤ │ ┌───┐ ┌───┐ ┌───┐  │
│ │ Total      £15.00│ │ │📦4│ │📦5│ │📦6│  │
│ └───────────────────┘ │ └───┘ └───┘ └───┘  │
├───────────────────────┴─────────────────────┤
│ Numpad (64×64dp keys)                       │
│      [Clear]                                │
│      [Discount]                             │
│      [Void]                                 │
│ [.][⬅]     [Pay]                            │
└─────────────────────────────────────────────┘
```

### Lock Screen

**Compact:**
```
┌─────────────────────────────┐
│ Welcome, [Store Name]       │
├─────────────────────────────┤
│ Staff Profiles (2 columns)  │
│ ┌──────┐ ┌──────┐          │
│ │ 👤   │ │ 👤   │          │
│ │ Budi │ │ Sari │          │
│ └──────┘ └──────┘          │
│ ┌──────┐ ┌──────┐          │
│ │ 👤   │ │ 👤   │          │
│ │ Ahmad│ │ Mia  │          │
│ └──────┘ └──────┘          │
└─────────────────────────────┘
```

**Expanded:**
```
┌─────────────────────────────────────────────┐
│ Welcome, [Store Name]                       │
├─────────────────────────────────────────────┤
│ Staff Profiles (3 columns)                  │
│ ┌──────┐ ┌──────┐ ┌──────┐                 │
│ │ 👤   │ │ 👤   │ │ 👤   │                 │
│ │ Budi │ │ Sari │ │ Ahmad│                 │
│ └──────┘ └──────┘ └──────┘                 │
│ ┌──────┐ ┌──────┐ ┌──────┐                 │
│ │ 👤   │ │ 👤   │ │ 👤   │                 │
│ │ Mia  │ │ Juan │ │ Lea  │                 │
│ └──────┘ └──────┘ └──────┘                 │
└─────────────────────────────────────────────┘
```

---

## Accessibility

### WCAG AA Requirements

- **Contrast ratio**: 4.5:1 for normal text, 3:1 for large text
- **Focus indicators**: 2dp outline on all focusable elements
- **TalkBack**: All buttons have contentDescription
- **Font scaling**: Support up to 200% without clipping

### Accessibility Checklist

- [ ] All icons have `contentDescription`
- [ ] All buttons are 48×48dp minimum
- [ ] Text contrast ≥ 4.5:1
- [ ] No color-only state indicators (use icon + colour)
- [ ] Screen content does not clip at 200% font scale
- [ ] Focus order is logical (left-to-right, top-to-bottom)

---

## Components

### Card

```kotlin
Card(
    modifier = modifier
        .padding(12.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colors.surface
    ),
    border = BorderStroke(
        1.dp,
        MaterialTheme.colors.outlineVariant
    )
) {
    // content
}
```

### Button

```kotlin
Button(
    modifier = modifier
        .height(56.dp),
    shape = RoundedCornerShape(8.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colors.primary
    )
) {
    // content
}
```

### Primary Action Button (POS)

```kotlin
Button(
    modifier = modifier
        .height(64.dp)
        .fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colors.success // green
    ),
    contentPadding = ContentPadding(16.dp)
) {
    Text(
        text = "Pay",
        style = Typography.titleLarge
    )
}
```

### Numpad Key

```kotlin
Button(
    modifier = modifier
        .size(64.dp),
    shape = RoundedCornerShape(8.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colors.surface
    )
) {
    Text(
        text = "7",
        style = Typography.titleLarge,
        fontFamily = FontFamily.Monospace
    )
}
```

### Chip (Filter)

```kotlin
Chip(
    modifier = modifier.padding(4.dp),
    shape = RoundedCornerShape(4.dp),
    colors = ChipDefaults.chipColors(
        containerColor = MaterialTheme.colors.primaryContainer
    )
) {
    Text("Category")
}
```

### TextField (Item Search)

```kotlin
OutlinedTextField(
    modifier = modifier
        .height(56.dp),
    value = searchText,
    onValueChange = { searchText = it },
    placeholder = { Text("Search items...") },
    leadingIcon = { Icon(Icons.Search, "Search") },
    trailingIcon = {
        if (searchText.isNotEmpty()) {
            IconButton(onClick = { searchText = "" }) {
                Icon(Icons.Clear, "Clear")
            }
        }
    },
    shape = RoundedCornerShape(8.dp)
)
```

---

## Version

- **Initial release**: 2026-03-22
- **Last updated**: 2026-03-22
- **Maintainer**: Core maintainer