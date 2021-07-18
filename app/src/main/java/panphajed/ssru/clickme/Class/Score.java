package panphajed.ssru.clickme.Class;

public class Score {

    private String rank, name, score;

    public Score() {
    }

    public Score(String rank, String name, String score) {

        this.rank = rank;
        this.name = name;
        this.score = score;

    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}