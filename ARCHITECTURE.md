# SAM Architecture

SAM is intentionally split into independent layers:

- `ai/` — provider abstraction, model configuration and routing.
- `agent/` — structured actions, tools, risk levels and execution.
- `security/` — encrypted local secrets.
- `voice/` — planned STT/TTS and wake-word integration.
- `calls/` — planned Android call integration, subject to Android APIs and device/OEM restrictions.
- `apps/` — planned app integrations using permitted Android mechanisms.
- `memory/` — planned local user preferences and memory.
- `scheduler/` — planned scheduled tasks and notifications.

## Execution rule

The LLM never executes arbitrary Android, shell, browser or network commands. It produces a structured action. The application validates that action and checks permissions before a tool can execute it.

## Model providers

`AIProvider` is the extension point. A provider receives a `ModelConfig` containing provider name, model, base URL and an API key reference. Secrets are stored separately in encrypted local storage.

## Roadmap

1. Android foundation and Arabic UI
2. Multi-provider configuration screen
3. Secure API key storage and connection testing
4. Voice STT/TTS
5. Agent planner and tool registry
6. Call assistant
7. App integrations
8. Memory and user rules
9. Scheduler and follow-ups
10. Automated tests and release builds
