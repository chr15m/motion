(ns motion.fx)

(defn component-svg-filter-glow []
  [:filter {:id "glowfilter" :x 0 :y 0 :width "200%" :height "200%"
            :dangerouslySetInnerHTML
            {:__html "<feGaussianBlur in='SourceGraphic' stdDeviation='5'/>
                      <feMerge>
                        <feMergeNode/><feMergeNode in='SourceGraphic'/>
                      </feMerge>"}}])

(defn component-svg-pattern-hatch [& [id fg bg]]
  [:pattern {:id (or id "hatch") :width 7 :height 7 :patternTransform "rotate(45 0 0)" :patternUnits "userSpaceOnUse"}
   [:g
    (if bg
      [:rect {:x 0 :y 0 :width 7 :height 7 :fill bg :stroke "none"}])
    [:line {:x1 0 :y1 0 :x2 0 :y2 7 :stroke (or fg "#41A4E6") :stroke-width 2}]]])

