public class Main {
    public static void main(String[] args) {
        generateGrid(5, 10);
    }

    public static void generateGrid(int gridSize, int bombCount) {
        System.out.println(String.format("Bomb count must be strictly less than Grid size. Current values: bomb count = %1$d, gridSize = %2$d", bombCount, gridSize));
//        new MSGrid().generateGrid(gridSize, bombCount);
    }

    public static <T> void print(String prefix, T toPrint) {
        System.out.println(prefix + ": " + toPrint);
    }
}