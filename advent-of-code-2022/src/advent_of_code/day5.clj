(ns advent-of-code.day5
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]))

(def grammar "
S = stacks <newline> <newline> instruction+
instruction = <'move '> number-of-crates <' from '> from-stack <' to '> to-stack <newline?>
number-of-crates = #'\\d+'
from-stack = #'\\d+'
to-stack = #'\\d+'
stacks = crate-line+ <whitespace stack-id (whitespace stack-id)* whitespace>
crate-line = (crate | empty) (<' '> (crate | empty))* <newline>
crate = <'['> #'[A-Z]' <']'>
empty = <'   '>
stack-id = #'[1-9]'
newline = #'\\n'
whitespace = #'[ ]+'
")

(def parser (insta/parser grammar))

(defn parseInt [s]
  (Integer/parseInt s))

(defn ->instruction [crates from to]
  {:number-of-crates crates
   :from (dec from)
   :to (dec to)})

(defn transpose [m]
  (apply mapv vector m))

(defn ->stacks [& lines]
  (let [columns
        (transpose lines)]

    (->> columns
         (map (fn [col]
                (filter #(not= % [:empty]) col)))
         (map reverse)
         (map #(into '() %)))))

(def tmap {:number-of-crates
           parseInt

           :from-stack
           parseInt

           :to-stack
           parseInt

           :instruction
           ->instruction

           :crate-line
           (fn [& children] (vec children))

           :stacks
           ->stacks

           :S
           (fn [stacks & instructions]
             {:stacks
              (vec stacks)

              :instructions
              instructions})})

(defn apply-instruction [stacks instruction]
  (if (= 0 (:number-of-crates instruction))
    
    stacks
    
    (let [{:keys [number-of-crates from to]}
          instruction

          crate
          (peek (nth stacks from))

          new-stacks
          (assoc stacks
                 from (pop (nth stacks from))
                 to (conj (nth stacks to) crate))]

      (recur new-stacks
             (assoc instruction
                    :number-of-crates
                    (dec number-of-crates))))))

(defn part1 []
  (let [raw-input
        (slurp (io/resource "5.txt"))

        {stacks :stacks instructions :instructions}
        (insta/transform tmap (parser raw-input))

        result
        (reduce apply-instruction
                stacks
                instructions)]

    (->> result
         (map first)
         (map second)
         (apply str))))

(defn apply-instruction-2 [stacks instruction]
  (let [{n :number-of-crates from :from to :to}
          instruction

          crates
          (take n (nth stacks from))

          new-stacks
          (assoc stacks
                 from (drop n (nth stacks from))
                 to (concat crates (nth stacks to)))]

    new-stacks))

(defn part2 []
  (let [raw-input
        (slurp (io/resource "5.txt"))

        {stacks :stacks instructions :instructions}
        (insta/transform tmap (parser raw-input))

        result
        (reduce apply-instruction-2
                stacks
                instructions)]

    (->> result
         (map first)
         (map second)
         (apply str))))
