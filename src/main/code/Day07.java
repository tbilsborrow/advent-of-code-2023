import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

enum HandType { Five, Four, FullHouse, Three, TwoPair, OnePair, HighCard }

class Hand {
    String cards;
    int bid;
    private final HandType type;
    private final HandType typeWithJokers;

    private static final Map<Character, Integer> values = Map.ofEntries(
            new AbstractMap.SimpleEntry<>('A', 14),
            new AbstractMap.SimpleEntry<>('K', 13),
            new AbstractMap.SimpleEntry<>('Q', 12),
            new AbstractMap.SimpleEntry<>('J', 11),
            new AbstractMap.SimpleEntry<>('T', 10),
            new AbstractMap.SimpleEntry<>('9', 9),
            new AbstractMap.SimpleEntry<>('8', 8),
            new AbstractMap.SimpleEntry<>('7', 7),
            new AbstractMap.SimpleEntry<>('6', 6),
            new AbstractMap.SimpleEntry<>('5', 5),
            new AbstractMap.SimpleEntry<>('4', 4),
            new AbstractMap.SimpleEntry<>('3', 3),
            new AbstractMap.SimpleEntry<>('2', 2)
    );

    private static final Map<Character, Integer> valuesWithJokers = Map.ofEntries(
            new AbstractMap.SimpleEntry<>('A', 14),
            new AbstractMap.SimpleEntry<>('K', 13),
            new AbstractMap.SimpleEntry<>('Q', 12),
            new AbstractMap.SimpleEntry<>('T', 10),
            new AbstractMap.SimpleEntry<>('9', 9),
            new AbstractMap.SimpleEntry<>('8', 8),
            new AbstractMap.SimpleEntry<>('7', 7),
            new AbstractMap.SimpleEntry<>('6', 6),
            new AbstractMap.SimpleEntry<>('5', 5),
            new AbstractMap.SimpleEntry<>('4', 4),
            new AbstractMap.SimpleEntry<>('3', 3),
            new AbstractMap.SimpleEntry<>('2', 2),
            new AbstractMap.SimpleEntry<>('J', 1)
    );

    private static final Map<String, HandType> handTypes = Map.of(
            "11111", HandType.Five,
            "11112", HandType.Four,
            "11122", HandType.FullHouse,
            "11123", HandType.Three,
            "11223", HandType.TwoPair,
            "11234", HandType.OnePair,
            "12345", HandType.HighCard
    );

    public Hand(String cards, int bid) {
        this.cards = cards;
        this.bid = bid;
        this.type = determineType(cards, false);
        this.typeWithJokers = determineType(cards, true);
    }

    private HandType determineType(String cards, boolean withJokers) {
        // tiny histogram of counts per card
        final Map<Character, AtomicInteger> histo = new HashMap<>();
        for (int c = 0; c < cards.length(); c++) {
            histo.computeIfAbsent(cards.charAt(c), k-> new AtomicInteger(0)).incrementAndGet();
        }

        // sort histogram by count descending
        List<Map.Entry<Character, AtomicInteger>> entries = histo.entrySet().stream().sorted(
                Collections.reverseOrder(
                        Map.Entry.comparingByValue(Comparator.comparingInt(AtomicInteger::get))
                )).toList();

        if (withJokers && histo.containsKey('J') && histo.size() > 1) {
            // any jokers are treated as more of the most common card
            final int numJokers = histo.get('J').get();
            final List<Map.Entry<Character, AtomicInteger>> mutableList = new ArrayList<>(entries);
            final int j = IntStream.range(0, entries.size()).filter(i -> mutableList.get(i).getKey() == 'J').findFirst().getAsInt();
            mutableList.remove(j);
            mutableList.get(0).getValue().addAndGet(numJokers);

            // and resort
            entries = mutableList.stream().sorted(
                Collections.reverseOrder(
                        Map.Entry.comparingByValue(Comparator.comparingInt(AtomicInteger::get))
                )).toList();
        }

        // build string representative of the hand structure (ignoring card values,
        // only showing runs of matching cards) - e.g. five of a kind is "11111", any
        // full house is "11122", etc
        int x = 1;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, AtomicInteger> entry : entries) {
            sb.append(String.valueOf(x).repeat(entry.getValue().get()));
            x += 1;
        }

        return handTypes.get(sb.toString());
    }

    // sort by hand value ascending
    static final Comparator<Hand> part1Comparator = (o1, o2) -> {
        if (o1.type.compareTo(o2.type) > 0) return -1;
        if (o1.type.compareTo(o2.type) < 0) return 1;
        for (int i = 0; i < o1.cards.length(); i++) {
            int c = values.get(o1.cards.charAt(i)).compareTo(values.get(o2.cards.charAt(i)));
            if (c != 0) return c;
        }
        return 0;
    };

    static final Comparator<Hand> part2Comparator = (o1, o2) -> {
        if (o1.typeWithJokers.compareTo(o2.typeWithJokers) > 0) return -1;
        if (o1.typeWithJokers.compareTo(o2.typeWithJokers) < 0) return 1;
        for (int i = 0; i < o1.cards.length(); i++) {
            int c = valuesWithJokers.get(o1.cards.charAt(i)).compareTo(valuesWithJokers.get(o2.cards.charAt(i)));
            if (c != 0) return c;
        }
        return 0;
    };
}

public class Day07 {
    public static void main(String[] args) throws IOException {
        final List<String> lines = Files.readAllLines(Paths.get("src/main/resources/input-07.txt"));
//        final List<String> lines = Files.readAllLines(Paths.get("src/main/resources/test.txt"));

        final List<Hand> hands = lines.stream().map(s -> {
            final String[] tokens = s.split(" ");
            return new Hand(tokens[0], Integer.parseInt(tokens[1]));
        }).toList();

        final List<Hand> hands1 = hands.stream().sorted(Hand.part1Comparator).toList();
        int part1 = IntStream.range(0, hands1.size()).map(i -> hands1.get(i).bid * (i+1)).sum();
        System.out.println("Part 1: " + part1);

        final List<Hand> hands2 = hands.stream().sorted(Hand.part2Comparator).toList();
        int part2 = IntStream.range(0, hands2.size()).map(i -> hands2.get(i).bid * (i+1)).sum();
        System.out.println("Part 2: " + part2);
    }
}
