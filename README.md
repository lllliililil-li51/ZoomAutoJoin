# Zoom Auto Join (Android)

A small Android app that lets you:
1. Paste a Zoom link
2. Pick a date + time (optionally "repeat weekly")
3. At that exact time, your phone shows a full-screen "Joining now" alarm
   screen and automatically opens Zoom and joins the meeting — even if the
   phone is locked or the app is closed.

## How to build & install it

You need **Android Studio** (free, from developer.android.com/studio) on a
Windows/Mac/Linux computer.

1. Unzip this project.
2. Open Android Studio → **Open** → select the unzipped `ZoomAutoJoin` folder.
3. Let it sync (it will auto-download the Gradle wrapper — first sync needs
   internet).
4. Plug your Android phone in via USB with **Developer Options + USB
   debugging** enabled (Settings → About Phone → tap "Build number" 7 times,
   then Settings → Developer Options → USB debugging).
5. Click the green ▶ Run button, pick your phone, and it installs directly.

You now have a real app icon on your phone called "Zoom Auto Join" — no Play
Store needed.

## Using the app

1. Open the app.
2. Tap **"Allow Exact Alarms"** and **"Disable Battery Optimization for this
   app"** once — this is the single biggest factor in whether Android lets
   the alarm fire exactly on time instead of delaying it.
3. Type a class name, paste the Zoom link (e.g.
   `https://zoom.us/j/1234567890?pwd=abcDEF`), pick the date and time, check
   "Repeat every week" if it's a recurring class, then tap **Schedule
   Auto-Join**.
4. At that moment, your phone will show a full-screen "Joining now" screen
   for 3 seconds (tap Join Now to skip the wait, or Cancel to stop it), then
   it launches Zoom and joins automatically using the meeting ID + passcode
   from your link.

## Important real-world notes

- **Zoom must already be installed** on the phone — the app hands the link
  to Zoom, it doesn't reimplement a Zoom client.
- **Auto-join without a tap only works if your link contains a passcode**
  (`?pwd=...`). Some links use a personal meeting room ID with no passcode
  parameter — those still open Zoom instantly, but Zoom itself may ask you
  to confirm your name once.
- **Phone must be powered on** (not off) at class time — no app can start on
  a fully powered-down phone.
- **Some phone brands (Xiaomi/Redmi, Oppo, Vivo, Huawei, some Samsung)**
  aggressively kill background apps. If alarms feel unreliable on these,
  search "[your phone brand] autostart permission" and enable autostart /
  "no restrictions" for this app in the phone's own battery settings, in
  addition to the in-app battery button.
- This is a **from-scratch open-source project**, not affiliated with Zoom.
  It works by using Zoom's own public deep-link format
  (`zoommtg://zoom.us/join?...`), which is the same mechanism Zoom's own
  "Add to Calendar" links use.

## Project structure

- `MainActivity.kt` — the form + list of scheduled classes
- `AlarmScheduler.kt` — arms/cancels the exact `AlarmManager` alarms
- `AlarmReceiver.kt` — fires at class time, shows the full-screen notification
- `JoinActivity.kt` — the full-screen "joining now" screen, launches Zoom
- `BootReceiver.kt` — re-arms all future alarms if the phone restarts
- `ZoomLinkUtils.kt` — converts a normal zoom.us link into the auto-join
  deep-link format
- `ScheduleStore.kt` — simple on-device storage (SharedPreferences)
