(ns advent-of-code.day11)

(defn make-op [op a div]
  (fn [old]
    (op a old)))

(defn make-test [div then else]
  (fn [x]
    (if (= 0 (mod x div))
      then
      else)))

(def init
  [{:items
    [59 65 86 56 74 57 56]

    :inspected
    0

    :operation
    (make-op * 17 3)

    :test
    (make-test 3 3 6)}

   {:items
    [63 83 50 63 56]

    :inspected
    0

    :operation
    (make-op + 2 13)

    :test
    (make-test 13 3 0)}

   {:items
    [93 79 74 55]

    :inspected
    0

    :operation
    (make-op + 1 2)

    :test
    (make-test 2 0 1)}

   {:items
    [86 61 67 88 94 69 56 91]

    :inspected
    0

    :operation
    (make-op + 7 11)

    :test
    (make-test 11 6 7)}

   {:items
    [76 50 51]

    :inspected
    0

    :operation
    (fn [old] (* old old))

    :test
    (make-test 19 2 5)}

   {:items
    [77 76]

    :inspected
    0

    :operation
    (make-op + 8 17)

    :test
    (make-test 17 2 1)}

   {:items
    [74]

    :inspected
    0

    :operation
    (make-op * 2 5)

    :test
    (make-test 5 4 7)}

   {:items
    [86 85 52 86 91 95]

    :inspected
    0

    :operation
    (make-op + 6 7)

    :test
    (make-test 7 4 5)}])

(defn inspect-and-throw
  [monkeys index div]
  (let [monkey
        (get monkeys index)
        
        items
        (:items monkey)]

    (if (empty? items)
      
      monkeys

      (let [item
            (first items)

            monkey'
            (assoc monkey
                   :items (vec (rest items))
                   :inspected  (inc (:inspected monkey)))

            worry-level'
            (quot ((:operation monkey) item)
                  div)

            throw-to
            ((:test monkey) worry-level')

            receiver
            (get monkeys throw-to)

            receiver'
            (assoc receiver
                   :items (conj (:items receiver)
                                worry-level'))
            monkeys'
            (assoc monkeys
                   index monkey'
                   throw-to receiver')]

        (inspect-and-throw monkeys' index div)))))

(defn round [div monkeys]
  (reduce (fn [acc index]
            (inspect-and-throw acc index div))
          monkeys
          (range (count monkeys))))

(defn part1 []
  (let [result
        (reduce (fn [acc _] (round 3 acc))
                init
                (range 20))]
    (->> result
         (map :inspected)
         sort
         reverse
         (take 2)
         (apply *))))

(defn part2 []
  (let [result
        (reduce (fn [acc _] (round 1 acc))
                init
                (range 10000))]
    (->> result
         (map :inspected)
         sort
         reverse
         (take 2)
         (apply *))))
