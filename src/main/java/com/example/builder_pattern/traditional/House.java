package com.example.builder_pattern.traditional;

/**
 * 房屋类 - 传统建造者模式的产品
 */
public class House {
    private String foundation; // 地基
    private String walls; // 墙壁
    private String roof; // 屋顶
    private String windows; // 窗户
    private String doors; // 门
    private String interior; // 内饰
    private String garden; // 花园
    private String garage; // 车库

    // Setters
    public void setFoundation(String foundation) {
        this.foundation = foundation;
    }

    public void setWalls(String walls) {
        this.walls = walls;
    }

    public void setRoof(String roof) {
        this.roof = roof;
    }

    public void setWindows(String windows) {
        this.windows = windows;
    }

    public void setDoors(String doors) {
        this.doors = doors;
    }

    public void setInterior(String interior) {
        this.interior = interior;
    }

    public void setGarden(String garden) {
        this.garden = garden;
    }

    public void setGarage(String garage) {
        this.garage = garage;
    }

    // Getters
    public String getFoundation() {
        return foundation;
    }

    public String getWalls() {
        return walls;
    }

    public String getRoof() {
        return roof;
    }

    public String getWindows() {
        return windows;
    }

    public String getDoors() {
        return doors;
    }

    public String getInterior() {
        return interior;
    }

    public String getGarden() {
        return garden;
    }

    public String getGarage() {
        return garage;
    }

    @Override
    public String toString() {
        return "House{" + "\n  地基='" + foundation + '\'' + "\n  墙壁='" + walls + '\'' + "\n  屋顶='" + roof + '\''
                + "\n  窗户='" + windows + '\'' + "\n  门='" + doors + '\'' + "\n  内饰='" + interior + '\'' + "\n  花园='"
                + garden + '\'' + "\n  车库='" + garage + '\'' + "\n}";
    }
}