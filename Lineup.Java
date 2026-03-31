package basketball;

import java.util.*;

/**
 * Represents a 5-player lineup and calculates synergy bonuses.
 */
public class Lineup {

    private List<Player> players;
    private double synergyBonus;

    public Lineup(List<Player> players) {
        if (players.size() != 5)
            throw new IllegalArgumentException("A lineup must have exactly 5 players.");
        this.players = new ArrayList<>(players);
        this.synergyBonus = calculateSynergy();
    }

    /**
     * Synergy model: rewards positional balance, complementary skills,
     * and compatible playstyle pairs.
     */
    private double calculateSynergy() {
        double synergy = 0.0;

        // 1. Positional balance bonus (each unique position adds 2 pts)
        Set<String> positions = new HashSet<>();
        for (Player p : players) positions.add(p.getPosition());
        synergy += positions.size() * 2.0; // max 10 for 5 unique positions

        // 2. Playmaking + Scoring synergy: PG/SG with high playmaking boosts scorers
        double avgPlaymaking = players.stream().mapToDouble(Player::getPlaymaking).average().orElse(0);
        double avgScoring = players.stream().mapToDouble(Player::getScoring).average().orElse(0);
        if (avgPlaymaking > 70 && avgScoring > 75) synergy += 6.0;
        else if (avgPlaymaking > 60 && avgScoring > 65) synergy += 3.0;

        // 3. Spacing bonus: 3+ players with 3PT > 65 enables floor spacing
        long sharpshooters = players.stream().filter(p -> p.getThreePoint() > 65).count();
        synergy += sharpshooters * 2.5;

        // 4. Two-way penalty: teams with low average defense suffer synergy loss
        double avgDefense = players.stream().mapToDouble(Player::getDefense).average().orElse(0);
        if (avgDefense < 55) synergy -= 5.0;

        // 5. Stamina coherence: lineup of tired players is penalised
        double avgStamina = players.stream().mapToDouble(Player::getCurrentStamina).average().orElse(0);
        if (avgStamina < 40) synergy -= 4.0;

        // 6. Star power: having one player with OVR > 85 adds spark
        boolean hasStar = players.stream().anyMatch(p -> p.getOverallRating() > 85);
        if (hasStar) synergy += 4.0;

        return Math.max(0, synergy);
    }

    /**
     * Total lineup offensive rating (per-100 possessions style).
     */
    public double getOffensiveRating() {
        double raw = players.stream().mapToDouble(Player::getOffensiveScore).sum();
        return (raw / 5.0) + (synergyBonus * 0.4);
    }

    /**
     * Total lineup defensive rating.
     */
    public double getDefensiveRating() {
        double raw = players.stream().mapToDouble(Player::getDefensiveScore).sum();
        return (raw / 5.0) + (synergyBonus * 0.3);
    }

    /**
     * Net Rating = Offensive Rating - Defensive Rating (higher is better).
     * This mirrors the NBA's net rating concept.
     */
    public double getNetRating() {
        return getOffensiveRating() - getDefensiveRating();
    }

    /**
     * Composite lineup score for optimisation ranking.
     */
    public double getCompositeScore() {
        return (getOffensiveRating() * 0.5) + (getDefensiveRating() * 0.35) + (synergyBonus * 0.15);
    }

    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }
    public double getSynergyBonus() { return synergyBonus; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("┌─────────────────────────────────────────────────────┐\n");
        sb.append(String.format("│  Lineup Score: %-6.2f | Synergy Bonus: +%-5.1f        │\n",
            getCompositeScore(), synergyBonus));
        sb.append(String.format("│  OFF Rating: %-6.2f | DEF Rating: %-6.2f            │\n",
            getOffensiveRating(), getDefensiveRating()));
        sb.append("├─────────────────────────────────────────────────────┤\n");
        for (Player p : players) {
            sb.append(String.format("│  %s%n", p));
        }
        sb.append("└─────────────────────────────────────────────────────┘");
        return sb.toString();
    }
}