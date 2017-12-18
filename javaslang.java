package com.brahim.testing;


import io.vavr.test.Arbitrary;
import io.vavr.test.CheckResult;
import io.vavr.test.Property;
import javaslang.*;
import javaslang.collection.List;
import javaslang.collection.Stream;
import javaslang.control.Either;
import javaslang.control.Option;
import javaslang.control.Try;

import java.util.Optional;
import java.util.function.Predicate;

import static javaslang.API.*;

public class javaslang {


    private static Predicate<Integer> divisibleByTwo = i -> i % 2 == 0;
    private static Predicate<Integer> divisibleByFive = i -> i % 5 == 0;


    public static String stringIfy(Integer i) {

        return Match(i).of(
                Case($(0), "zero"),
                Case($(1), "one"),
                Case($(n -> n > 5), ">5"),
                Case($(), "negative")

        );
    }


    public static void main(String[] args) {
        System.out.println(javaslang.stringIfy(6));

        Tuple2<String, Integer> java8 = Tuple.of("java", 8);

        System.out.println(java8._1);
        System.out.println(java8._2);

        // (vavr, 1)
        Tuple2<String, Integer> that = java8.map(
                s -> s.substring(2) + "vr",
                i -> i / 8
        );

        System.out.println(that._1);
        System.out.println(that._2);

        Tuple2<String, Integer> that2 = java8.map(
                (s, i) -> Tuple.of(s.substring(2) + "vr", i / 8)
        );

        Function2<Integer, Integer, Integer> sum = (a, b) -> a + b;

        //Composition
        Function1<Integer, Integer> plusOne = a -> a + 1;
        Function1<Integer, Integer> multiplyByTwo = a -> a * 2;


        Function1<Integer, Integer> add1AndMultiplyBy2 = plusOne.andThen(multiplyByTwo);
        System.out.println(add1AndMultiplyBy2.apply(3));

        //Lifting

        Function2<Integer, Integer, Integer> divide = (a, b) -> a / b;

        Function2<Integer, Integer, Option<Integer>> safeDivide = Function2.lift(divide);

        Option<Integer> i1 = safeDivide.apply(1, 0);
        System.out.println(i1.getOption());
        Option<Integer> i2 = safeDivide.apply(4, 2);
        System.out.println(i2.get() + "  " + i2.getOption());

        //Partial application
        Function5<Integer, Integer, Integer, Integer, Integer, Integer> sum1 =
                (a, b, c, d, e) -> a + b + c + d + e;

        //the admetting 5  arg but i put only 3  that why called partial application
        System.out.println(sum1.apply(2, 3, 1));


        //Currying
        Function3<Integer, Integer, Integer, Integer> sum3 = (a, b, c) -> a + b + c;

        final Function1<Integer, Function1<Integer, Integer>> add2 = sum3.curried().apply(2);

        //Memoization
        Function0<Double> hashCache = Function0.of(Math::random).memoized();

        double randomValue1 = hashCache.apply();
        double randomValue2 = hashCache.apply();
        System.out.println(randomValue1 == randomValue2);

        //Option (vavr) and Optional ( java)
        //1-Optional ( java)
        Optional<String> maybeFoo = Optional.of("foo");

        System.out.println(maybeFoo.get().equals("foo"));

        Optional<String> maybeFooBar = maybeFoo.map(s -> (String) null).map(s -> s.toUpperCase() + "bar");
        System.out.println(maybeFooBar.isPresent());

        //2-Option (vavr)
        Option<String> maybeFooOption = Option.of("foo");
        System.out.println(maybeFooOption.get().equals("foo"));

       /* Option<String> maybeFooBarOption = maybeFooOption.map(s -> (String)null).map(s -> s.toUpperCase() + "bar");
        System.out.println(maybeFooBarOption.getOption());*/


        //try
        Try<Integer> computation = Try.of(() -> 1 / 0);

        if (computation.isSuccess()) {
            System.out.println(computation.get());
        } else if (computation.isFailure()) {
            System.out.println(computation.getCause());
        }

        //Lazy
        Lazy<Double> lazy = Lazy.of(Math::random);
        lazy.isEvaluated(); // = false
        lazy.get();         // = 0.123 (random generated)
        lazy.isEvaluated(); // = true
        lazy.get();

        CharSequence chars = Lazy.val(() -> "Yay!", CharSequence.class);

        System.out.println(chars.toString());

        //Either
        System.out.println(computeWithEither(80).isLeft());
        System.out.println(computeWithEither(80).right());
     /*computeWithEither(80)
  .right()
  .filter(...)
   .map(...)*/
        System.out.println(computeWithEither(80).contains(80));
        //We can fold Left and Right to one common type:
        Either<String, Integer> either = Either.right(42);
        String result = either.fold(i -> i, Object::toString);
        System.out.println(either.right().get());
        System.out.println(result);


        //Future and validation in vavr

        //Collection
        // io.vavr.collection.List
        List.of(1, 2, 3).sum();


        //Property Checking
        System.out.println(Stream.of(0).map(i -> Match(i).of(
                Case($(divisibleByFive.and(divisibleByTwo)), "DividedByTwoAndFiveWithoutRemainder"),
                Case($(divisibleByFive), "DividedByFiveWithoutRemainder"),
                Case($(divisibleByTwo), "DividedByTwoWithoutRemainder"),
                Case($(), "")
        )).get());

        Arbitrary<Integer> multiplesOf2 = Arbitrary.integer().filter(i -> i > 0).filter(i -> i % 2 == 0 && i % 5 != 0);

        io.vavr.CheckedFunction1<Integer, Boolean> mustEquals = i -> stringsSupplier().get(i).equals("DividedByTwoWithoutRemainder");

        CheckResult resultCheckResult =
                Property.def("Every second element must equal to DividedByTwoWithoutRemainder")
                        .forAll(multiplesOf2)
                        .suchThat(mustEquals)
                        .check(10000, 100);
        System.out.println(resultCheckResult.isSatisfied());

    }


    private static Either<String, Integer> computeWithEither(int marks) {
        if (marks < 85) {
            return Either.left("Marks not acceptable");
        } else {
            return Either.right(marks);
        }
    }

    private static Stream<String> stringsSupplier() {
        return Stream.from(0).map(i -> Match(i).of(
                Case($(divisibleByFive.and(divisibleByTwo)), "DividedByTwoAndFiveWithoutRemainder"),
                Case($(divisibleByFive), "DividedByFiveWithoutRemainder"),
                Case($(divisibleByTwo), "DividedByTwoWithoutRemainder"),
                Case($(), "")));
    }
}
