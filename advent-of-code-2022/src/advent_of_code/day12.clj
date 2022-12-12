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

(defn elevation [c]
  (cond (= \S c)
        (int \a)
        (= \E c)
        (int \z)
        :else
        (int c)))

(defn can-move-to? [from to]
  (and (not (nil? to))
       (<= (- (elevation to)
              (elevation from))
           1)))

(defn adjacent-nodes
  [matrix row col value]
  (let [adjacent-coordinates
        [[row (inc col)]
         [(dec row) col]
         [row (dec col)]
         [(inc row) col]]
        
        edges
        (filter (fn [coordinate]
                  (let [from
                        (get-in matrix [row col])

                        to
                        (get-in matrix coordinate)]

                    (can-move-to? value to)))
                adjacent-coordinates)]

    edges))

(defn reduce-row
  [matrix acc [row-index row]]
  (reduce (fn [acc [col-index c]]
            (assoc acc
                   [row-index col-index]
                   (adjacent-nodes matrix row-index col-index c)))
          acc
          (map-indexed vector row)))

(defn parse [s]
  (let [matrix
        (->> s
             str/split-lines
             (map vec)
             (into []))

        coordinates
        (for [row (range 0 (count matrix))
              col (range 0 (count (first matrix)))]
          [row col])
        
        start
        (first (filter (fn [coordinate]
                         (= \S
                            (get-in matrix
                                    coordinate)))
                       coordinates))

        end
        (first (filter (fn [coordinate]
                         (= \E
                            (get-in matrix
                                    coordinate)))
                       coordinates))


        adjacency-map
        (reduce (fn [acc row] (reduce-row matrix acc row))
                {}
                (map-indexed vector matrix))]

    {:matrix
     matrix

     :graph
     (loom.graph/weighted-digraph adjacency-map)
     
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
         (loom.alg/dijkstra-path graph
                                 start
                                 end)]

     (dec (count path)))))

(defn find-as [matrix]
  (let [indexed-matrix
        (map-indexed (fn [rowi row]
                       (map-indexed (fn [coli c]
                                      {:coordinate [rowi coli]
                                       :value c})
                                    row))
                     matrix)]

    (->> indexed-matrix
         flatten
         (filter (fn [{:keys [_ value]}]
                   (or (= \S value)
                       (= \a value))))
         (map (fn [{:keys [coordinate _]}]
                coordinate)))))

(defn part2
  ([]
   (part2 input))
  ([data]
   (let [{:keys [matrix graph start end]}
         (parse data)

         as
         (find-as matrix)

         paths
         (map (fn [a] (loom.alg/dijkstra-path graph
                                              a
                                              end))
              as)]

     (->> paths
          (filter #(not (nil? %))) ; some starting points can not reach the end
          (map count)
          (map dec)
          (apply min)))))
