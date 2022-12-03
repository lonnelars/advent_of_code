(ns advent-of-code-2022.day2
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]))

(def example "
A Y
B X
C Z
")

(def parser (insta/parser "
S = <whitespace*> round (<newline> round)* <whitespace*>
round = letter <' '> letter
letter = #'[ABCXYZ]'
newline = #'\\n'
whitespace = #'\\s'
"))

(defn to-rps [letter]
  (case letter
    ("A" "X") :rock
    ("B" "Y") :paper
    ("C" "Z") :scissors))

(defn to-rps-and-outcome [letter]
  (case letter
    "A" :rock
    "B" :paper
    "C" :scissors
    "X" :lose
    "Y" :draw
    "Z" :win))

(defn transform-map [letter-fn]
  {:S #(into [] %&)
   :round vector
   :letter letter-fn})

(defn calc-point [[opponent me]]
  (let [hand (case me
               :rock 1
               :paper 2
               :scissors 3)
        win (cond
              (= me (win-against opponent)) 6
              (= me opponent) 3
              :else 0)]
    (+ hand win)))

(defn calc-points [rounds]
  (->> rounds
       (map calc-point)
       (apply +)))

(defn win-against [opponent]
  (case opponent
    :rock :paper
    :paper :scissors
    :scissors :rock))

(defn lose-to [opponent]
  (case opponent
    :rock :scissors
    :paper :rock
    :scissors :paper))

(defn to-desired-outcome [[opponent outcome]]
  (case outcome
    :win [opponent (win-against opponent)]
    :draw [opponent opponent]
    :lose [opponent (lose-to opponent)]))

(defn part1 []
  (->> (slurp (io/resource "2.txt"))
       parser
       (insta/transform (transform-map to-rps))
       calc-points))

(defn part2 []
  (->> (slurp (io/resource "2.txt"))
       parser
       (insta/transform (transform-map to-rps-and-outcome))
       (map to-desired-outcome)
       calc-points))
