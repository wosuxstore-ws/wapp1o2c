# WL Orders — admin app (all orders in one list)

An Android app for **you, the store owner**. Log in with the username/password stored in the
`wapp` table of your database, and see **every order from every customer** in one scrolling
list: customer name + phone, city, payment method, the items ordered, total, status and time.
Tap an order for full details (address, discounts, shipping, line items).

```
php-api/        PHP files that sit in front of your MySQL database (upload to hosting, e.g. public_html/api/)
android-app/    Open directly in Android Studio, or let GitHub Actions build the APK
.github/workflows/build-apk.yml   Builds a debug APK on every push to main
```

## How login works

1. The app sends the username + password to `login.php`.
2. `login.php` looks them up in the **`wapp`** table (`id`, `user`, `password`).
3. On success it returns a signed **token** (valid 30 days). The app stores it and sends it with
   every request (`X-Auth-Token` header). `get_orders.php` and `get_order_detail.php` refuse to
   answer without a valid token, so nobody can read your orders just by knowing the URL.
4. A token stops working if it expires, if you change `TOKEN_SECRET`, or if you change that
   admin's password in `wapp`. The app then sends you back to the login screen.

No database changes are needed — the `wapp` table is used exactly as it is.
To add another admin, just insert another row into `wapp`.

## Part 1 — Upload the PHP files

1. Upload everything in `php-api/` to your server's `api/` folder (overwrite the old files).
2. Edit `config.php` on the server: your DB details, and **`TOKEN_SECRET`** — a long random
   string (32+ characters). Login will refuse to work until you change it from the placeholder.
3. **Delete `create_test_user.php` from the server.** It is no longer used, and anyone holding
   its key could reset customer passwords with it.
4. Make sure `.htaccess` uploaded too (it blocks browser access to `config.php`).

### Test it before touching the app

```bash
# 1) log in - copy the "token" value from the reply
curl -X POST https://yourdomain.com/api/login.php \
  -H "Content-Type: application/json" \
  -d '{"user":"wadmin","password":"YOUR_PASSWORD"}'

# 2) all orders, with items
curl https://yourdomain.com/api/get_orders.php -H "X-Auth-Token: PASTE_TOKEN_HERE"

# 3) without a token it must be refused (HTTP 401)
curl -i https://yourdomain.com/api/get_orders.php
```

## Part 2 — The Android app

`android-app/app/src/main/java/com/wl/orders/ApiConfig.java` → `BASE_URL` must point at the
folder holding the PHP files (currently `https://wosux.com/api/`).

- **Android Studio:** File → Open → select `android-app`, let Gradle sync, press ▶.
- **GitHub Actions:** push this repo to GitHub → Actions tab → *Build debug APK* → download the
  `wl-orders-debug-apk` artifact → install `app-debug.apk` on your phone.

In the app: pull down to refresh, ⋮ menu → Refresh / Log out.

## Security notes

- **Change the `wapp` password.** It is stored as plain text (and `1234admin` is easy to guess).
  `login.php` also accepts a bcrypt hash in that column, so you can upgrade later without any
  code change. Changing the password logs out all existing sessions.
- Never commit `config.php` with real credentials to GitHub — the copy in this repo has
  placeholders; keep the real one on the server only.
- The app talks to your server over HTTPS. `usesCleartextTraffic` in `AndroidManifest.xml` is
  only needed for testing against a local `http://` server; remove it once you're done testing.
- Debug APKs are fine for your own phone. For the Play Store you'd add a signing config and
  build a release AAB.
