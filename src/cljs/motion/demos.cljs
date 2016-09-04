(ns motion.demos
  (:require [motion.fx :refer [component-svg-filter-glow component-svg-pattern-hatch]]
            [motion.shapes :refer [shapes]]
            [motion.styles :refer [colors styles]]
            [motion.utils :refer [timeline]]
            [motion.demo-nibblets :refer [component-svg-x component-svg-+ component-svg-o]]
            [motion.demo-circles :refer [component-svg-circle-test component-svg-circle-test-2 component-svg-circle-test-3 component-svg-arc-thing]]
            [motion.demo-curved-path :refer [component-svg-path-1 component-svg-hex-thing component-svg-hexagon component-svg-twolines]]
            [motion.demo-hex-plane :refer [component-svg-hex-plane]]
            [motion.demo-orbital-transformer :refer [component-orbiter]]
            [motion.demo-path-unfold :refer [component-unfolder]]
            [motion.demo-logo-unfold :refer [component-logo-unfold component-logo-unfold-2]]
            [motion.demo-hex-popout :refer [component-hex-popout]]
            [motion.demo-sci-menu :refer [component-sci-menu]]
            [motion.demo-walker :refer [component-walker-demo-world]]))

(defn ^export component-demo-curved-path [size event-chan]
  (let [style (styles :blue-line)]
    (fn []
      [:g
       [:defs
        (component-svg-filter-glow)
        (component-svg-pattern-hatch)]
       [:g
        [component-svg-path-1 style 0 -100]
        [component-svg-hex-thing style -140 0]
        [component-svg-hexagon style 150 0 40]
        [component-svg-hexagon style 50 0 40]
        [component-svg-twolines style]]])))

(defn ^export component-demo-circles [size event-chan]
  (let [style (styles :blue-line)]
    (fn []
      [:g
       [component-svg-circle-test style 120 -100]
       [component-svg-circle-test-2 style -120 50]
       [component-svg-circle-test-3 style 50 100]
       [component-svg-arc-thing style -100 -150]])))

(defn ^export component-demo-nibblets [size event-chan]
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

(defn ^export component-demo-hex-plane [size event-chan]
  (let [style-1 (merge (styles :blue-line) {:fill "url(#hatch)"})
        style-2 (styles :blue-flat)]
    (fn []
      [:g
       [:defs
        (component-svg-filter-glow)
        (component-svg-pattern-hatch)]
       [component-svg-hex-plane style-1 style-2]])))

(defn ^export component-demo-orbital-transformer [size event-chan]
  (let [style (styles :blue-line)]
    [:g style
     [:defs
      (component-svg-filter-glow)
      (component-svg-pattern-hatch)]
     [component-svg-hexagon (assoc style :fill "url(#hatch)") 0 0 40]
     [:circle {:cx 0 :cy 0 :r 45}]
     [component-orbiter style 0 0]]))

(defn ^export component-demo-path-unfold [size event-chan]
  (let [style (styles :blue-line)]
    [:g style
     [component-unfolder style 0 0]]))

(defn ^export component-demo-logo-unfold [size event-chan]
  (let [style-1 (styles :blue-line)
        style-2 (styles :blue-flat)]
    [:g style-1
     [:defs
      (component-svg-pattern-hatch)]
     [component-logo-unfold style-1 style-2 0 0]]))

(defn ^export component-demo-logo-unfold-2 [size event-chan]
  (let [style-1 (styles :blue-line)
        style-2 (styles :blue-flat)]
    [:g style-1
     [:defs
      (component-svg-pattern-hatch)]
     [component-logo-unfold-2 style-1 style-2 size 0 0]]))

(defn ^export component-demo-walker [size event-chan]
  (let [style-1 (styles :blue-line)
        style-2 (styles :blue-flat)]
    (fn []
      [:g style-1
       [:defs
        (component-svg-pattern-hatch "hatch" (colors :blue) "#383838")]
       [component-walker-demo-world style-1 style-2]])))

(defn ^export component-iconography [size event-chan]
  [:g
   [:defs
    (component-svg-pattern-hatch "hatch" (colors :blue) "#383838")]
   [:g (merge (styles :blue-line) (styles :blue-flat) {:stroke-width "2px"})
    [:g {:transform "translate(-100,200)"}
     (shapes :rocket)]
    [:g {:transform "translate(0,-100)" :fill (colors :blue)}
     (shapes :wreath)
     [:g {:transform "scale(-1,1)"}
      (shapes :wreath)]]
    [:g {:transform "translate(100,200)"}
     (shapes :temple)]]])

(defn ^export component-demo-hex-popout [size event-chan]
  (let [style-1 (merge (styles :blue-line) {:fill "url(#hatch)"})
        style-2 (styles :blue-flat)]
    (fn []
      [:g (merge style-1 style-2)
       [:defs
        (component-svg-pattern-hatch)]
       [component-hex-popout event-chan style-1 style-2 size]])))

(defn ^export component-demo-sci-menu [size event-chan]
  (fn []
    [:g (styles :cf-12)
     [:defs
      (component-svg-pattern-hatch)]
     [component-sci-menu event-chan size]]))

(def demos ["circles" component-demo-circles
            "nibblets" component-demo-nibblets
            "iconography" component-iconography
            ;"test: curved path" component-demo-curved-path
            "test: hex plane" component-demo-hex-plane
            ;"test: path unfold" component-demo-path-unfold
            "interactive: orbital transformer" component-demo-orbital-transformer
            "interactive: logo unfold" component-demo-logo-unfold
            "interactive: logo unfold #2" component-demo-logo-unfold-2
            ;"interactive: walker" component-demo-walker
            "interactive: sci menu" component-demo-sci-menu
            "interactive: hex popout" component-demo-hex-popout])

