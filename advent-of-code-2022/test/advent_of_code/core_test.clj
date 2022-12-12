(ns advent-of-code-2022.core-test
  (:require [clojure.test :refer :all]
            [advent-of-code-2022.core :refer :all]
            [advent-of-code.day11 :as day11]
            [advent-of-code.day12 :as day12]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest day-11
  (testing "part 1"
    (is (= 58322
           (day11/part1)))))

(deftest day-12
  (testing "part 1"
    (is (= 468
           (day12/part1))))
  (testing "part 2"
    (is (= 459
           (day12/part2)))))
