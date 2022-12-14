(ns advent-of-code.day14
  (:require [instaparse.core :as insta]
            [clojure.set :as set]
            [clojure.java.io :as io]))

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

(defn falling-forever? [rocks sand]
  (let [lowest
        (apply max
               (map second
                    rocks))]
    (> (second sand)
       lowest)))

(defn init [data]
  {:rocks
   (parse data)

   :current
   [500 0]

   :sand-at-rest
   (set nil)})

(defn step
  [{:keys [rocks current sand-at-rest] :as state}]
  (let [[x y]
        current

        floor
        (+ 2
           (apply max
                  (map second
                       rocks)))

        air?
        (fn [coordinate]
          (and (not (contains? rocks
                               coordinate))
               (not (contains? sand-at-rest
                               coordinate))
               (not (> (second coordinate)
                       (- floor 1)))))]
    (cond
      (air? [x (inc y)])
      (assoc state
             :current
             [x (inc y)])

      (air? [(dec x) (inc y)])
      (assoc state
             :current
             [(dec x) (inc y)])

      (air? [(inc x) (inc y)])
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

(defn part1 [data]
  (loop [state (init data)]
    (let [{:keys [rocks current sand-at-rest]} state]
      (if (falling-forever? rocks
                            current)

        (count sand-at-rest)

        (recur (step state))))))

(defn part2 [data]
  (loop [state (init data)]
    (let [{:keys [rocks current sand-at-rest]}
          state]
      (if (contains? sand-at-rest
                     [500 0])
        (count sand-at-rest)
        (recur (step state))))))

(defn print-state
  [{:keys [rocks current sand-at-rest]}]
  (loop [x 480
         y 0]
    (let [xmax 520
          ymax 11]
      (cond
        (and (= x xmax) (= y ymax))
        (println)
        
        (= x xmax)
        (do (println)
            (recur 480 (inc y)))

        :else
        (do
          (cond
            (contains? rocks [x y])
            (print "#")

            (contains? sand-at-rest [x y])
            (print "o")

            (= current [x y])
            (print "+")

            :else
            (print "."))
          (recur (inc x) y))))))
