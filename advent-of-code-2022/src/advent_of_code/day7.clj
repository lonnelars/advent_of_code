(ns advent-of-code.day7
  (:require [instaparse.core :as insta]
            [clojure.zip :as zip]
            [clojure.java.io :as io]))

(def grammar "
S = (command | output)+
command = <'$ '> (cd | ls) <newline>
cd = <'cd '> dir-name
dir-name = #'[a-z]+' | '/' | '..'
ls = <'ls'>
output = (dir | file)*
dir = <'dir '> dir-name <newline>
file = size <' '> file-name <newline>
size = #'\\d+'
file-name = #'[a-z\\.]+'
newline = #'\\n'
")

(def parser (insta/parser grammar))

(def tmap {:S #(into [] %&)
           :size (fn [child] [:size (Integer/parseInt child)])
           :file (fn [& children] [:file (into {} children)])
           :dir (fn [& children] [:dir (into {} children)])
           :cd (fn [[_ dir-name]] [:cd dir-name])})

(defn- branch?
  [node]
  (= :dir (first node)))

(defn- children
  [node]
  (drop 2 node))

(defn- make-node
  [node children]
  (into []
        (concat (take 2 node) children)))

(defn node-name [tree]
  (:name (second (zip/node tree))))

(defn go-to-root [tree]
  (if (= "/" (node-name tree))
    tree
    (go-to-root (zip/up tree))))

(defn find-dir [tree dir-name]
  (if (= (node-name tree) dir-name)
    tree
    (find-dir (zip/right tree) dir-name)))

(defn cd [tree dir-name]
  (case dir-name
    ".." (zip/up tree)
    "/" (go-to-root tree)
    (find-dir (zip/down tree) dir-name)))

(defn run-command [tree command]
  (if (= :cd (first command))
    
    (let [[_ dir-name] command]
      (cd tree dir-name))

    ;; command is ls. Do nothing. 
    tree))

(defn append-child [tree file]
  (let [[tag body]
        file]
    
    (case tag
      
      :dir
      (let [{dir-name :dir-name} body]
        (zip/append-child tree [:dir {:name dir-name :size 0}]))

      :file
      (let [{:keys [size file-name]}
            body]
        (zip/append-child tree
                          [:file {:name file-name :size size}])))))

(defn apply-instruction [tree instruction]
  (let [[tag body]
        instruction]

    (if (= :command tag)
      (run-command tree body)
      (append-child tree body))))

(defn build-fs [instructions]
  (let [root 
        (zip/zipper
         branch?
         children
         make-node
         [:dir {:name "/" :size 0}])]
    
    (zip/root (reduce apply-instruction root instructions))))

(defn total-size [tree]
  (->> tree
       (tree-seq branch?
                 children)
       (map #(:size (second %)))
       (reduce + 0)))

(defn part1 [data]
  (let [instructions
        (insta/transform tmap (parser data))

        fs
        (build-fs instructions)]

    (->> fs
         (tree-seq branch?
                   children)
         (filter #(= :dir (first %)))
         (map total-size)
         (filter #(<= % 100000))
         (reduce + 0))))

(def input (slurp (io/resource "7.txt")))

(def disk-space 70000000)
(def desired-space 30000000)

(defn part2 [data]
  (let [instructions
        (insta/transform tmap (parser data))

        fs
        (build-fs instructions)

        unused-space
        (- disk-space
           (total-size fs))]

    (->> fs
         (tree-seq branch? children)
         (filter #(= :dir (first %)))
         (map total-size)
         (filter #(>= % (- desired-space unused-space)))
         (apply min))))
