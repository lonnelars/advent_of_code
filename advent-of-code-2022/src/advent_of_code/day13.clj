(ns advent-of-code.day13
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn in-order?
  [left right]
  (cond
    (and (int? left)
         (int? right))
    (cond
      (< left right) :in-order
      (> left right) :out-of-order
      :else :undecided)

    (and (coll? left)
         (coll? right))
    (cond
      (and (empty? left)
           (empty? right))
      :undecided

      (empty? left)
      :in-order

      (empty? right)
      :out-of-order

      :else
      (let [result
            (in-order? (first left) (first right))]

        (if (not= :undecided
                  result)
          result
          (in-order? (rest left) (rest right)))))

    :else
    (let [left'
          (if (int? left) [left] left)

          right'
          (if (int? right) [right] right)]

      (in-order? left' right'))))

(def input (slurp (io/resource "13.txt")))

(defn parse
  [s]
  (->> s
       str/split-lines
       (filter #(not (empty? %)))
       (map #(eval (read-string %)))))

(defn part1
  [data]
  (->> data
       parse
       (partition 2)
       (map #(apply in-order? %))
       (map vector (iterate inc 1))
       (filter #(= :in-order (second %)))
       (map first)
       (reduce + 0)))

(defn part2
  [data]
  (let [packets
        (parse data)

        keyfn
        (fn [a b]
          (case (in-order? a b)
            :in-order -1
            :undecided 0
            :out-of-order 1))

        sorted
        (sort-by identity
                 keyfn
                 (conj packets
                       [[2]]
                       [[6]]))

        indexed
        (map vector
             (iterate inc 1)
             sorted)

        divider-packet-indices
        (->> indexed
             (filter #(or (= [[2]] (second %))
                          (= [[6]] (second %))))
             (map first))]

    (apply * divider-packet-indices)))
