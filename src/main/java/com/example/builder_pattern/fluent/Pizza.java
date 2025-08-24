package com.example.builder_pattern.fluent;

import java.util.ArrayList;
import java.util.List;

/**
 * 披萨类 - 使用流畅的链式建造者模式
 */
public class Pizza {
    // 必需参数
    private final String size;
    private final String dough;

    // 可选参数
    private final List<String> toppings;
    private final String cheese;
    private final String sauce;
    private final boolean hasSpice;
    private final int cookingTime;
    private final String crust;

    private Pizza(Builder builder) {
        this.size = builder.size;
        this.dough = builder.dough;
        this.toppings = new ArrayList<>(builder.toppings);
        this.cheese = builder.cheese;
        this.sauce = builder.sauce;
        this.hasSpice = builder.hasSpice;
        this.cookingTime = builder.cookingTime;
        this.crust = builder.crust;
    }

    /**
     * 静态建造者类
     */
    public static class Builder {
        // 必需参数
        private final String size;
        private final String dough;

        // 可选参数
        private List<String> toppings = new ArrayList<>();
        private String cheese = "马苏里拉奶酪";
        private String sauce = "番茄酱";
        private boolean hasSpice = false;
        private int cookingTime = 15;
        private String crust = "薄脆";

        public Builder(String size, String dough) {
            this.size = size;
            this.dough = dough;
        }

        public Builder addTopping(String topping) {
            this.toppings.add(topping);
            return this;
        }

        public Builder addToppings(String... toppings) {
            for (String topping : toppings) {
                this.toppings.add(topping);
            }
            return this;
        }

        public Builder cheese(String cheese) {
            this.cheese = cheese;
            return this;
        }

        public Builder sauce(String sauce) {
            this.sauce = sauce;
            return this;
        }

        public Builder withSpice() {
            this.hasSpice = true;
            return this;
        }

        public Builder cookingTime(int minutes) {
            this.cookingTime = minutes;
            return this;
        }

        public Builder crust(String crust) {
            this.crust = crust;
            return this;
        }

        // 预设配置方法 - 玛格丽特披萨
        public Builder margherita() {
            this.cheese = "新鲜马苏里拉";
            this.sauce = "新鲜番茄酱";
            this.toppings.clear();
            this.toppings.add("新鲜罗勒叶");
            return this;
        }

        // 预设配置方法 - 夏威夷披萨
        public Builder hawaiian() {
            this.toppings.clear();
            this.toppings.add("火腿");
            this.toppings.add("菠萝");
            return this;
        }

        // 预设配置方法 - 肉食者披萨
        public Builder meatLovers() {
            this.toppings.clear();
            this.toppings.add("意大利辣香肠");
            this.toppings.add("培根");
            this.toppings.add("火腿");
            this.toppings.add("牛肉");
            this.cookingTime = 20;
            return this;
        }

        public Pizza build() {
            // 验证必需参数
            if (size == null || size.trim().isEmpty()) {
                throw new IllegalArgumentException("披萨尺寸不能为空");
            }
            if (dough == null || dough.trim().isEmpty()) {
                throw new IllegalArgumentException("披萨面团类型不能为空");
            }

            return new Pizza(this);
        }
    }

    // Getters
    public String getSize() {
        return size;
    }

    public String getDough() {
        return dough;
    }

    public List<String> getToppings() {
        return new ArrayList<>(toppings);
    }

    public String getCheese() {
        return cheese;
    }

    public String getSauce() {
        return sauce;
    }

    public boolean hasSpice() {
        return hasSpice;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public String getCrust() {
        return crust;
    }

    @Override
    public String toString() {
        return "Pizza{" + "\n  尺寸='" + size + '\'' + "\n  面团='" + dough + '\'' + "\n  配料=" + toppings + "\n  奶酪='"
                + cheese + '\'' + "\n  酱料='" + sauce + '\'' + "\n  是否辣=" + hasSpice + "\n  烘烤时间=" + cookingTime + "分钟"
                + "\n  饼边='" + crust + '\'' + "\n}";
    }
}