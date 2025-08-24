package com.example.builder_pattern.traditional;

/**
 * 公寓建造者 具体建造者实现
 */
public class ApartmentBuilder extends HouseBuilder {

    @Override
    public void buildFoundation() {
        house.setFoundation("标准混凝土基础");
        System.out.println("建造公寓的标准混凝土基础");
    }

    @Override
    public void buildWalls() {
        house.setWalls("标准砖墙");
        System.out.println("建造公寓的标准砖墙");
    }

    @Override
    public void buildRoof() {
        house.setRoof("平顶屋顶");
        System.out.println("建造公寓的平顶屋顶");
    }

    @Override
    public void buildWindows() {
        house.setWindows("双层玻璃窗");
        System.out.println("安装公寓的双层玻璃窗");
    }

    @Override
    public void buildDoors() {
        house.setDoors("标准防盗门");
        System.out.println("安装公寓的标准防盗门");
    }

    @Override
    public void buildInterior() {
        house.setInterior("简约现代装修");
        System.out.println("进行公寓的简约现代装修");
    }

    @Override
    public void buildGarden() {
        house.setGarden("阳台小花园");
        System.out.println("建造公寓的阳台小花园");
    }

    @Override
    public void buildGarage() {
        house.setGarage("地下停车位");
        System.out.println("分配公寓的地下停车位");
    }
}