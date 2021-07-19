package algorithm;

import com.alibaba.fastjson.JSON;

import java.util.Random;

public class SortValidator {

    public static void main(String[] args) {
        Random random = new Random();
        int[] array = new int[random.nextInt(100)];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(10000);
        }

        MergeSort.sort(array);

        System.out.println(JSON.toJSONString(array));

        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i-1]) {
                throw new RuntimeException("算法错误");
            }
        }
    }

}
