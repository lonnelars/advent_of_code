(ns advent-of-code.day9
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input (slurp (io/resource "9.txt")))

(def π Math/PI)

(defn vlength
  "length of a 2d vector"
  [[x y]]
  (Math/sqrt (+ (* x x) (* y y))))

(defn angle
  "angle between x-axis and 2d vector, in [0, 2π)"
  [[x y]]
  (let [a (Math/atan2 y x)]
    (if (neg? a)
      (+ a (* 2 π))
      a)))

(defn quadrant
  "returns which quadrant the given 2d vector is in"
  [[x y]]
  (let [θ (angle [x y])]
    (cond
      (< θ (/ π 2)) 1
      (< θ π) 2
      (< θ (* π (/ 3 2))) 3
      :else 4)))

(defn tail->head
  "a 2d vector from the tail to the head"
  [tail head]
  (let [[xt yt] tail
        [xh yh] head]

    [(- xh xt)
     (- yh yt)]))

(defn parse
  "Example: D 20 -> [:down 20]"
  [move]
  (let [dir (first move)
        dist (apply str (drop 2 move))]

    [(case dir
       \R :right
       \U :up
       \L :left
       \D :down)

     (Integer/parseInt dist)]))

(defn apply-move
  "Moves the head one space in the given direction. Returns the new
  head."
  [[xh yh] move]
  (case move
    :right [(inc xh) yh]
    :up [xh (inc yh)]
    :left [(dec xh) yh]
    :down [xh (dec yh)]))

(defn close?
  "Are the tail and head close to each other?"
  [tail head]
  (<= (vlength (tail->head tail head))
      (Math/sqrt 2)))

(defn move-tail
  "First argument is the path of one tail, second is the head. Moves
  tail close to the head. Returns the new path for the tail."
  [path head]
  (if (close? (peek path) head)
    
    path
    
    (let [[xt yt :as tail] (peek path)

          v (tail->head tail head)

          a (angle v)
          
          q (quadrant v)
          
          new-tail
          (cond
            (= 0.0 a) [(inc xt) yt]
            (= 1 q) [(inc xt) (inc yt)]
            (= (/ π 2) a) [xt (inc yt)]
            (= 2 q) [(dec xt) (inc yt)]
            (= π a) [(dec xt) yt]
            (= 3 q) [(dec xt) (dec yt)]
            (= (* π (/ 3 2)) a) [xt (dec yt)]
            (= 4 q) [(inc xt) (dec yt)])]

      (if (close? new-tail head)

        (conj path new-tail)

        (move-tail (conj path new-tail)
                   head)))))

(defn move-tails
  "Moves a list of tails close to the head. Returns an updated list of
  tails."
  [tails head]
  (if (empty? tails)
    
    (list)
    
    (let [first-tail
          (move-tail (first tails)
                     head)]

      (conj (move-tails (rest tails)
                        (peek first-tail))
            first-tail))))

(defn step
  "Moves the head to a new position as given by move. Moves all tails
  close to the head."
  [{:keys [tails head] :as state} move]

  (let [new-head (apply-move head move)]

    {:tails (move-tails tails new-head)
     :head new-head}))

(defn solve
  [data number-of-tails]
  (let [;; A tail is represented as its history of positions. All
        ;; tails start in [0 0].
        tails
        (repeat number-of-tails (list [0 0]))

        ;; The head also starts in [0 0].
        head
        [0 0]

        ;; translates input to a sequence of single moves.
        ;; Example: "R 2" -> (:right :right).
        moves
        (->> data
             str/split-lines
             (map parse)
             (mapcat (fn [[dir distance]] (repeat distance dir))))

        result
        (reduce step {:head head :tails tails} moves)]

    (count (set (last (:tails result))))))

(defn part1 [data]
  (solve data 1))

(defn part2 [data]
  (solve data 9))
