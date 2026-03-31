package basketball;

import java.util.*;
import java.util.stream.*;

/**
 * Optimises lineups using combinatorial search + greedy heuristics.
 * Evaluates all C(n,5) combinations and ranks by composite score.
 */
public class LineupOptimizer {

    private List<Player> roster;
    private List<Lineup> allLineups;

    public LineupOptimizer(List<Player> roster) {
        this.roster = new ArrayList<>(roster);
        this.allLineups = new ArrayList<>();
    }

    /**
     * Generate all possible 5-player combinations and evaluate each.
     * Time complexity: O(C(n,5)) — manageable for roster sizes <= 15
     */
    public void generateAllLineups() {
        allLineups.clear();
        int n = roster.size();
        List<List<Integer>> combinations = new ArrayList<>();
        generateCombinations(n, 5, 0, new ArrayList<>(), combinations);

        for (List<Integer> combo : combinations) {
            List<Player> group = combo.stream().map(roster::get).collect(Collectors.toList());
            allLineups.add(new Lineup(group));
        }

        // Sort by composite score descending
        allLineups.sort((a, b) -> Double.compare(b.getCompositeScore(), a.getCompositeScore()));
    }

    private void generateCombinations(int n, int k, int start,
                                      List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < n; i++) {
            current.add(i);
            generateCombinations(n, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Returns the top N lineups by composite score.
     */
    public List<Lineup> getTopLineups(int n) {
        if (allLineups.isEmpty()) generateAllLineups();
        return allLineups.subList(0, Math.min(n, allLineups.size()));
    }

    /**
     * Best lineup optimised for OFFENSE.
     */
    public Lineup getBestOffensiveLineup() {
        if (allLineups.isEmpty()) generateAllLineups();
        return allLineups.stream()
            .max(Comparator.comparingDouble(Lineup::getOffensiveRating))
            .orElseThrow();
    }

    /**
     * Best lineup optimised for DEFENSE.
     */
    public Lineup getBestDefensiveLineup() {
        if (allLineups.isEmpty()) generateAllLineups();
        return allLineups.stream()
            .max(Comparator.comparingDouble(Lineup::getDefensiveRating))
            .orElseThrow();
    }

    /**
     * Closing lineup: highest net rating for late-game situations.
     */
    public Lineup getClosingLineup() {
        if (allLineups.isEmpty()) generateAllLineups();
        return allLineups.stream()
            .max(Comparator.comparingDouble(Lineup::getNetRating))
            .orElseThrow();
    }

    /**
     * Fatigue-aware lineup: picks best lineup where all players have stamina > threshold.
     */
    public Optional<Lineup> getFreshLineup(double staminaThreshold) {
        if (allLineups.isEmpty()) generateAllLineups();
        return allLineups.stream()
            .filter(l -> l.getPlayers().stream()
                .allMatch(p -> p.getCurrentStamina() >= staminaThreshold))
            .findFirst();
    }

    /**
     * Simulate N game scenarios (Q1-offensive, Q2-balanced, Q3-defensive, Q4-closing)
     * and return recommended lineup per scenario.
     */
    public Map<String, Lineup> getScenarioLineups() {
        if (allLineups.isEmpty()) generateAllLineups();
        Map<String, Lineup> scenarios = new LinkedHashMap<>();
        scenarios.put("Q1 - Opening (Offensive)", getBestOffensiveLineup());
        scenarios.put("Q2 - Balanced", getTopLineups(1).get(0));
        scenarios.put("Q3 - Defensive", getBestDefensiveLineup());
        scenarios.put("Q4 - Closing", getClosingLineup());
        return scenarios;
    }

    /**
     * Run a Monte Carlo-style simulation: apply stamina degradation over 48 min,
     * then report which lineup holds up best per quarter.
     */
    public void runGameSimulation() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║         GAME SIMULATION — 48 Minute Model           ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        // Reset all staminas
        roster.forEach(Player::resetStamina);

        String[] quarters = {"Q1", "Q2", "Q3", "Q4"};
        String[] modes = {"Offensive", "Balanced", "Defensive", "Closing"};

        for (int q = 0; q < 4; q++) {
            generateAllLineups(); // Regenerate with current stamina values

            Lineup best = switch (modes[q]) {
                case "Offensive" -> getBestOffensiveLineup();
                case "Defensive" -> getBestDefensiveLineup();
                case "Closing"   -> getClosingLineup();
                default          -> getTopLineups(1).get(0);
            };

            System.out.printf("── %s [%s Mode] ──────────────────────────────────%n",
                quarters[q], modes[q]);
            System.out.println(best);

            // Degrade stamina for players in this lineup
            for (Player p : best.getPlayers()) {
                p.consumeStamina(10); // ~10 min per quarter
            }
            // Bench players recover slightly
            List<Player> bench = roster.stream()
                .filter(p -> !best.getPlayers().contains(p))
                .collect(Collectors.toList());
            bench.forEach(p -> p.restoreStamina(4));

            System.out.println();
        }
    }

    /**
     * Statistical summary of the roster.
     */
    public void printRosterStats() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║                  ROSTER OVERVIEW                    ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.printf("%-18s %-4s %6s %6s %6s%n", "Name", "Pos", "OVR", "OFF", "DEF");
        System.out.println("─".repeat(50));
        roster.stream()
            .sorted(Comparator.comparingDouble(Player::getOverallRating).reversed())
            .forEach(p -> System.out.printf("%-18s %-4s %6.1f %6.1f %6.1f%n",
                p.getName(), p.getPosition(),
                p.getOverallRating(), p.getOffensiveScore(), p.getDefensiveScore()));

        double avgOVR = roster.stream().mapToDouble(Player::getOverallRating).average().orElse(0);
        System.out.printf("%nRoster Average OVR: %.2f | Total Players: %d%n", avgOVR, roster.size());
    }

    public List<Lineup> getAllLineups() { return Collections.unmodifiableList(allLineups); }
    public List<Player> getRoster()     { return Collections.unmodifiableList(roster); }
    public int getTotalCombinations()   { return allLineups.size(); }
}