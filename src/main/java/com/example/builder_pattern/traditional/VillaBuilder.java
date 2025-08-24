package com.example.builder_pattern.traditional;

/**
 * 豪华别墅建造者 具体建造者实现
 */
public class VillaBuilder extends HouseBuilder {

    @Override
    public void buildFoundation() {
        house.setFoundation("钢筋混凝土深基础");
        System.out.println("建造豪华别墅的钢筋混凝土深基础");
    }

    @Override
    public void buildWalls() {
        house.setWalls("双层保温砖墙");
        System.out.println("建造豪华别墅的双层保温砖墙");
    }

    @Override
    public void buildRoof() {
        house.setRoof("欧式斜坡瓦屋顶");
        System.out.println("建造豪华别墅的欧式斜坡瓦屋顶");
    }

    @Override
    public void buildWindows() {
        house.setWindows("三层玻璃落地窗");
        System.out.println("安装豪华别墅的三层玻璃落地窗");
    }

    @Override
    public void buildDoors() {
        house.setDoors("实木雕花大门");
        System.out.println("安装豪华别墅的实木雕花大门");
    }

    @Override
    public void buildInterior() {
        house.setInterior("欧式豪华装修");
        System.out.println("进行豪华别墅的欧式豪华装修");
    }

    @Override
    public void buildGarden() {
        house.setGarden("私人花园泳池");
        System.out.println("建造豪华别墅的私人花园泳池");
    }

    @Override
    public void buildGarage() {
        house.setGarage("双车位车库");
        System.out.println("建造豪华别墅的双车位车库");
    }
}