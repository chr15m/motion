(ns motion.styles)

(def colors {:background "#383838"
             :white "#aeaeae"
             :blue "#41A4E6"
             :red "#BB5C3F"
             :yellow "#BBAA3F"
             :green "#3FBB3F"
             :orange "#E6A441"})

(def styles {:blue-line {:fill "none" :stroke (colors :blue) :stroke-width "1px" :stroke-linecap "round"}
             :blue-flat {:fill (colors :blue) :fill-opacity "0.3" :stroke-linecap "round"}
             :cf-12 {:fill "none" :stroke (colors :white) :stroke-width 1 :stroke-linecap "round"}})


