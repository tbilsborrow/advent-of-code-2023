import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Day08 {
    static class Node {
        final String label;
        Node left;
        Node right;

        public Node(String label) {
            this.label = label;
        }
    }

    static class Input {
        final String instructions;
        Node head;
        final Map<String, Node> nodes = new HashMap<>();

        public Input(String instructions) {
            this.instructions = instructions;
        }
    }

    static Input parseNodeInput(List<String> lines) {
        final Input input = new Input(lines.get(0));

        for (int i = 2; i < lines.size(); i++) {
            final String l = lines.get(i);
            final String label = l.split(" = ")[0];
            final String[] paths = l.split(" = ")[1].replaceAll("^\\(|\\)$", "").split(", ");

            final Node node = input.nodes.computeIfAbsent(label, Node::new);
            if (input.head == null && label.equals("AAA")) input.head = node;

            node.left = input.nodes.computeIfAbsent(paths[0], Node::new);
            node.right = input.nodes.computeIfAbsent(paths[1], Node::new);
        }
        return input;
    }

    // follow instructions from start node until endCondition is met
    static int navigate(String instructions, Node start, Function<Node, Boolean> endCondition) {
        int n = 0;
        Node currNode = start;
        int count = 0;
        while(!endCondition.apply(currNode)) {
            currNode = (instructions.charAt(n) == 'L') ? currNode.left : currNode.right;

            // cycle over instructions
            if (++n >= instructions.length()) n = 0;
            count++;
        }
        return count;
    }

    // brute force find the least common multiple
    // increment by max until all numbers evenly divide, not pretty but fast enough
    static long findLcm(List<Integer> numbers) {
        final int max = Collections.max(numbers);
        long lcm = max;
        boolean found = false;
        while (!found) {
            found = true;
            for (int num : numbers) {
                if (lcm % num != 0) {
                    found = false;
                    break;
                }
            }
            if (!found) lcm += max;
        }
        return lcm;
    }

    static long part2(Input input) {
        final List<Node> startNodes = input.nodes.entrySet().stream()
                .filter(e -> e.getKey().endsWith("A"))
                .map(Map.Entry::getValue).toList();

        // find how long each start node's path to its finish is
        final List<Integer> factors = startNodes.stream()
                .map(n -> navigate(input.instructions, n, node -> node.label.endsWith("Z"))).toList();

        // then find the LCM, this is the first point at which all paths end together
        return findLcm(factors);
    }

    public static void main(String[] args) throws IOException {
        final List<String> lines = Files.readAllLines(Paths.get("src/main/resources/input-08.txt"));
//        final List<String> lines = Files.readAllLines(Paths.get("src/main/resources/test.txt"));

        final Input input = parseNodeInput(lines);

        final int part1 = navigate(input.instructions, input.head, n -> n.label.equals("ZZZ"));
        System.out.println("Part 1: " + part1);

        System.out.println("Part 2: " + part2(input));
    }
}
