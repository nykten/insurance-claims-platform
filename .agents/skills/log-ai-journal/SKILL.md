---
name: log-ai-journal
description: Draft a journal entry skeleton for docs/ai-journal.md. Use only when the user asks to log an AI interaction.
---

The journal records the human's judgement. Do not invent decisions.

Produce a skeleton the user will complete, and do NOT write it into the file yourself:

### <time> <short title>
- **Asked:** <what the user prompted, one line>
- **AI proposed:** <summary of what you produced, factual>
- **Accepted:** [user fills in]
- **Challenged / overrode:** [user fills in]
- **Why:** [user fills in]
- **Commit:** <hash if known>

Only fill in "AI proposed" and "Asked" from the session. Leave the rest for the user.