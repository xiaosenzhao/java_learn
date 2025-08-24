package com.example.builder_pattern;

/**
 * 计算机产品类 作为建造者模式要构建的复杂对象
 */
public class Computer {
    // 必需参数
    private String cpu;
    private String memory;

    // 可选参数
    private String hardDisk;
    private String graphicsCard;
    private String motherboard;
    private String powerSupply;
    private String computerCase;
    private String monitor;
    private String keyboard;
    private String mouse;
    private String operatingSystem;
    private boolean hasWifi;
    private boolean hasBluetooth;
    private boolean hasOpticalDrive;

    // 私有构造函数，只能通过Builder创建
    private Computer(Builder builder) {
        this.cpu = builder.cpu;
        this.memory = builder.memory;
        this.hardDisk = builder.hardDisk;
        this.graphicsCard = builder.graphicsCard;
        this.motherboard = builder.motherboard;
        this.powerSupply = builder.powerSupply;
        this.computerCase = builder.computerCase;
        this.monitor = builder.monitor;
        this.keyboard = builder.keyboard;
        this.mouse = builder.mouse;
        this.operatingSystem = builder.operatingSystem;
        this.hasWifi = builder.hasWifi;
        this.hasBluetooth = builder.hasBluetooth;
        this.hasOpticalDrive = builder.hasOpticalDrive;
    }

    // 静态内部Builder类
    public static class Builder {
        // 必需参数
        private String cpu;
        private String memory;

        // 可选参数 - 初始化为默认值
        private String hardDisk = "无硬盘";
        private String graphicsCard = "集成显卡";
        private String motherboard = "标准主板";
        private String powerSupply = "标准电源";
        private String computerCase = "标准机箱";
        private String monitor = "无显示器";
        private String keyboard = "标准键盘";
        private String mouse = "标准鼠标";
        private String operatingSystem = "无操作系统";
        private boolean hasWifi = false;
        private boolean hasBluetooth = false;
        private boolean hasOpticalDrive = false;

        // 必需参数的构造函数
        public Builder(String cpu, String memory) {
            this.cpu = cpu;
            this.memory = memory;
        }

        // 可选参数的设置方法（链式调用）
        public Builder hardDisk(String hardDisk) {
            this.hardDisk = hardDisk;
            return this;
        }

        public Builder graphicsCard(String graphicsCard) {
            this.graphicsCard = graphicsCard;
            return this;
        }

        public Builder motherboard(String motherboard) {
            this.motherboard = motherboard;
            return this;
        }

        public Builder powerSupply(String powerSupply) {
            this.powerSupply = powerSupply;
            return this;
        }

        public Builder computerCase(String computerCase) {
            this.computerCase = computerCase;
            return this;
        }

        public Builder monitor(String monitor) {
            this.monitor = monitor;
            return this;
        }

        public Builder keyboard(String keyboard) {
            this.keyboard = keyboard;
            return this;
        }

        public Builder mouse(String mouse) {
            this.mouse = mouse;
            return this;
        }

        public Builder operatingSystem(String operatingSystem) {
            this.operatingSystem = operatingSystem;
            return this;
        }

        public Builder enableWifi() {
            this.hasWifi = true;
            return this;
        }

        public Builder enableBluetooth() {
            this.hasBluetooth = true;
            return this;
        }

        public Builder enableOpticalDrive() {
            this.hasOpticalDrive = true;
            return this;
        }

        // 构建最终的Computer对象
        public Computer build() {
            return new Computer(this);
        }
    }

    // Getters
    public String getCpu() {
        return cpu;
    }

    public String getMemory() {
        return memory;
    }

    public String getHardDisk() {
        return hardDisk;
    }

    public String getGraphicsCard() {
        return graphicsCard;
    }

    public String getMotherboard() {
        return motherboard;
    }

    public String getPowerSupply() {
        return powerSupply;
    }

    public String getComputerCase() {
        return computerCase;
    }

    public String getMonitor() {
        return monitor;
    }

    public String getKeyboard() {
        return keyboard;
    }

    public String getMouse() {
        return mouse;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public boolean hasWifi() {
        return hasWifi;
    }

    public boolean hasBluetooth() {
        return hasBluetooth;
    }

    public boolean hasOpticalDrive() {
        return hasOpticalDrive;
    }

    @Override
    public String toString() {
        return "Computer{" + "\n  CPU='" + cpu + '\'' + "\n  内存='" + memory + '\'' + "\n  硬盘='" + hardDisk + '\''
                + "\n  显卡='" + graphicsCard + '\'' + "\n  主板='" + motherboard + '\'' + "\n  电源='" + powerSupply + '\''
                + "\n  机箱='" + computerCase + '\'' + "\n  显示器='" + monitor + '\'' + "\n  键盘='" + keyboard + '\''
                + "\n  鼠标='" + mouse + '\'' + "\n  操作系统='" + operatingSystem + '\'' + "\n  WiFi=" + hasWifi + "\n  蓝牙="
                + hasBluetooth + "\n  光驱=" + hasOpticalDrive + "\n}";
    }
}