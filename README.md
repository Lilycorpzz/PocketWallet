# PocketWallet

## Video

[Video](https://drive.google.com/file/d/1b4r79BbptdVatn2IWHuIGSmGVbDaPPQL/view?usp=sharing)

## APK

Apk is in the app folder.

## App Overview

PocketWallet enables users to:

Log in securely with a username and password

Add and manage spending categories

Register daily expenses with optional photo attachments

Set minimum and maximum monthly budget goals

View total spending by category over a chosen period

Track achievements based on budget performance

All data is stored locally using RoomDB (or SQLite), ensuring offline access and data persistence.

## Main Features

- Login Page

Users can log in using a username and password.

If either field is left empty, an error message prompts the user to complete both fields.

Upon successful login, users are taken to the Home Dashboard.

- Home Page (Dashboard)

Displays the user’s current budget overview, including total spending and remaining balance.

Navigation buttons allow quick access to:

Register Expense

Categories

Achievements

- Register Expense Page

Allows users to:

Add a new expense with a name, description, category, and amount (Rands).

Attach an optional photo for receipts or proof of purchase.

Includes input validation and feedback messages.

Data saved locally for persistence.

- Category Page

Users can create, edit, and delete spending categories.

Each expense must belong to a category.

Displays total spending per category for a selected date range.

- Achievement Page

Shows progress toward monthly spending goals.

Provides feedback or achievements for staying within budget limits.

Encourages responsible financial habits.

- Data Persistence

All user data is stored locally using:

RoomDB / SQLite

Supports offline usage

Automatically updates totals and category summaries

## Technologies Used

Language: Kotlin

Framework: Android SDK (Jetpack Components)

Database: RoomDB / SQLite

UI Design: XML Layouts & Material Components

## How to Run

1. Open the project in **Android Studio**.
2. Sync Gradle and make sure all dependencies install successfully.
3. Run the app on an emulator or physical Android device.
4. Log in using any username and password.
5. Explore the Home and Expense Entry pages.

## Currency

All monetary values in this app are represented in **South African Rands (ZAR)**.

## Developers

**Name:** [Tapuwa, Mpho, Sihle]  
**Institution:** [Vega School]  
**Module:** [OPSC7311]  
**Year:** 2025  
