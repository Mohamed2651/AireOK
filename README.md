# 🌿 AireOK — Air Quality Android App

**AireOK** is a native Android application built with Kotlin and Jetpack Compose that provides real-time air quality data for monitoring stations across Spain. It consumes a custom REST API backed by the public [WAQI](https://aqicn.org/api/) (World Air Quality Index) data source.

---

## 📱 Screenshots

> _Add screenshots here_

---

## ✨ Features

- 🏠 **Home** — stations sorted by proximity to the user's location
- 🔍 **Search** — find stations by city or name
- 🗺️ **Map** — interactive OSMDroid map with color-coded AQI markers
- 📊 **Station detail** — pollutants, temperature, humidity, UV index, 5-day forecast
- ❤️ **Favourites** — save and sync favourite stations with the server
- 👤 **Profile** — view and edit user name
- 🌿 **Eco tips** — ecological advice organised by category
- 🔐 **Auth** — register, login, forgot password (email link), reset password
- 🌙 **Dark / Light mode** — full Material Design 3 theming

---

## 🏗️ Architecture

The Android project follows **Clean Architecture** with three independent layers:

```
com.mohammed.aireok/
├── domain/          # Entities, use case interfaces, repository interfaces
├── data/            # Retrofit data sources, DTOs, mappers, TokenManager
├── presentation/    # Composables, ViewModels (MVVM), Navigation
├── di/              # Hilt modules
└── ui/theme/        # Color.kt, Theme.kt, Type.kt (Material 3)
```

Pattern: **MVVM** · Dependency injection: **Hilt** · Navigation: **Navigation Compose**

---

## 🛠️ Tech Stack

### Android client

| Library | Purpose |
|---|---|
| Kotlin + Jetpack Compose | UI and language |
| Material Design 3 | Design system (Dark/Light mode) |
| Navigation Compose | Screen navigation |
| Hilt (Dagger) | Dependency injection |
| Retrofit 2 + Gson | HTTP calls to the REST API |
| OkHttp 3 | JWT Bearer interceptor + logging |
| DataStore Preferences | JWT token persistence |
| OSMDroid (OpenStreetMap) | Interactive map |
| Coroutines | Async operations |

### Backend

| Technology | Purpose |
|---|---|
| Node.js + Express | REST API server |
| PostgreSQL | Database (hosted on Render) |
| bcryptjs | Password hashing (cost factor 12) |
| jsonwebtoken | JWT session management (7d expiry) |
| node-cron | Hourly station sync from WAQI |
| Brevo (Sendinblue) | Password recovery emails |
| Docker + docker-compose | Local development environment |

---

## 🚀 Getting started

### Prerequisites

- Android Studio Hedgehog or newer
- Android device or emulator with API 24+
- Node.js 20+ (for local backend)
- Docker (optional, for local DB)

---

### Run the Android app

```bash
git clone https://github.com/Mohamed2651/AireOK
```

Open the project in Android Studio, then run on a device or emulator.

The app connects to the production API at `https://aire-ok-backend.onrender.com` by default — no extra configuration needed to run it.

> **Note:** the free Render instance hibernates after 15 minutes of inactivity. The first request may take ~30 seconds to wake the server.

---

### Run the backend locally

**Option A — Docker (recommended)**

```bash
cd aire-ok-backend
cp .env.example .env   # fill in WAQI_TOKEN and BREVO_API_KEY
docker-compose up
```

The API will be available at `http://localhost:3000`.

**Option B — Manual**

```bash
cd aire-ok-backend
npm install
# create .env with the variables below
node src/index.js
```

#### Required environment variables

```env
NODE_ENV=production
PORT=3000
DATABASE_URL=postgresql://user:pass@host:5432/dbname
JWT_SECRET=your_long_random_secret
JWT_EXPIRES_IN=7d
WAQI_TOKEN=your_waqi_api_token
BREVO_API_KEY=your_brevo_api_key
APP_URL=https://aire-ok-backend.onrender.com
```

Get a free WAQI token at [aqicn.org/data-platform/token](https://aqicn.org/data-platform/token/).  
Get a free Brevo API key at [brevo.com](https://www.brevo.com/).

---

## 🗄️ Database schema

The database is auto-created on first run via `initDatabase()`:

| Table | Description |
|---|---|
| `usuarios` | User accounts (name, email, bcrypt hash, role) |
| `estaciones` | Station registry from WAQI (id, name, lat, lon) |
| `mediciones` | AQI measurements per station with timestamp |
| `favoritos` | User ↔ station N:N relationship |
| `preferencias_usuario` | Alert threshold and notification settings |
| `reset_tokens` | Password recovery tokens (1h expiry) |
| `logs_actividad` | User action audit log |

---

## 🔌 API endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/registro` | — | Register new user |
| `POST` | `/api/auth/login` | — | Login, returns JWT |
| `GET` | `/api/auth/me` | JWT | Authenticated user profile |
| `POST` | `/api/auth/recuperar` | — | Send password recovery email |
| `POST` | `/api/auth/reset-password` | — | Reset password with token |
| `GET` | `/api/aire/estaciones` | — | All stations with latest AQI |
| `GET` | `/api/aire/estacion/:uid` | — | Full station detail from WAQI |
| `GET` | `/api/aire/cercana?lat=&lon=` | — | Nearest station to coordinates |
| `GET` | `/api/aire/buscar?q=` | — | Search stations by name |
| `GET` | `/api/aire/historial/:uid` | JWT | AQI history (up to 30 days) |
| `GET` | `/api/usuario/favoritos` | JWT | User's favourites |
| `POST` | `/api/usuario/favoritos` | JWT | Add station to favourites |
| `DELETE` | `/api/usuario/favoritos/:id` | JWT | Remove from favourites |
| `PUT` | `/api/usuario/perfil` | JWT | Update user name |

Health check: `GET /health`

---

## 🎨 Design

The UI was fully prototyped in Figma before implementation, following Material Design 3 guidelines with a custom green-based color palette supporting both Dark and Light modes.

🔗 [Figma prototype](https://www.figma.com/design/1qgnWtwGqw9ZPw0WCHQbPI/FigmaAireOK)

---

## 📂 Project structure

```
AireOK/                        # Android app
├── app/src/main/java/com/mohammed/aireok/
│   ├── data/                  # Remote data sources, DTOs, mappers, TokenManager
│   ├── di/                    # Hilt modules (Network, Repository, UseCase…)
│   ├── domain/                # Entities, use cases, repository interfaces
│   ├── presentation/          # Screens and ViewModels per feature
│   └── ui/theme/              # Material 3 theme (Color, Type, Theme)
│
aire-ok-backend/               # Node.js REST API
├── src/
│   ├── db/database.js         # PostgreSQL connection + schema init
│   ├── middleware/auth.js      # JWT + admin middleware
│   ├── routes/                # auth, aire, usuario, recuperar, incidencias
│   └── services/
│       ├── waqiService.js     # WAQI API integration + hourly cron sync
│       └── emailService.js    # Brevo transactional email
├── Dockerfile
└── docker-compose.yml
```

---

## 🔒 Security

- Passwords stored with **bcrypt** (cost factor 12)
- All API communication over **HTTPS**
- JWT tokens expire after **7 days**
- Password recovery tokens expire after **1 hour** and are single-use
- Protected endpoints require `Authorization: Bearer <token>` header
- Role-based access control: `invitado`, `registrado`, `admin`

---

## 📄 License

This project was developed as the final integrated project (*Proyecto Intermodular*) for the **2.º DAM** programme at Institut La Senia, Valencia — 2025/2026.

---

## 👤 Author

**Mohamed Shahin** — [github.com/Mohamed2651](https://github.com/Mohamed2651)
