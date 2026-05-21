# ScheduleTeller

ScheduleTeller is a lightweight and user-friendly Android application designed to help students manage their academic schedules efficiently. It provides a clean, dark-themed interface to track classes and automatically calculates gaps between them, highlighting long breaks as "Okienko" (free slots).

## 🚀 Key Features
* **Chronological Schedule View:** Clear daily breakdown of classes.
* **Smart Break Detection:** Automatically identifies and calculates the duration of breaks between lectures and labs.
* **Special "Okienko" Mode:** Highlights breaks lasting 90 minutes or longer.
* **Flexible Modes:** Supports both on-site (room-based) and remote (e.g., MS Teams) classes.
* **Dark-Themed Material Design:** Optimized for readability and battery efficiency.

## 🛠 Setup Instructions
To ensure data privacy, the schedule data is not hardcoded in the source code. Follow these steps to set up the project:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/ScheduleTeller.git
   ```
2. **Prepare your schedule data:**
   * Navigate to the `app/src/main/assets/` folder.
   * Locate the `schedule.template.json` file.
   * Create a copy of this file and name it `schedule.json`.
3. **Configure the schedule:**
   * Open `schedule.json` and fill it with your own academic data, following the structure defined in the template.
4. **Build the project:**
   * Open the project in Android Studio.
   * Sync Gradle and run the application on your device or emulator.

*Note: A `.gitignore` file is included in this repository to prevent your `schedule.json` (which contains your personal data) from being uploaded to GitHub.*

## 📂 Project Structure
* `/app/src/main/java/`: Core logic of the application (Fragments, ViewModels, Data Models).
* `/app/src/main/assets/`: Contains `schedule.template.json` (structure) and the location for your `schedule.json` (private user data).
* `/app/src/main/res/`: Layout files, UI resources, icons, and themes.

## ⚙️ Prerequisites
* **Android Studio:** Jellyfish version or newer recommended.
* **JDK:** 17 or higher.
* **Minimum SDK:** Android 8.0 (API 26) or higher.

## 🤖 AI-Assisted Development
This project was developed with the support of AI tools:
* **Code:** The core logic, UI implementation, and break-calculation algorithms were written and optimized with the help of **Google Gemini**.
* **Iconography:** The application launcher icon was conceptualized and generated using **ChatGPT (DALL-E 3)**.

## 🔒 Privacy
This repository is designed with privacy in mind. Sensitive schedule information remains local to your device and is excluded from version control via `.gitignore`.

## 📱 Tech Stack
* **Kotlin**
* **Material Design Components**
* **GSON** (JSON Parsing)
* **Android Jetpack**

## 📝 License
This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.
You are free to use, modify, and distribute the code, provided that any derivative works are also distributed under the same license terms and include proper attribution.