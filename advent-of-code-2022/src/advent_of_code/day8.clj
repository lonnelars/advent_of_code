(ns advent-of-code.day8
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn ->ints
  [line]
  (->> (seq line)
       (map #(Character/digit % 10))
       vec))

(defn parse
  [data]
  (->> data
       str/split-lines
       (map ->ints)))

(defn- rows
  [matrix]
  (count matrix))

(defn- columns
  [matrix]
  (count (first matrix)))

(defn outer-trees
  [matrix]
  (+ (* 2
        (count matrix))
     (* 2
        (- (count (first matrix))
           2))))

(defn first-is-tallest?
  [trees]
  (every? #(< % (first trees))
          (rest trees)))

(defn col [matrix col-index]
  (vec (map #(nth % col-index) matrix)))

(defn tree-lines
  "returns a vector of the four lines of trees seen from the given row
  and column. Each line is a vector with the tree at the given
  coordinate first."
  [matrix [r c]]

  (let [right
        (drop c (nth matrix r))

        left
        (reverse (take (inc c)
                       (nth matrix r)))

        column
        (col matrix c)

        top
        (->> column (take (inc r)) reverse)

        bottom
        (drop r column)]

    [right left top bottom]))

(defn visible?
  "Is the tree at the given coordinate visible from the outside?"
  [matrix coord]
  (boolean (some first-is-tallest?
                 (tree-lines matrix coord))))

(defn inner-trees
  "Counts the inner trees that are visible from the outside."
  [matrix]
  (let [coords
        (for [r (range 1 (dec (rows matrix)))
              c (range 1 (dec (columns matrix)))]
          [r c])]

    (->> coords
         (map (fn [coord] (visible? matrix coord)))
         (filter identity)
         count)))

(defn part1 [data]
  (let [matrix
        (parse data)]

    (+ (outer-trees matrix)
       (inner-trees matrix))))

(def input (slurp (io/resource "8.txt")))

(defn visible-trees
  "Counts the number of trees visible from the first tree in the
  tree-line."
  [tree-line]

  (let [[smaller others]
        (split-with (fn [tree] (< tree (first tree-line)))
                    (rest tree-line))]

    (cond
      (empty? smaller)
      1 ; we can only see one tree
      
      (and (< (last smaller) (first tree-line))
           (not-empty others))
      (inc (count smaller)) ; we can see one of the larger trees

      :else
      (count smaller))))

(defn scenic-score
  [matrix coord]

  (->> (tree-lines matrix coord)
       (map visible-trees)
       (apply *)))

(defn part2 [data]
  (let [matrix
        (parse data)

        inner-coords
        (for [r (range 1 (dec (rows matrix)))
              c (range 1 (dec (columns matrix)))]
          [r c])]

    (->> inner-coords
         (map (fn [coord] (scenic-score matrix coord)))
         (apply max))))
