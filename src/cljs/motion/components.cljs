(ns motion.components
  (:require [motion.utils :refer [svg-arc g-trans]]))

(defn component-svg-arc [style x y r as ae th]
  [:g (g-trans x y)
   [:path (merge style {:stroke-width th :d (svg-arc 0 0 r as ae)})]])

