package com.example.builder_pattern.traditional;

/**
 * 抽象建造者接口 定义创建产品各个部件的方法
 */
public abstract class HouseBuilder {
    protected House house;

    // 创建一个新的产品实例
    public void createNewHouse() {
        house = new House();
    }

    // 获取构建的产品
    public House getHouse() {
        return house;
    }

    // 抽象方法，子类必须实现
    public abstract void buildFoundation();

    public abstract void buildWalls();

    public abstract void buildRoof();

    public abstract void buildWindows();

    public abstract void buildDoors();

    public abstract void buildInterior();

    public abstract void buildGarden();

    public abstract void buildGarage();
}