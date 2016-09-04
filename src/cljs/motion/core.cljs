(ns motion.core
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs.core.async :refer [<! put! close! timeout chan] :as async]
              [clojure.string :as string]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [motion.utils :refer [svg-arc timeline g-trans make-link]]
              [motion.demos :as demos])
    (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def m js/Math)

(defn handle-svg-click [event-chan [ow oh] ev]
  (when (= (-> ev .-target .-tagName .toLowerCase)
         "svg")
    (put! event-chan ["click" [(- (.-clientX ev) ow) (- (.-clientY ev) oh)]])))

;; -------------------------
;; Components

(defn component-svg-top [ow]
  (let [style {:fill "none" :stroke "#555" :stroke-width "2px" :stroke-linecap "round"}]
    [:g
     [:path (merge style {:d (js/roundPathCorners (str "M 0 45 L 48 45 L 98 5 L " ow " 5") 5 false)})]
     [:path (merge style {:d (js/roundPathCorners (str "M 0 50 L 50 50 L 100 10 L " ow " 10") 5 false)})]]))

(defn component-svg-main [size demo demo-name]
  (let [event-chan (chan)]
  (fn []
    (let [[ow oh] (map #(int (/ % 2)) @size)]
      [:div
       [:svg {:x 0 :y 0 :width "100%" :height "100%" :style {:top "0px" :left "0px" :position "absolute"} :on-click (partial handle-svg-click event-chan [ow oh])}

        (component-svg-top (* ow 2))

       [:g (g-trans ow oh)
         [demo size event-chan]]]

       [:div#back-link
        [:a {:href (make-link "/")} "<-"]]]))))

;; -------------------------
;; Views

(defn component-page-contents []
  [:div#contents
   [:p#contact-link [:a {:href "contact"} "contact"]]
   [:h2 "demos"]
   [:ol
    (doall (map (fn [[d f]]
                  [:li {:key d} [:a {:href (str "v?" d)} d]])
                (partition 2 demos/demos)))]])

(defn component-page-contact []
  [:div#contents.contact
   [:p "hello."]
   [:p "are you aerospace industry? do you build and/or operate machines that run above the clouds?"]
   [:p "i think it would be cool to make badass interfaces for space gear. it's a dream of mine. if you think that would be cool too then hit me up."]
   [:p "the demos here are not animations, they are functional software. i have decades of experience interfacing with different systems and protocols. i am expensive but fast and i have a high focus on detail."]
   [:p "if this sounds good to you let's talk about how we can make some neat UIs for your space gear & systems."]
   [:p [:a {:href "mailto:chris@mccormickit.com"} "email me"] "."]])

(defn component-page-viewer []
  (let [demo-name (js/unescape (get (string/split js/document.location.href "?") 1))
        size (atom [(.-innerWidth js/window) (.-innerHeight js/window)])
        demo (get (apply hash-map demos/demos) demo-name)]
    (fn []
      (if demo
        [component-svg-main size demo demo-name]
        [:div#contents "Demo not found."]))))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute #"(.*)/$" []
  (session/put! :current-page #'component-page-contents))

(secretary/defroute #"(.*)/v$" []
  (session/put! :current-page #'component-page-viewer))

(secretary/defroute #"(.*)/contact$" []
  (session/put! :current-page #'component-page-contact))

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

