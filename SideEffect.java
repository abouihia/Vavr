package com.brahim.testing;

import io.vavr.collection.*;
import io.vavr.control.Try;

import java.util.Comparator;

public class SideEffect {


    static  Try<Integer> divide(Integer dividend, Integer divisor) {
        return Try.of(() -> dividend / divisor);
    }

    static Integer divideThrow(Integer dividend, Integer divisor) {
        return  dividend / divisor;
    }


    public static void main(String[] args) {

        List<Integer> list2  = List.Nil.instance();

        System.out.println(list2.toString());
        Iterator<List<Integer>> list =list2.crossProduct(3);
        System.out.println(list.size());

        Queue<Integer> queue = Queue.of(1, 2, 3)
                .enqueue(4)
                .enqueue(5);
        System.out.println(queue.asJava());
        Comparator<Integer> c = (a, b) -> b - a;
        SortedSet<Integer> reversed = TreeSet.of(c, 2, 3, 1, 2);

        System.out.println(List.of(1, 2, 3, 4,5).groupBy(i -> i % 2));

        System.out.println(List.of('a', 'b', 'c').zipWithIndex());


        System.out.println(List.fill(5, () -> 2));


    }

}
