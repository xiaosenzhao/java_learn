package com.example.builder_pattern.traditional;

/**
 * 指挥者类 负责控制建造过程的顺序
 */
public class HouseDirector {
    private HouseBuilder houseBuilder;

    public HouseDirector(HouseBuilder houseBuilder) {
        this.houseBuilder = houseBuilder;
    }

    /**
     * 构建一个完整的房屋（标准流程）
     */
    public House constructHouse() {
        System.out.println("开始建造房屋...");
        houseBuilder.createNewHouse();
        houseBuilder.buildFoundation();
        houseBuilder.buildWalls();
        houseBuilder.buildRoof();
        houseBuilder.buildWindows();
        houseBuilder.buildDoors();
        houseBuilder.buildInterior();
        houseBuilder.buildGarden();
        houseBuilder.buildGarage();
        System.out.println("房屋建造完成！\n");
        return houseBuilder.getHouse();
    }

    /**
     * 构建简化版房屋（不包含花园和车库）
     */
    public House constructSimpleHouse() {
        System.out.println("开始建造简化版房屋...");
        houseBuilder.createNewHouse();
        houseBuilder.buildFoundation();
        houseBuilder.buildWalls();
        houseBuilder.buildRoof();
        houseBuilder.buildWindows();
        houseBuilder.buildDoors();
        houseBuilder.buildInterior();
        System.out.println("简化版房屋建造完成！\n");
        return houseBuilder.getHouse();
    }

    /**
     * 自定义建造流程
     */
    public House constructCustomHouse(boolean includeGarden, boolean includeGarage) {
        System.out.println("开始建造自定义房屋...");
        houseBuilder.createNewHouse();
        houseBuilder.buildFoundation();
        houseBuilder.buildWalls();
        houseBuilder.buildRoof();
        houseBuilder.buildWindows();
        houseBuilder.buildDoors();
        houseBuilder.buildInterior();

        if (includeGarden) {
            houseBuilder.buildGarden();
        }
        if (includeGarage) {
            houseBuilder.buildGarage();
        }

        System.out.println("自定义房屋建造完成！\n");
        return houseBuilder.getHouse();
    }
}