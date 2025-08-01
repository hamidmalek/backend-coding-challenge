# 🎬 Large-Scale Movie Rating System
This project is designed to support a large-scale Movie Rating System with:

---

## 📊 Scale & Requirements
- 1M Daily Active Users – horizontally scalable architecture to handle high concurrency.

- ~50M Movies – each user posts about 50 movies, requiring efficient storage and indexed retrieval.

- 100M+ Ratings (potentially billions) – baseline is 1M × 100, but realistic scenarios include much higher volumes due to user growth and rating frequency.

- User Pages – fast, paginated listing of a user's posted movies (P95 latency <200ms for first page).

- User Search – quick lookup by display name or email (<250ms P95) using trigram indexes.

- Popular Movies in Near Real-Time – ranked by ratings count, average score, and recency; updates visible globally within 60s and locally within 5s.

- Design implications: PostgreSQL with tuned indexes, Redis caching for hot endpoints, denormalized aggregates for performance, pagination limits, and consistency guarantees (strong for writes, eventual for global lists).
