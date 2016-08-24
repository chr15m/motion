(ns motion.demos
  (:require [motion.fx :refer [component-svg-filter-glow component-svg-pattern-hatch]]
            [motion.nibblets :refer [component-svg-x component-svg-+]]
            [motion.demo-circles :refer [component-svg-circle-test component-svg-circle-test-2 component-svg-circle-test-3 component-svg-arc-thing]]
            [motion.demo-curved-path :refer [component-svg-path-1 component-svg-hex-thing component-svg-hexagon]]))

(defn component-demo-curved-path [size]
  (fn []
    [:g
     [:defs
      (component-svg-filter-glow)
      (component-svg-pattern-hatch)]
     [:g
      [component-svg-path-1 0 -100]
      [component-svg-hex-thing -80 0]
      [component-svg-hexagon 180 0 40]
      [component-svg-hexagon 80 0 40]
      [:g {:transform "translate(0,100)"}
       [:path {:d (js/roundPathCorners (str "M -100 45 L 48 45 L 98 5 L 200 5") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]
       [:path {:d (js/roundPathCorners (str "M -100 50 L 50 50 L 100 10 L 200 10") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]]]]))

(defn component-demo-circles [size]
  (fn []
    [:g
     [component-svg-circle-test 150 -100]
     [component-svg-circle-test-2 -200 50]
     [component-svg-circle-test-3 0 100]
     [component-svg-arc-thing -100 -100]]))

(defn component-demo-nibblets [size]
  (fn []
    [:g
       (component-svg-x 90 30)
       (component-svg-x 140 20)
       (component-svg-x 120 40)
       (component-svg-+ -100 -100)
       (component-svg-+ -130 -130)
       (component-svg-+ -90 -150)]))

(def demos {"curved path" component-demo-curved-path
            "circles" component-demo-circles
            "nibblets" component-demo-nibblets})
