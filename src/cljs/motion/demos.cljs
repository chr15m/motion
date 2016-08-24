(ns motion.demos
  (:require [motion.fx :refer [component-svg-filter-glow component-svg-pattern-hatch]]
            [motion.demo-nibblets :refer [component-svg-x component-svg-+ component-svg-o]]
            [motion.demo-circles :refer [component-svg-circle-test component-svg-circle-test-2 component-svg-circle-test-3 component-svg-arc-thing]]
            [motion.demo-curved-path :refer [component-svg-path-1 component-svg-hex-thing component-svg-hexagon component-svg-twolines]]
            [motion.demo-hex-plane :refer [component-svg-hex-plane]]
            [motion.demo-orbital-transformer :refer [component-orbiter]]))

(def styles {:blue-line {:fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"}
             :blue-flat {:fill "#41A4E6" :fill-opacity "0.3" :stroke-linecap "round"}})

(defn component-demo-curved-path [size]
  (let [style (styles :blue-line)]
    (fn []
      [:g
       [:defs
        (component-svg-filter-glow)
        (component-svg-pattern-hatch)]
       [:g
        [component-svg-path-1 style 0 -100]
        [component-svg-hex-thing style -80 0]
        [component-svg-hexagon style 180 0 40]
        [component-svg-hexagon style 80 0 40]
        [component-svg-twolines style]]])))

(defn component-demo-circles [size]
  (let [style (styles :blue-line)]
    (fn []
      [:g
       [component-svg-circle-test style 150 -100]
       [component-svg-circle-test-2 style -200 50]
       [component-svg-circle-test-3 style 0 100]
       [component-svg-arc-thing style -100 -100]])))

(defn component-demo-nibblets [size]
  (let [style (styles :blue-line)]
    (fn []
      [:g
       (component-svg-x style 90 30)
       (component-svg-x style 140 20)
       (component-svg-x style 120 40)

       (component-svg-+ style -100 -100)
       (component-svg-+ style -130 -130)
       (component-svg-+ style -90 -150)
       
       (component-svg-o style -50 100)
       (component-svg-o style -80 120)
       (component-svg-o style -50 150)])))

(defn component-demo-hex-plane [size]
  (let [style-1 (merge (styles :blue-line) {:fill "url(#hatch)"})
        style-2 (styles :blue-flat)]
    (fn []
      [:g
       [:defs
        (component-svg-filter-glow)
        (component-svg-pattern-hatch)]
       [component-svg-hex-plane style-1 style-2]])))

(defn component-demo-orbital-transformer [size]
  (let [style (styles :blue-line)]
    [:g style
     [:defs
      (component-svg-filter-glow)
      (component-svg-pattern-hatch)]
     [component-svg-hexagon (assoc style :fill "url(#hatch)") 0 0 40]
     [:circle {:cx 0 :cy 0 :r 45}]
     [component-orbiter style 0 0]]))

(def demos {"curved path" component-demo-curved-path
            "circles" component-demo-circles
            "nibblets" component-demo-nibblets
            "hex plane" component-demo-hex-plane
            "interactive: orbital transformer" component-demo-orbital-transformer})
