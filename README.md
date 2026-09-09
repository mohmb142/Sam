# SAM — Smart Personal Assistant

SAM is an Android-first personal AI agent designed to interact with the phone through voice, memory, permissions, and structured tools.

## Core vision

- Voice-first Arabic-friendly assistant
- Personal memory and user preferences
- Call handling and call summaries where Android permits
- App/tool integrations for messaging, browser, contacts, calendar and notifications
- Scheduled tasks and reminders
- Multiple AI providers with user-supplied API keys
- Model routing and fallback models
- Permission-aware structured actions

## Security

SAM must never allow an LLM to execute arbitrary Android or shell commands directly. Model output is converted into structured actions and validated by the permission manager before execution.

API keys are intended to be stored securely on-device using Android Keystore/encrypted storage and must never be committed to Git.

## Project status

Initial project scaffold. The implementation will be built incrementally, starting with the Android app foundation, model-provider abstraction, voice flow, memory, permissions, and task engine.
