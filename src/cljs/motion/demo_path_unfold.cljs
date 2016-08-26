(ns motion.demo-path-unfold
  (:require [motion.utils :refer [timeline svg-path partial-path]]))

(defn component-unfolder [style x y]
  (let [[tl] (timeline js/Infinity)]
    (fn []
      (let [t (/ @tl 2000)]
        [:g (merge style {:transform (str "translate(" x "," y ")")})
         [:path {:d (js/roundPathCorners (svg-path (partial-path (mod t 1.0)   [-200 40  48 40  98 0    200 0 ])) 5 false) :stroke-width "2px"}]
         [:path {:d (js/roundPathCorners (svg-path (partial-path (mod t 1.0)   [-200 50  50 50  100 10  200 10])) 5 false) :stroke-width "2px"}]]))))

