(ns advent-of-code.day4
  (:require [instaparse.core :as insta]
            [clojure.set :as set]
            [clojure.java.io :as io]))

(def parser (insta/parser "
S = (pair-of-ranges <newline>)+
pair-of-ranges = range <','> range
range = section <'-'> section
section = #'\\d+'
newline = #'\\n'
"))

(defn range-to-set [a b]
  (into #{}
        (range a (inc b))))

(def tmap {:section #(Integer/parseInt %)
           :range range-to-set
           :pair-of-ranges #(into [] %&)
           :S #(into [] %&)})

(defn fully-contained? [set1 set2]
  (or (set/subset? set1 set2)
      (set/subset? set2 set1)))

(def input (slurp (io/resource "4.txt")))

(defn part1 []
  (->> input
       parser
       (insta/transform tmap)
       (filter (fn [[a b]] (fully-contained? a b)))
       count))

(defn overlap? [set1 set2]
  (> (count (set/intersection set1 set2))
     0))

(defn part2 []
  (->> input
       parser
       (insta/transform tmap)
       (filter (fn [[a b]] (overlap? a b)))
       count))
