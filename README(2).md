# Basketball Lineup Optimization Strategy
## Java OOP Simulation — CS Portfolio Project

---

## Project Overview

A Java object-oriented simulation applying algebra, statistics, and optimisation principles
to evaluate and rank all possible 5-player lineup combinations from a basketball roster.

The system models player synergy, multi-attribute performance metrics, stamina degradation,
and scenario-based recommendations across a full 48-minute game.

---

## Architecture

```
src/
├── Player.java          — Data model with 7 performance attributes
├── Lineup.java          — 5-player group with synergy calculation
├── LineupOptimizer.java — Combinatorial search + optimisation engine
└── Main.java            — Simulation runner and output
```

---

## Key Algorithms & Concepts

### 1. Player Rating Model (Player.java)
Each player has 7 attributes (0–100 scale):
- Scoring, Defense, Rebounding, Playmaking, Athleticism, 3-Point, Stamina

Composite ratings are computed as weighted sums:
```
OVR  = 0.25*SCR + 0.20*DEF + 0.15*REB + 0.15*PMA + 0.10*ATH + 0.10*3PT + 0.05*STM
OFF  = 0.40*SCR + 0.30*PMA + 0.20*3PT + 0.10*ATH
DEF  = 0.50*DEF + 0.30*REB + 0.20*ATH
```

Stamina degrades non-linearly with minutes played:
```
fatigueFactor = 1.0 + (minutes / 48.0)
newStamina = max(0, stamina - minutes * fatigueFactor * 0.8)
```

### 2. Synergy Model (Lineup.java)
Lineup synergy rewards:
- Positional balance: +2 per unique position (max +10)
- Elite playmaking + scoring combo: +6
- Floor spacing (3+ shooters with 3PT > 65): +2.5 each
- Star power (any player OVR > 85): +4

Penalties:
- Low defensive floor (avg DEF < 55): −5
- Fatigued squad (avg stamina < 40): −4

### 3. Combinatorial Optimisation (LineupOptimizer.java)
Generates all C(n,5) combinations via recursive backtracking:
```
C(10,5) = 252 unique lineups evaluated
```

Lineups are ranked by composite score:
```
CompositeScore = 0.50*OFF_rating + 0.35*DEF_rating + 0.15*Synergy
```

Separate optimisation targets: best offensive, best defensive, best net rating (closing).

### 4. Game Simulation
Simulates Q1–Q4 with fatigue propagation:
- Each quarter: active players consume ~10 min of stamina
- Bench players recover 4 stamina points per quarter
- Optimizer re-evaluates all lineups each quarter with updated stamina values

---

## How to Run

```bash
# Compile
javac -d out src/*.java

# Run
java -cp out basketball.Main
```

Requires: Java 14+ (uses switch expressions and text blocks)

---

## Sample Output

```
▶ Evaluated 252 unique lineup combinations.

#1 Ranked Lineup:
┌─────────────────────────────────────────────────────┐
│  Lineup Score: 74.40  | Synergy Bonus: +24.0        │
│  OFF Rating: 87.58  | DEF Rating: 77.18             │
├─────────────────────────────────────────────────────┤
│  Marcus Webb        [PG] OVR: 74.6 | OFF: 83.7 ...  │
│  Darius Cole        [SG] OVR: 73.6 | OFF: 81.2 ...  │
│  Kai Thompson       [SG] OVR: 72.9 | OFF: 80.0 ...  │
│  Lamar Okafor       [SF] OVR: 75.8 | OFF: 73.7 ...  │
│  Elijah Grant       [PF] OVR: 75.2 | OFF: 71.3 ...  │
└─────────────────────────────────────────────────────┘
```

---

## CV Bullet Points (from spec)

- Designed and implemented a Java OOP simulation applying algebra, statistics,
  and optimisation principles to improve team performance
- Modelled player synergy and performance metrics to evaluate 252 optimal lineup
  combinations under varying game scenarios
- Analysed results using quantitative techniques (weighted composite scoring,
  net rating, fatigue modelling) to support data-driven decision making
