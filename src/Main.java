import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> data = new ArrayList<>();
        Map<String, List<Integer>> map = new HashMap<>();

        if (args[0].equals("--data"))
            importPeople(args[1], map, data);

        Scanner scanner = new Scanner(System.in);

        String input;
        while (true) {
            printMenu();

            input = scanner.nextLine();
            System.out.println();

            switch (input) {
                case "1":
                    find(scanner, map, data);
                    break;
                case "2":
                    printPeople(data);
                    break;
                case "0":
                    exit();
                    return;
                default:
                    System.out.println("Incorrect option! Try again.");
            }
        }
    }

    public static void importPeople(String filename, Map<String, List<Integer>> map, List<String> data) {
        File file = new File(filename);
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNext()) {
                data.add(fileScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < data.size(); i++) {
            List<String> line = parseLine(data.get(i));
            for (String s : line) {
                String str = s.toLowerCase();
                if (!map.containsKey(str)) {
                    List<Integer> tmp = new ArrayList<>();
                    tmp.add(i);
                    map.put(str, tmp);
                } else {
                    List<Integer> tmp = map.get(str);
                    tmp.add(i);
                    map.put(str, tmp);
                }
            }
        }
    }

    public static void find(Scanner scanner, Map<String, List<Integer>> map, List<String> data) {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strategy = scanner.nextLine().trim();

        System.out.println();
        System.out.println("Enter a name or email to search all suitable people.");

        switch (strategy) {
            case "ALL":
                findAll(scanner, map, data);
                break;
            case "ANY":
                findAny(scanner, map, data);
                break;
            case "NONE":
                findNone(scanner, map, data);
                break;
        }
    }

    public static void findAll(Scanner scanner, Map<String, List<Integer>> map, List<String> data) {
        List<String> queryList = parseLine(scanner.nextLine().toLowerCase());
        List<String> resultList = new ArrayList<>();

        if (isAllWordsInKeySet(queryList, map.keySet())) {
            List<List<Integer>> listsOfIndex = new ArrayList<>();
            queryList.forEach(str -> listsOfIndex.add(map.get(str)));
            for (Integer i : indexIntersection(listsOfIndex)) {
                resultList.add(data.get(i));
            }
            if (resultList.isEmpty()) {
                System.out.println("No matching people found.");
            }
            System.out.println(resultList.size() + " persons found:");
            printPeople(resultList);
        } else {
            System.out.println("No matching people found.");
        }
    }

    private static boolean isAllWordsInKeySet(List<String> query, Set<String> keySet) {
        int res = 0;
        for (String s : query) {
            if (keySet.contains(s))
                res++;
        }
        return res == query.size();
    }

    private static List<Integer> indexIntersection(List<List<Integer>> listOfLists) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> tmp = new HashSet<>();
        listOfLists.forEach(tmp::addAll);
        for (Integer i : tmp) {
            boolean isContains = true;
            for (List<Integer> list : listOfLists) {
                isContains = isContains && list.contains(i);
            }
            if (isContains)
                result.add(i);
        }
        return result;
    }

    public static void findAny(Scanner scanner, Map<String, List<Integer>> map, List<String> data) {
        List<String> queryList = parseLine(scanner.nextLine().toLowerCase());
        Set<Integer> resultSet = new HashSet<>();
        List<String> resultList = new ArrayList<>();
        queryList.forEach(str -> {
            if (map.containsKey(str))
                resultList.add(str);
        });
        if (resultList.isEmpty()) {
            System.out.println("No matching people found.");
        }
        resultList.forEach(str -> resultSet.addAll(map.get(str)));
        System.out.println(resultSet.size() + " persons found:");
        resultSet.forEach(i -> System.out.println(data.get(i)));
    }

    public static void findNone(Scanner scanner, Map<String, List<Integer>> map, List<String> data) {
        List<String> queryList = parseLine(scanner.nextLine().toLowerCase());
        List<String> resultList = new ArrayList<>();
        if (noMatches(queryList, map)) {
            resultList = data;
        } else {
            Set<Integer> checkingSet = new HashSet<>();
            queryList.forEach(word -> checkingSet.addAll(map.get(word)));
            for (int i = 0; i < data.size(); i++) {
                if (!checkingSet.contains(i)) {
                    resultList.add(data.get(i));
                }
            }
        }
        System.out.println(resultList.size() + " persons found:");
        printPeople(resultList);
    }

    private static boolean noMatches(List<String> list, Map<String, List<Integer>> map) {
        List<String> tmp = new ArrayList<>();
        list.forEach(str -> {
            if (map.containsKey(str))
                tmp.add(str);
        });
        return tmp.isEmpty();
    }

    public static void printPeople(List<String> data) {
        System.out.println();
        System.out.println("=== List of people ===");
        data.forEach(System.out::println);
    }

    public static void exit() {
        System.out.println("Bye!");
        System.exit(0);
    }

    public static void printMenu() {
        System.out.println();
        System.out.println("=== Menu ===");
        System.out.println("1. Find a person");
        System.out.println("2. Print all people");
        System.out.println("0. Exit");
    }

    private static List<String> parseLine(String string) {
        List<String> result = new ArrayList<>();
        if (!string.contains(" ")) {
            result.add(string);
        } else {
            int spaceInd = string.indexOf(" ");
            result.add(string.substring(0, spaceInd));
            result.addAll(parseLine(string.substring(spaceInd + 1)));
        }
        return result;
    }
}
