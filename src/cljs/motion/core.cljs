(ns motion.core
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs.core.async :refer [<! close! timeout chan] :as async]
              [clojure.string :as string]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant])
    (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

;; -------------------------
;; Functions

(def m js/Math)
(def tau (* m.PI 2))

(defn pol2crt [cx cy r a]
  [(+ cx (* r (m.cos a)))
   (+ cy (* r (m.sin a)))])

(defn svg-arc [cx cy r as ae]
  (let [[xs ys] (pol2crt cx cy r (+ (mod ae tau) tau))
        [xe ye] (pol2crt cx cy r (+ (mod as tau) tau))
        direction (if (<= (- ae as) m.PI) 0 1)
        path ["M" (m.round xs) (m.round ys)
              "A" r r 0 direction 0 (m.round xe) (m.round ye)]]
    (clojure.string/join " " path)))

(defn timeline [duration & {:keys [a c interval] :or {a (atom 0) c (chan) interval 16}}]
    (reset! a 0)
    (go-loop []
             (let [new-val (swap! a (fn [p] (if p (+ p interval))))]
               (if (and new-val (< new-val duration))
                 (do
                   (<! (timeout interval))
                   (recur))
                 (do
                   (reset! a nil)
                   (if c (close! c))))))
  [a c])

(defn g-trans [x y]
  {:transform (str "translate(" x "," y ")")})

(def default-style {:fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"})

;; -------------------------
;; Components

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

(defn component-svg-path-1 [x y]
  (let [over (atom false)]
    (fn []
      [:g (merge (g-trans x y) {:on-mouse-over (fn [ev] (reset! over true) nil) :on-mouse-out (fn [ev] (reset! over false) nil)})
       [:path {:d (js/roundPathCorners "M -100 -100 L 100 -100 L 120 -80 L 200 -80 L 200 0 L 0 0 L -20 -20 L -100 -20 Z" 5 false) :fill "url(#hatch)" :stroke (if @over "#E6A441" "#41A4E6") :stroke-width "2px" :stroke-linecap "round"}]])))

(defn component-svg-hexagon [x y r & {:keys [style] :or {style default-style}}]
  (let [seg (/ m.PI 3)
        coords (map #(let [a (+ (* % seg) (/ seg 2))] (str (m.round (* r (m.sin a))) " " (m.round (* r (m.cos a))))) (range 6))]
  [:g (g-trans x y)
   [:path (merge {:d (js/roundPathCorners (str "M " (clojure.string/join " L " coords) " Z") 5 false)} style)]]))

(defn component-svg-top [ow]
  [:g
   [:path {:d (js/roundPathCorners (str "M 0 45 L 48 45 L 98 5 L " ow " 5") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]
   [:path {:d (js/roundPathCorners (str "M 0 50 L 50 50 L 100 10 L " ow " 10") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]])

(defn component-svg-circle-test [t x y]
  (let [t-scale 0.3]
    [:g (g-trans x y)
     [:circle {:cx 0 :cy 0 :r (+ 35 (* (m.sin (* (+ @t 100) t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]
     [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* @t t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "4px"}]
     [:circle {:cx 0 :cy 0 :r (+ 45 (* (m.sin (* (+ @t 200) t-scale)) 2)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]]))

(defn component-svg-circle-test-2 [t x y]
  (let [t-scale 0.1]
    [:g (g-trans x y)
     [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* (+ @t 100) t-scale)) 7)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]
     [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* (+ @t 200) t-scale)) 7)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]
     [:circle {:cx 0 :cy 0 :r (+ 40 (* (m.sin (* (+ @t 300) t-scale)) 7)) :fill "none" :stroke "#41A4E6" :stroke-width "1px"}]]))

(defn component-svg-circle-test-3 [x y]
  (let [[t c] (timeline 2000)]
    (go-loop [[t c] [t c]]
      (<! c)
      (recur (timeline 2000 :a t)))
    (fn []
      (if @t
        [:g (g-trans x y)
         [:circle {:cx 0 :cy 0 :r (+ (/ @t 10) 20) :fill "none" :stroke "#41A4E6" :stroke-width "2px"}]]))))

(defn component-svg-arc [x y r as ae t]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width t :d (svg-arc 0 0 r as ae)}]])

(defn component-svg-arc-thing [t x y]
  (let [p (* m.PI 2 (/ (mod @t 100) 100))]
    [:g (g-trans x y)
     [:path {:fill "none" :stroke "#41A4E6" :stroke-width "5" :d (svg-arc 0 0 51 (- p 1) p)}]
     [:circle {:cx 0 :cy 0 :r 53 :fill "none" :stroke "#41A4E6" :stroke-width "1px" :stroke-linecap "round"}]]))

(defn component-svg-hex-thing [x y]
  (let [over (atom false)]
    (fn []
      [:g {:on-mouse-over (fn [ev] (reset! over true) nil) :on-mouse-out (fn [ev] (reset! over false) nil)}
       (component-svg-hexagon x y 40 :style (assoc default-style :fill "url(#hatch)"))
       (if @over
         [:g
          (component-svg-arc x y 78 (* m.PI .75) (* m.PI 1.25) 2)
          (component-svg-arc x y 78 (* m.PI -0.25) (* m.PI 0.25) 2)
          (component-svg-arc x y 70 (* m.PI .75) (* m.PI 1.25) 5)
          (component-svg-arc x y 70 (* m.PI -0.25) (* m.PI 0.25) 5)])])))

(defn component-svg-x [x y]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 0 0 L 15 15 Z"}]
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 15 0 L 0 15 Z"}]])

(defn component-svg-+ [x y]
  [:g (g-trans x y)
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 7.5 0 L 7.5 15 Z"}]
   [:path {:fill "none" :stroke "#41A4E6" :stroke-width "1" :d "M 0 7.5 L 15 7.5 Z"}]])

(defn component-demo-old [size]
  (let [[ow oh] @size
        [t c] (timeline -1)]
    [:g (merge (g-trans ow oh) (comment {:filter "url(#glowfilter)"}))
     (component-svg-circle-test t 300 0)
     (component-svg-circle-test-2 t -200 150)
     [component-svg-circle-test-3 0 200]
     (component-svg-arc-thing t -100 0)
     (component-svg-x 190 130)
     (component-svg-x 240 220)
     (component-svg-x 220 240)
     (component-svg-+ -200 -200)
     (component-svg-+ -230 -230)
     (component-svg-+ -190 -250)]))

(defn component-demo-curved-path [size]
  (let [[ow oh] @size]
    [:g
     [:defs
      (component-svg-filter-glow)
      (component-svg-pattern-hatch)]
     [:g (merge (g-trans ow oh))
      [component-svg-path-1 0 -100]
      [component-svg-hex-thing -80 0]
      [component-svg-hexagon 180 0 40]
      [component-svg-hexagon 80 0 40]
      [:g {:transform "translate(0,100)"}
       [:path {:d (js/roundPathCorners (str "M -100 45 L 48 45 L 98 5 L 200 5") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]
       [:path {:d (js/roundPathCorners (str "M -100 50 L 50 50 L 100 10 L 200 10") 5 false) :fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]]]]))

(def demos {"curved-path" component-demo-curved-path
            "old-stuff" component-demo-old})

(defn component-game [size t]
  (let [[ow oh] (map #(/ % 2) @size)
        frag (get (string/split js/document.location.href "?") 1)]
    [:div
     [:svg {:x 0 :y 0 :width "100%" :height "100%" :style {:top "0px" :left "0px" :position "absolute"}}
      
      (component-svg-top (* ow 2))
      
      (if (get demos frag)
        [:g {:transform (str "translate(" (* ow -1) "," (* oh -1) ")")}
         [(demos frag) size]]
        [:g (merge (g-trans ow oh) {:fill "#41A4E6" :text-anchor "middle"})
         [:text "Demo not found."]])]
     
     [:div {:style {:top "10px" :left "10px" :position "absolute" :font-size "20px" :padding "0px"}}
      [:a {:href "/"} "<-"]]]))

;; -------------------------
;; Views

(defn component-page-contents []
  [:div#contents [:h2 "demos"]
   [:ul
    [:li [:a {:href "/v?curved-path"} "curved path"]]
    [:li [:a {:href "/v?old-stuff"} "old stuff"]]]])

(defn component-page-viewer []
  [component-game (atom [(.-innerWidth js/window) (.-innerHeight js/window)]) (atom 0)])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'component-page-contents))

(secretary/defroute "/v" []
  (session/put! :current-page #'component-page-viewer))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
