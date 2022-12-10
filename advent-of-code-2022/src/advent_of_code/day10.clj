(ns advent-of-code.day10
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]))

(def input (slurp (io/resource "10.txt")))

(def grammar "
S = (addx | noop)*
addx = <'addx '> #'-?\\d+' <newline>?
noop = <'noop'> <newline>?
newline = #'\\n'")

(def parser (insta/parser grammar))

(def tmap {:S
           (fn [& children] (vec children))

           :addx
           (fn [s] [:addx (Integer/parseInt s)])})

(defn addx->noop+addx
  [instruction]
  (let [[tag body]
        instruction]

    (case tag
      :noop
      [[:noop]]

      :addx
      [[:noop] [:addx body]])))

(defn parse [data]
  (->> data
       parser
       (insta/transform tmap)
       (mapcat addx->noop+addx)))

(defn step
  [{:keys [X cycle] :as registers}
   [tag body :as instruction]]

  (case tag
    :noop
    registers

    :addx
    (assoc registers
           :X (+ X body))))

(defn signal [history cycle]
  (let [{X :X}
        (get (vec history)
             (dec cycle))]

    (* X cycle)))

(defn part1 [data]
  (let [history
        (->> (parse data)
             (reductions step {:X 1}))]

    (apply +
           (map (partial signal history)
                [20 60 100 140 180 220]))))

(defn draw? [cycle sprite-pos]
  (< (abs (- sprite-pos
             (mod cycle 40)))
     2))

(defn draw-line [line]
  (apply str
         (map #(if % \# \.) line)))

(defn part2 [data]
  (let [history
        (->> data
             parse
             (reductions step {:X 1}))

        pixels
        (map-indexed #(draw? %1 (:X %2))
                     history)

        lines
        (map draw-line
             (partition 40 pixels))]

    (doseq [line lines]
      (println line))))
