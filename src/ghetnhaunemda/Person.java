/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ghetnhaunemda;

/**
 *
 * @author Le Tan Kim - CE170469
 */
public class Person {

    private int hp;
    private int score;
    private int x;
    private int y;

    public Person() {
    }

    public Person(int hp, int x, int y, int score) {
        this.hp = hp;
        this.x = x;
        this.y = y;
        this.score = score;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
