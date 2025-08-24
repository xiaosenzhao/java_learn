package com.example.builder_pattern;

import com.example.builder_pattern.fluent.Pizza;
import com.example.builder_pattern.traditional.*;

import java.math.BigDecimal;

/**
 * 建造者模式演示类 展示不同类型的建造者模式实现
 */
public class BuilderPatternDemo {

    public static void main(String[] args) {
        System.out.println("================ 建造者模式演示 ================\n");

        // 1. 静态内部类建造者模式 - Computer
        demonstrateStaticBuilderPattern();

        // 2. 传统建造者模式 - House
        demonstrateTraditionalBuilderPattern();

        // 3. 流畅的链式建造者模式 - Pizza
        demonstrateFluentBuilderPattern();

        // 4. Lombok @Builder 注解示例
        demonstrateLombokBuilder();
    }

    /**
     * 演示静态内部类建造者模式
     */
    private static void demonstrateStaticBuilderPattern() {
        System.out.println("========== 1. 静态内部类建造者模式 ==========");

        // 构建一台基础电脑
        Computer basicComputer = new Computer.Builder("Intel i5", "8GB DDR4").hardDisk("256GB SSD")
                .operatingSystem("Windows 11").build();

        System.out.println("基础电脑配置：" + basicComputer);

        // 构建一台高端游戏电脑
        Computer gamingComputer = new Computer.Builder("Intel i9", "32GB DDR4").hardDisk("1TB NVMe SSD")
                .graphicsCard("RTX 4080").motherboard("高端游戏主板").powerSupply("750W金牌电源").computerCase("RGB游戏机箱")
                .monitor("32寸4K显示器").keyboard("机械键盘").mouse("游戏鼠标").operatingSystem("Windows 11 Pro").enableWifi()
                .enableBluetooth().build();

        System.out.println("\n高端游戏电脑配置：" + gamingComputer);
        System.out.println();
    }

    /**
     * 演示传统建造者模式
     */
    private static void demonstrateTraditionalBuilderPattern() {
        System.out.println("========== 2. 传统建造者模式 ==========");

        // 建造豪华别墅
        HouseBuilder villaBuilder = new VillaBuilder();
        HouseDirector director = new HouseDirector(villaBuilder);
        House villa = director.constructHouse();
        System.out.println("豪华别墅：" + villa);

        // 建造公寓
        HouseBuilder apartmentBuilder = new ApartmentBuilder();
        director = new HouseDirector(apartmentBuilder);
        House apartment = director.constructSimpleHouse();
        System.out.println("简约公寓：" + apartment);

        // 自定义建造流程
        House customHouse = director.constructCustomHouse(true, false);
        System.out.println("自定义房屋（有花园，无车库）：" + customHouse);
        System.out.println();
    }

    /**
     * 演示流畅的链式建造者模式
     */
    private static void demonstrateFluentBuilderPattern() {
        System.out.println("========== 3. 流畅的链式建造者模式 ==========");

        // 自定义披萨
        Pizza customPizza = new Pizza.Builder("大", "薄饼").addTopping("蘑菇").addTopping("青椒").addToppings("洋葱", "橄榄")
                .cheese("车达奶酪").sauce("白酱").withSpice().cookingTime(18).crust("厚边").build();

        System.out.println("自定义披萨：" + customPizza);

        // 使用预设配置 - 玛格丽特披萨
        Pizza margheritaPizza = new Pizza.Builder("中", "传统").margherita().build();

        System.out.println("\n玛格丽特披萨：" + margheritaPizza);

        // 使用预设配置 - 夏威夷披萨
        Pizza hawaiianPizza = new Pizza.Builder("大", "厚饼").hawaiian().cheese("双倍奶酪").cookingTime(20).build();

        System.out.println("\n夏威夷披萨：" + hawaiianPizza);

        // 使用预设配置 - 肉食者披萨
        Pizza meatLoversPizza = new Pizza.Builder("特大", "厚饼").meatLovers().withSpice().crust("芝士边").build();

        System.out.println("\n肉食者披萨：" + meatLoversPizza);
        System.out.println();
    }

    /**
     * 演示Lombok @Builder注解
     */
    private static void demonstrateLombokBuilder() {
        System.out.println("========== 4. Lombok @Builder 注解 ==========");
        System.out.println("注意：Lombok需要IDE支持或编译时处理才能正常工作");
        System.out.println("以下代码展示了Lombok @Builder的用法：");

        System.out.println("// 基础产品");
        System.out.println("Product basicProduct = Product.builder()");
        System.out.println("        .name(\"iPhone 15\")");
        System.out.println("        .category(\"智能手机\")");
        System.out.println("        .build();");
        System.out.println();
        System.out.println("// 完整产品信息");
        System.out.println("Product fullProduct = Product.builder()");
        System.out.println("        .name(\"MacBook Pro\")");
        System.out.println("        .category(\"笔记本电脑\")");
        System.out.println("        .price(new BigDecimal(\"15999.00\"))");
        System.out.println("        .description(\"14英寸MacBook Pro\")");
        System.out.println("        .brand(\"Apple\")");
        System.out.println("        .color(\"深空灰色\")");
        System.out.println("        .size(\"14英寸\")");
        System.out.println("        .weight(1580)");
        System.out.println("        .isAvailable(true)");
        System.out.println("        .feature(\"M3 Pro芯片\")");
        System.out.println("        .feature(\"Liquid Retina XDR显示屏\")");
        System.out.println("        .feature(\"18小时电池续航\")");
        System.out.println("        .addTag(\"高性能\")");
        System.out.println("        .addTag(\"专业级\")");
        System.out.println("        .addTag(\"创作工具\")");
        System.out.println("        .manufacturer(\"苹果公司\")");
        System.out.println("        .model(\"MacBook Pro 14 2023\")");
        System.out.println("        .warrantyMonths(12)");
        System.out.println("        .build();");
        System.out.println();
        System.out.println("// 使用静态工厂方法");
        System.out.println("Product simpleProduct = Product.createBasic(\"iPad Air\", \"平板电脑\");");

        System.out.println("Lombok @Builder特点：");
        System.out.println("- 自动生成Builder类");
        System.out.println("- 支持@Singular注解处理集合");
        System.out.println("- 支持@NonNull验证");
        System.out.println("- 可以与@Data、@Value等注解组合使用");
        System.out.println("- 减少样板代码，提高开发效率");
        System.out.println();
    }
}