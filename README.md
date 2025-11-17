# PocketWallet

## Video
[Watch the demo](https://drive.google.com/file/d/1kduCbd6kqFKHUEWLwb0PPgyeKndqfaYM/view?usp=sharing)

## APK
The APK is in the `app/` folder.

---

## Overview
PocketWallet helps you plan and track day-to-day spending.

- Secure **login** (username & password)  
- Create and manage **categories**  
- Register **expenses** (with optional receipt photo)  
- Set **monthly budget goals** (min & max)  
- See **spend per category** over a user-selectable period  
- Automatically **warn** when you **over-spend** your max goal  
- **Most recent expense** displayed on the Home page for quick context  
- Works **offline** with Room/SQLite and **syncs** via Firebase

---

## What’s New (This Build)

1. **Monthly Budget Goals + Over-Budget Alerts**  
   - Set **Min** and **Max** in the **Budget** screen.  
   - When you add expenses, the app totals the month and **warns you if you’ve gone over** your Max goal.  
   - The Budget screen includes a **bar chart** that draws **Min/Max** as goal lines.

2. **Category Spinner + Category Bar Graph on Home**  
   - A **Category spinner** was added to the Categories/Expense flow.  
   - On **Home**, a **bar graph** shows **amount spent per category** over a **user-selectable period** (Start/End date pickers).  
   - As you add categories/expenses, the chart updates automatically.  
   - The **most recent expense** is surfaced on the Home page for instant reference.

> The graphs are display-only: users add data via the forms; charts update themselves.

---

## Screens & Features

### Login
- Username + password validation; routes to Dashboard on success.

### Home (Dashboard)
- Current **balance** (Income − Expenses).  
- **Most recent expense** summary at the top.  
- **Expenses by Category** bar chart:
  - Period controls (Start/End date pickers).  
  - Bars colored by category.  
  - **Min/Max** budget goal lines pulled from Budget settings.  
- **Over-spending warning** displayed if monthly total > Max goal.  
- Quick links: **Register Expense**, **Categories**, **Achievements**, **Budget**.

### Register Expense
- Fields: Name, Description, **Category (spinner)**, Amount (ZAR), **Date**, **Start/End Time**, optional **Photo**.  
- Saves to Room and syncs to Firebase.  
- Immediately reflected in the Home chart, the “most recent expense” banner, and over-budget check.

### Categories
- Create/edit category **name**, **description**, **budget**, and **color**.  
- Category colors are reused for chart bars.  
- Category spinner selection standardizes entries.

### Budget Goals
- Set **Minimum** and **Maximum** monthly goals.  
- Built-in **bar chart** visualizes these as **limit lines**.  
- Automatic “**over-budget**” alert when monthly total exceeds Max goal.

### Achievements
- Simple progress indicators for staying within budget.

---

## Data & Persistence

- **Local (offline-first):** RoomDB / SQLite  
  Stores categories, expenses, and goals for offline use.

- **Cloud Sync: Firebase Realtime Database**  
  - Data replicated under:
    - `/users/{uid}/categories/...`
    - `/users/{uid}/expenses/...`
    - `/users/{uid}/goals/...`
  - On startup, Room hydrates local data from Firebase if available; new/updated items are pushed back.

---

## Tech Stack

- **Language:** Kotlin  
- **Android:** Jetpack, ConstraintLayout, Material Components  
- **Charts:** MPAndroidChart (bar charts + limit lines)  
- **Local DB:** Room / SQLite  
- **Cloud:** Firebase Realtime Database  
- **Images:** optional receipt photos

---

## Run the Project

1. Open in **Android Studio**.  
2. Add Firebase config:  
   - Place `google-services.json` in `app/`.  
   - Ensure the **Google Services** Gradle plugin is applied.  
3. Sync Gradle and build.  
4. Run on device/emulator.  
5. Log in (any username/password for demo).  
6. Create categories, set **Min/Max** in **Budget**, then add expenses — Home will show your **most recent expense**, the **bar chart**, and warn if you go **over budget**.

---

## Currency
All amounts are shown in **South African Rands (ZAR)**.

---

## Developers
**Tapuwa, Mpho, Sihle**  
**Vega School** — **OPSC7311** (2025)
