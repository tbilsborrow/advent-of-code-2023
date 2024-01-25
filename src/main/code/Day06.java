class Day06 {
    static long calculateDistance(long raceDuration, int buttonDuration) {
        return ((raceDuration - buttonDuration) * buttonDuration);
    }

    static long calculateNumWaysToWin(long raceDuration, long record) {
        for (int i = 0; i < raceDuration / 2; i++) {
            if (calculateDistance(raceDuration, i) > record) {
                long n = (((raceDuration + 1) / 2) - i) * 2;
                n = raceDuration % 2 == 0 ? n + 1 : n;
                return n;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        // numbers hard coded from input-06.txt

        long part1 = calculateNumWaysToWin(61, 643)
                * calculateNumWaysToWin(70, 1184)
                * calculateNumWaysToWin(90, 1362)
                * calculateNumWaysToWin(66, 1041);
        System.out.println("Part 1: " + part1);

        long part2 = calculateNumWaysToWin(61709066L, 643118413621041L);
        System.out.println("Part 2: " + part2);
    }
}
