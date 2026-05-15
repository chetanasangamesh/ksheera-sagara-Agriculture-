# Ksheera Sagara — Dairy Farming P&L App

## Overview
Android app (Kotlin + Jetpack Compose + Room DB) for dairy farmers to track milk income, farm expenses, and net profit/loss.

## Architecture
- **UI**: Jetpack Compose, single-activity, state-driven navigation (`AppPage` enum)
- **Data**: Room Database (local SQLite)
- **Pattern**: MVVM — ViewModel + DAO + Entity

## Key Files
| File | Purpose |
|------|---------|
| `app/src/main/java/ui/KsheeraSagaraApp.kt` | All UI composables and navigation |
| `app/src/main/java/viewmodel/DairyViewModel.kt` | Business logic, DB operations |
| `app/src/main/java/data/DairyDao.kt` | Room DAO queries |
| `app/src/main/java/data/MilkEntry.kt` | Milk record entity |
| `app/src/main/java/data/Expense.kt` | Expense record entity |
| `app/src/main/java/data/DairyDatabase.kt` | Room database config |
| `app/src/main/java/com/example/ksheera_sagara/MainActivity.kt` | App entry point |

## Pages (AppPage enum)
1. **Dashboard** — Overview with quick-action cards, live summary strip
2. **Milk Entry** — Log milk collection (liters, fat, SNF, rate)
3. **Expenses** — Log farm costs with category chips (Feed, Medicine, Labor, Vet, Transport)
4. **Reports** — Profit/Loss summary with delete confirmation
5. **Records** — Tabbed list of all DB entries (Milk / Expenses) with per-record delete

## Features Added (latest update)
- **Back arrow** — Shown in header whenever not on Dashboard; returns to Dashboard
- **Records page** — New 5th page showing all database entries in tabbed cards
- **Per-record delete** — Each milk/expense card has a delete button with confirmation step
- **Attractive UI overhaul** — Gradient cards, emoji icons, animated tab colors, better typography
- **Category chips** — Quick-select for expense categories
- **Estimated income preview** — Shows calculated income while filling milk entry form
- **Confirmation dialog** — "Clear All" now requires explicit confirm/cancel

## Build
- compileSdk 36, minSdk 24, Kotlin + Compose BOM
- Room for local persistence with kapt annotation processing
