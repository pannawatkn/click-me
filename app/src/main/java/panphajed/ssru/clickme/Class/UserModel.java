package panphajed.ssru.clickme.Class;

public class UserModel {
    private String name;
    private int score;
    private int max_combo;

    public UserModel(){}

    public UserModel(String name, int score, int max_combo) {
        this.name = name;
        this.score = score;
        this.max_combo = max_combo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMax_combo() {
        return max_combo;
    }

    public void setMax_combo(int max_combo) {
        this.max_combo = max_combo;
    }
}
