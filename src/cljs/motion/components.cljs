(ns motion.components
  (:require [motion.utils :refer [svg-arc]]))

(defn component-svg-arc [x y r as ae th]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width th :d (svg-arc 0 0 r as ae)}]])

