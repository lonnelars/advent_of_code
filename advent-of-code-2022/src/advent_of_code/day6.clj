(ns advent-of-code.day6
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input (slurp (io/resource "6.txt")))

(def ex "mjqjpqmgbljsphdztnvjfqwrcgsmlb")

(defn part1 [data]
  (let [four-tuples
        (partition 4 1 data)

        marker
        (first (filter (fn [four-tuple] (= 4 (count (set four-tuple))))
                       four-tuples))

        index
        (str/index-of data (apply str marker))]

    (+ index 4)))

(defn part2 [data]
  (let [tuples
        (partition 14 1 data)

        message
        (first (filter (fn [tuple] (= 14 (count (set tuple))))
                       tuples))

        index
        (str/index-of data (apply str message))]

    (+ index 14)))
