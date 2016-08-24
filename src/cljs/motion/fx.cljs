(ns motion.fx)

(defn component-svg-filter-glow []
  [:filter {:id "glowfilter" :x 0 :y 0 :width "200%" :height "200%"
            :dangerouslySetInnerHTML
            {:__html "<feGaussianBlur in='SourceGraphic' stdDeviation='5'/>
                      <feMerge>
                        <feMergeNode/><feMergeNode in='SourceGraphic'/>
                      </feMerge>"}}])

(defn component-svg-pattern-hatch []
  [:pattern {:id "hatch" :width 7 :height 7 :patternTransform "rotate(45 0 0)" :patternUnits "userSpaceOnUse"}
   [:line {:x1 0 :y1 0 :x2 0 :y2 7 :style {:stroke "#41A4E6" :stroke-width 2}}]])

