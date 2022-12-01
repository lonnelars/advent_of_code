(ns advent-of-code-2022.day1
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]))

(def parser (insta/parser "
S = Foods (<Blank_line> Foods)* <#'\\s*'>.
Blank_line = #'\\n\\n'.
Foods = Food (<#'\\n'> Food)*.
Food = #'\\d+'.
"))

(def transform-map {:S #(into [] %&) :Foods #(into [] %&) :Food #(Integer/parseInt %)})

(defn part1 [data-set]
  (let [input (slurp (io/resource data-set))]
    (->> input
         (insta/parse parser)
         (insta/transform transform-map)
         (map #(apply + %))
         (apply max))))

(defn part2 [data-set]
  (let [input (slurp (io/resource data-set))]
    (->> input
         (insta/parse parser)
         (insta/transform transform-map)
         (map #(apply + %))
         sort
         reverse
         (take 3)
         (apply +))))
