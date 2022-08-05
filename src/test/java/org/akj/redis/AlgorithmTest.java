package org.akj.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
public class AlgorithmTest {

    @Test
    public void test() {
        String[] signs = new String[]{"+", "-"/*,"<",">",""*/};
        Integer bound = 20;
        float inflation = 0.5f;
        IntStream.range(0, 100).forEach(i -> {
            Boolean flag = true;
            while (flag) {
                Random random = new Random();
                Integer num1 = random.nextInt(bound);
                Integer num2 = random.nextInt(bound);
                int signIndex = random.nextInt(signs.length);
                String sign = signs[signIndex];
                if (sign.trim().equals("+") && num1 + num2 > (int) bound * (1 + inflation)) {
                    flag = true;
                    continue;
                }

                if (sign.trim().equals("-") && num1 < num2) {
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }

                String formula = String.format("%2d %s %2d = ", num1, signs[signIndex], num2);
                System.out.println(formula);
                flag = false;
            }
        });
    }

    @Test
    public void test1(){
        IntStream.range(0,10).forEach(i -> {
            System.out.println(randomLevel(10, 0.5));
        });
    }

    private int randomLevel(Integer maxLevel, double PROBABILITY) {
        int level = 0;
        while (Math.random() < PROBABILITY && level < maxLevel - 1) {
            ++level;
        }
        return level;
    }
}
