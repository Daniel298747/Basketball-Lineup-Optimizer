package basketball;

import java.util.*;

/**
 * Entry point — builds a roster, runs the optimiser, and prints results.
 */
public class Main {

    public static void main(String[] args) {

        // ─── Build Roster ────────────────────────────────────────────────
        // Player(name, position, scoring, defense, rebounding, playmaking, athleticism, 3PT, stamina)
        List<Player> roster = new ArrayList<>(Arrays.asList(
            new Player("Marcus Webb",      "PG", 82, 68, 45, 91, 80, 78, 85),
            new Player("Jordan Ellis",     "PG", 74, 72, 42, 85, 76, 65, 90),
            new Player("Darius Cole",      "SG", 88, 65, 50, 70, 82, 84, 80),
            new Player("Kai Thompson",     "SG", 80, 70, 48, 75, 79, 88, 75),
            new Player("Lamar Okafor",     "SF", 76, 80, 72, 68, 85, 72, 82),
            new Player("Trevon Hayes",     "SF", 83, 74, 68, 72, 88, 60, 78),
            new Player("DeShawn Morris",   "PF", 70, 85, 82, 60, 83, 55, 88),
            new Player("Elijah Grant",     "PF", 78, 78, 79, 65, 80, 63, 84),
            new Player("Malik Osei",       "C",  65, 88, 91, 52, 75, 40, 86),
            new Player("Byron Stokes",     "C",  72, 82, 87, 58, 78, 45, 80)
        ));

        // ─── Initialise Optimiser ─────────────────────────────────────────
        LineupOptimizer optimizer = new LineupOptimizer(roster);

        // Print roster overview
        optimizer.printRosterStats();

        // Generate all combinations
        optimizer.generateAllLineups();
        System.out.printf("%n▶ Evaluated %,d unique lineup combinations.%n",
            optimizer.getTotalCombinations());

        // ─── Top 3 Overall Lineups ─────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║             TOP 3 OVERALL LINEUPS                   ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        List<Lineup> top3 = optimizer.getTopLineups(3);
        for (int i = 0; i < top3.size(); i++) {
            System.out.printf("%n  #%d Ranked Lineup:%n", i + 1);
            System.out.println(top3.get(i));
        }

        // ─── Scenario Lineups ─────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║           SCENARIO-BASED RECOMMENDATIONS            ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        optimizer.getScenarioLineups().forEach((scenario, lineup) -> {
            System.out.printf("%n  ▸ %s:%n", scenario);
            System.out.println(lineup);
        });

        // ─── Full Game Simulation ──────────────────────────────────────────
        optimizer.runGameSimulation();

        // ─── Fatigue-Aware Late-Game Lineup ───────────────────────────────
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║         FATIGUE-AWARE LATE-GAME LINEUP               ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        optimizer.getFreshLineup(50).ifPresentOrElse(
            l -> System.out.println("\n  Best fresh lineup (stamina > 50):\n" + l),
            () -> System.out.println("  ⚠ No lineup with all players above stamina threshold.")
        );

        System.out.println("\n  ✓ Optimisation complete.");
    }
}
