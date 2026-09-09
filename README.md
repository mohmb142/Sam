# SAM — Smart Personal Assistant

SAM is an Android-first, Arabic-friendly personal AI agent. The current MVP provides voice input, OpenRouter chat, secure API-key storage, structured agent actions, call-screening role support, local memory storage, and scheduled-task notifications.

## Current release

**0.3.0 — installable/debug build target**

### Implemented
- Android 10+ (API 29)
- Arabic RTL Compose UI
- OpenRouter as the primary AI provider
- User-supplied OpenRouter API key and model ID
- OpenAI-compatible provider abstraction
- Secure local secret storage
- Voice input with Android SpeechRecognizer and TTS foundation
- Structured AgentAction / Tool / PermissionManager architecture
- CallScreeningService role request and incoming-call notification
- Local preference memory store
- AlarmManager-based scheduled task notifications
- GitHub Actions debug APK build

### Planned integrations
WhatsApp/Telegram automation, richer call conversation, browser tools, calendar, advanced memory, and additional AI providers will be added only through Android-supported APIs and explicit permissions.

## Security

SAM never gives an LLM direct shell or Android execution privileges. Model output must become a structured action and pass validation/permission checks before a tool executes it.

Never commit an OpenRouter API key to Git. Keys are stored locally using encrypted Android storage.

## Build

Open the project in Android Studio and run the `app` module. GitHub Actions also builds `app-debug.apk` on pushes to `main` and uploads it as the `sam-debug-apk` artifact.
