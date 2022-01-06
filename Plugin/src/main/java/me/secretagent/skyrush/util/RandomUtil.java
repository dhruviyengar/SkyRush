package me.secretagent.skyrush.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtil {

    public static List<Integer> getRandomNumbers(int size, int bound) {
        List<Integer> list = new ArrayList<>();
        while (list.size() != size) {
            int random = new Random().nextInt(bound);
            while (list.contains(random)) {
                random = new Random().nextInt(bound);
            }
            list.add(random);
        }
        return list;
    }

}
