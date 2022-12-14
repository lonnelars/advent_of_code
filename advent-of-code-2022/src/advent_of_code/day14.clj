(ns advent-of-code.day14
  (:require [instaparse.core :as insta]
            [clojure.set :as set]
            [clojure.java.io :as io]
            [taoensso.tufte :as tufte :refer (defnp profile p)]))

(def ex "498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9
")

(defn parse [data]
  (let [grammar
        "
S = path+
path = coordinate (<' -> '> coordinate)* <#'\\n'>
coordinate = #'\\d+' <','> #'\\d+'
"

        parser
        (insta/parser grammar)

        tmap
        {:coordinate
         (fn [x y]
           [(Integer/parseInt x) (Integer/parseInt y)])

         :path
         #(into [] %&)

         :S
         (fn [& children] (vec children))}

        pair->set-of-points
        (fn [[[a b] [c d]]]
          (set (for [x (range (min a c) (inc (max a c)))
                     y (range (min b d) (inc (max b d)))]
                 [x y])))

        path->set-of-points
        (fn [path]
          (let [pairs
                (partition 2 1 path)]

            (reduce (fn [acc pair]
                      (set/union acc
                                 (pair->set-of-points pair)))
                    #{}
                    pairs)))]

    (->> data
         parser
         (insta/transform tmap)
         (map path->set-of-points)
         (apply set/union))))

(defnp falling-forever? [lowest sand]
  (> (second sand)
     lowest))

(defnp air? [rocks sand-at-rest floor coordinate]
  (and (not (contains? rocks
                       coordinate))
       (not (contains? sand-at-rest
                       coordinate))
       (not (> (second coordinate)
               (- floor 1)))))

(defnp find-floor [rocks]
  (+ 2
     (apply max
            (map second
                 rocks))))

(defnp step
  [{:keys [rocks current sand-at-rest] :as state}
   floor]
  (let [[x y]
        current]
    (cond
      (air? rocks
            sand-at-rest
            floor
            [x (inc y)])
      (assoc state
             :current
             [x (inc y)])

      (air? rocks
            sand-at-rest
            floor
            [(dec x) (inc y)])
      (assoc state
             :current
             [(dec x) (inc y)])

      (air? rocks
            sand-at-rest
            floor
            [(inc x) (inc y)])
      (assoc state
             :current
             [(inc x) (inc y)])

      :else
      (assoc state
             :current
             [500 0]

             :sand-at-rest
             (conj sand-at-rest
                   current)))))

(def input (slurp (io/resource "14.txt")))

(defnp part1 [data]
  (let [initial-state
        {:rocks
         (parse data)

         :current
         [500 0]

         :sand-at-rest
         (set nil)}

        lowest-rock
        (apply max
               (map second
                    (:rocks initial-state)))

        floor
        (+ 2 lowest-rock)]
    (loop [state
           initial-state]
      (let [{:keys [rocks current sand-at-rest]} state]
        (if (falling-forever? lowest-rock current)

          (count sand-at-rest)

          (recur (step state floor)))))))

(defnp full? [sand-at-rest]
  (contains? sand-at-rest
             [500 0]))

(defn part2 [data]
  (let [initial-state
         {:rocks
          (parse data)

          :current
          [500 0]

          :sand-at-rest
          (set nil)}

         floor
         (find-floor (:rocks initial-state))]

    (loop [state
           initial-state]
      (let [{:keys [rocks current sand-at-rest]}
            state]
        (if (full? sand-at-rest)
          (count sand-at-rest)
          (recur (step state floor)))))))

(defn print-state
  [{:keys [rocks current sand-at-rest]}]
  (let [xmin (- (apply min (map first rocks)) 10)
        xmax (+ (apply max (map first rocks)) 10)
        ymax (+ 2 (apply max (map second rocks)))]
    (loop [x xmin
           y 0
           result []]
      (cond
        (and (= x xmax) (= y ymax))
        (apply str (conj result \newline))

        (= x xmax)
        (recur xmin (inc y) (conj result \newline))

        :else
        (let [result'
              (cond
                (contains? rocks [x y])
                (conj result \#)

                (contains? sand-at-rest [x y])
                (conj result \o)

                (= current [x y])
                (conj result \+)

                :else
                (conj result \.))]
          (recur (inc x) y result'))))))
