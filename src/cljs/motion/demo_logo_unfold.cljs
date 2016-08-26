(ns motion.demo-logo-unfold
  (:require [reagent.core :as reagent :refer [atom]]
            [motion.utils :refer [timeline svg-path partial-path svg-arc]]))

(def m js/Math)

(defn handle-planet-click [unfolder ev]
  (if (= (deref (unfolder :time)) 0)
    (timeline 2000 :atoms unfolder)
    (do
      (reset! (unfolder :go) false)
      (reset! (unfolder :time) 0))))

(defn component-wreath-half []
  ; original wreath by Piotr Michal Jaworski (CC public domain)
  [:g
   [:path {:d "m 15.455599,41.134923 c 15.86426,-10.944985 29.03509,-1.334618 41.096511,9.53329 -15.570231,4.237989 -32.059131,6.160431 -41.096511,-9.53329 z"}]
   [:path {:d "M 19.268589,31.714782 C 19.972519,12.429451 35.530039,7.5782488 51.43246,4.3694538 45.5329,19.415171 37.230659,33.817273 19.268589,31.714782 z"}]
   [:path {:d "m 43.744369,32.444033 c 14.884361,-12.24904 28.81875,-3.788191 41.75374,6.019635 -15.15709,5.541134 -31.424909,8.852781 -41.75374,-6.019635 z"}]
   [:path {:d "M 57.54894,23.299472 C 48.27369,6.3860678 59.14076,-5.7772622 71.145179,-16.708356 73.789449,-0.76192127 74.041909,15.867629 57.54894,23.299472 z"}]
   [:path {:d "M 69.593999,22.924835 C 80.544799,7.0488738 96.26363,11.34679 111.39108,17.215005 98.33784,26.716504 83.605029,34.380241 69.593999,22.924835 z"}]
   [:path {:d "M 85.061619,8.7650046 C 70.891659,-4.3068632 77.324959,-19.304914 85.228179,-33.497938 92.80518,-19.22626 98.33219,-3.5436712 85.061619,8.7650046 z"}]
   [:path {:d "m 92.59044,7.1650578 c 7.73701,-17.6728898 23.9848,-16.4487373 39.95013,-13.570263 -11.01119,11.81542 -24.02016,22.1466772 -39.95013,13.570263 z"}]
   [:path {:d "M 105.49878,-9.9200075 C 89.154719,-20.131977 92.69989,-36.06525 97.8385,-51.480609 c 10.08759,12.61608 18.42197,26.999522 7.66028,41.5606015 z"}]
   [:path {:d "m 111.20416,-12.308849 c 4.26158,-18.819936 20.4473,-20.696276 36.66789,-20.894624 -8.58231,13.688497 -19.4065,26.2981148 -36.66789,20.894624 z"}]
   [:path {:d "m 116.05803,-39.96592 c -17.40368,-8.269936 -15.70276,-24.50513 -12.35968,-40.408606 11.4631,11.375946 21.38648,24.70875 12.35968,40.408606 z"}]
   [:path {:d "m 118.95833,-34.585127 c 2.00084,-19.193719 17.84968,-22.982365 33.93204,-25.10905 -6.89921,14.612631 -16.15231,28.420732 -33.93204,25.10905 z"}]
   [:path {:d "m 123.37383,-71.428964 c -18.80795,-4.16471 -20.77085,-20.37025 -21.05915,-36.620086 13.71031,8.522112 26.35627,19.296304 21.05915,36.620086 z"}]
   [:path {:d "m 127.05441,-57.272619 c -5.44294,-18.511653 7.77288,-28.059875 21.83614,-36.160336 -0.82727,16.143764 -4.13688,32.441366 -21.83614,36.160336 z"}]
   [:path {:d "m 115.97517,-107.78976 c -19.23784,0.96517 -25.4072,-14.14376 -29.973211,-29.73897 15.471371,4.58739 30.510521,11.62863 29.973211,29.73897 z"}]
   [:path {:d "m 128.3613,-87.016867 c -11.56123,-15.435423 -2.51308,-29.011403 7.83438,-41.527983 4.86042,15.41411 7.44895,31.842147 -7.83438,41.527983 z"}]
   [:path {:d "m 107.55983,-135.24915 c -19.229891,-1.11156 -23.742981,-16.79725 -26.609851,-32.79378 14.889701,6.22649 29.086501,14.8461 26.609851,32.79378 z"}]
   [:path {:d "m 119.83422,-118.5948 c -14.44868,-12.76193 -8.34045,-27.8958 -0.74528,-42.25668 7.88313,14.10425 13.7472,29.66347 0.74528,42.25668 z"}]])

(defn component-logo-unfold [style-1 style-2 x y]
  (let [unfolder {:time (atom 0) :go (atom false)}]
    (fn []
      (let [u (deref (unfolder :time)) 
            t (m.min (/ u 1000) 1)]
        [:g (merge style-1 {:transform (str "translate(" x "," y ")")})
         [:g
          [:path {:d (js/roundPathCorners (svg-path (partial-path t [ 53  43   107  96    107  193])) 5 false) :stroke-width "2px"}]
          [:path {:d (js/roundPathCorners (svg-path (partial-path t [ 50  50   100  100   100  193])) 5 false) :stroke-width "2px"}]

          [:path {:d (js/roundPathCorners (svg-path (partial-path t [-53  43  -107  96   -107  193])) 5 false) :stroke-width "2px"}]
          [:path {:d (js/roundPathCorners (svg-path (partial-path t [-50  50  -100  100  -100  193])) 5 false) :stroke-width "2px"}]

          ;[:path {:d (js/roundPathCorners (svg-path (partial-path t [ 53  43   103  93    180  93])) 5 false) :stroke-width "2px"}]
          ;[:path {:d (js/roundPathCorners (svg-path (partial-path t [ 50  50   100  100   180  100])) 5 false) :stroke-width "2px"}]
          ;[:path {:d (js/roundPathCorners (svg-path (partial-path t [-53 -43  -103 -93   -180 -93])) 5 false) :stroke-width "2px"}]
          ;[:path {:d (js/roundPathCorners (svg-path (partial-path t [-50 -50  -100 -100  -180 -100])) 5 false) :stroke-width "2px"}]

          ;[:path {:d (js/roundPathCorners (svg-path (partial-path t [ 50  0   100  0   120 20   180  20])) 5 false) :stroke-width "2px"}]
          ]
         (if (or (= (mod (int (/ u 100)) 2) 1) (> u 1000))
           [:g (merge style-2 {:stroke "none"})
            [:g
             [component-wreath-half]]
            [:g {:transform "scale(-1,1)"}
             [component-wreath-half]]])
         [:g {:on-click (partial handle-planet-click unfolder)}
          [:circle {:cx 0 :cy 0 :r 80 :fill "#383838" :stroke-width "2px"}]
          (if (> u 200)
            [:g {:transform "rotate(60),scale(0.3,1)"}
             [:path {:d (svg-arc 0 0 100 (* m.PI -0.359 2) (* m.PI 2 0.359)) :stroke-width "3px" :stroke-opacity (/ u 2000)}]])]
         [:g (merge style-2 {:fill-opacity (/ u 2000) :stroke "none"})
          [:text {:y 150 :text-anchor "middle"} "veritate"]
          [:text {:y 200 :text-anchor "middle"} "ad astra"]]]))))

