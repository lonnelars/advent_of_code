(ns advent-of-code.day3
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.java.io :as io]))

(def example "vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw
")

(def input (slurp (io/resource "3.txt")))

(defn to-priority [c]
  (cond
    (Character/isLowerCase c) (+ 1
                                 (- (int c)
                                    (int \a)))
    :else (+ 27 (- (int c) (int \A)))))

(defn part1 [data]
  (let [lines (str/split-lines data)]
    (->> lines
         (map #(split-at (/ (count %) 2) %))
         (map (fn [pair] (map #(into #{} %) pair)))
         (map #(apply set/intersection %))
         (map #(into [] %))
         (map first)
         (map to-priority)
         (apply +)
         )))

(defn part2 [data]
  (->> data
       str/split-lines
       (partition 3)
       (map (fn [triple] (map #(into #{} %) triple)))
       (map (fn [triple] (apply set/intersection triple)))
       (map #(into [] %))
       (map first)
       (map to-priority)
       (apply +)))
