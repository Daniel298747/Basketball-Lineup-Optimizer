package basketball;

/**
 * Represents a basketball player with performance metrics.
 */
public class Player {
    private String name;
    private String position; // PG, SG, SF, PF, C
    private double scoring;     // 0-100
    private double defense;     // 0-100
    private double rebounding;  // 0-100
    private double playmaking;  // 0-100
    private double athleticism; // 0-100
    private double threePoint;  // 0-100
    private double stamina;     // 0-100 (degrades with minutes)
    private double currentStamina;

    public Player(String name, String position, double scoring, double defense,
                  double rebounding, double playmaking, double athleticism,
                  double threePoint, double stamina) {
        this.name = name;
        this.position = position;
        this.scoring = scoring;
        this.defense = defense;
        this.rebounding = rebounding;
        this.playmaking = playmaking;
        this.athleticism = athleticism;
        this.threePoint = threePoint;
        this.stamina = stamina;
        this.currentStamina = stamina;
    }

    /**
     * Overall Rating (OVR) — weighted composite of all attributes.
     */
    public double getOverallRating() {
        return (scoring * 0.25) + (defense * 0.20) + (rebounding * 0.15)
             + (playmaking * 0.15) + (athleticism * 0.10)
             + (threePoint * 0.10) + (currentStamina * 0.05);
    }

    /**
     * Offensive contribution score.
     */
    public double getOffensiveScore() {
        return (scoring * 0.40) + (playmaking * 0.30)
             + (threePoint * 0.20) + (athleticism * 0.10);
    }

    /**
     * Defensive contribution score.
     */
    public double getDefensiveScore() {
        return (defense * 0.50) + (rebounding * 0.30) + (athleticism * 0.20);
    }

    /**
     * Degrade stamina based on minutes played (non-linear fatigue model).
     */
    public void consumeStamina(double minutes) {
        double fatigueFactor = 1.0 + (minutes / 48.0); // More fatigue in higher-minute games
        currentStamina = Math.max(0, currentStamina - (minutes * fatigueFactor * 0.8));
    }

    public void restoreStamina(double minutes) {
        currentStamina = Math.min(stamina, currentStamina + minutes * 1.2);
    }

    public void resetStamina() {
        this.currentStamina = stamina;
    }

    // Getters
    public String getName() { return name; }
    public String getPosition() { return position; }
    public double getScoring() { return scoring; }
    public double getDefense() { return defense; }
    public double getRebounding() { return rebounding; }
    public double getPlaymaking() { return playmaking; }
    public double getAthleticism() { return athleticism; }
    public double getThreePoint() { return threePoint; }
    public double getStamina() { return stamina; }
    public double getCurrentStamina() { return currentStamina; }

    @Override
    public String toString() {
        return String.format("%-18s [%s] OVR: %.1f | OFF: %.1f | DEF: %.1f",
            name, position, getOverallRating(), getOffensiveScore(), getDefensiveScore());
    }
}