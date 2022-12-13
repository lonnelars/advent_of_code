(ns advent-of-code.day12
  (:require [clojure.string :as str]
            [loom.graph]
            [loom.alg]
            [clojure.java.io :as io]))

(def ex
  "Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi")

(defn indexed-matrix
  [matrix]
  (mapv (fn [rowi row]
          (mapv (fn [coli value]
                  {:coordinate [rowi coli]
                   :value value})
                (range)
                row))
        (range)
        matrix))

(def letter->elevation
  (into {}
        (map vector
             "abcdefghijklmnopqrstuvwxyz"
             (range))))

(defn elevation [c]
  (cond (= \S c)
        (get letter->elevation \a)
        (= \E c)
        (get letter->elevation \z)
        :else
        (get letter->elevation c)))

(defn can-move? [from to]
  (and (not (nil? to))
       (<= (- (elevation to)
              (elevation from))
           1)))

(defn adjacent-nodes
  ([matrix acc row]
   (reduce (fn [acc cell]
             (assoc acc
                    (:coordinate cell)
                    (adjacent-nodes matrix cell)))
           acc
           row))
  ([matrix cell]
   (let [{:keys [coordinate value]}
         cell

         [row col]
         coordinate

         adjacent-coordinates
         [[row (inc col)]
          [(dec row) col]
          [row (dec col)]
          [(inc row) col]]

         nodes
         (filter (fn [adjacent-coordinate]
                   (let [from
                         value

                         to
                         (get-in matrix adjacent-coordinate)]

                     (can-move? from to)))
                 adjacent-coordinates)]

     nodes)))

(defn parse [s]
  (let [matrix
        (->> s
             str/split-lines
             (map vec)
             (into []))

        start
        (->> matrix
             indexed-matrix
             flatten
             (filter #(= \S (:value %)))
             (map :coordinate)
             first)

        end
        (->> matrix
             indexed-matrix
             flatten
             (filter #(= \E (:value %)))
             (map :coordinate)
             first)

        adjacency-map
        (reduce (fn [acc row] (adjacent-nodes matrix acc row))
                {}
                (indexed-matrix matrix))]

    {:matrix
     matrix

     :graph
     (loom.graph/digraph adjacency-map)

     :start
     start

     :end
     end}))

(def input (slurp (io/resource "12.txt")))

(defn part1
  ([]
   (part1 input))
  ([data]
   (let [{:keys [graph start end]}
         (parse data)

         path
         (loom.alg/bf-path graph
                           start
                           end)]

     (dec (count path)))))

(defn find-as [matrix]
  (->> matrix
       indexed-matrix
       flatten
       (filter #(or (= \S (:value %)) (= \a (:value %))))
       (map :coordinate)))

(defn part2
  ([]
   (part2 input))
  ([data]
   (let [{:keys [matrix graph start end]}
         (parse data)

         as
         (find-as matrix)

         paths
         (map (fn [a]
                (loom.alg/bf-path graph
                                  a
                                  end))
              as)]

     (->> paths
          (filter #(not (nil? %))) ; some starting points can not reach the end
          (map count)
          (map dec)
          (apply min)))))
