(defproject lipstick "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [binaryage/devtools "0.8.2"]               ; TODO: move to dev-deps
                 [com.taoensso/timbre "4.1.4"]              ; Clojure/Script logging

                 [reagent "0.6.0"]                          ; rendering
                 [re-frame "0.8.0"]                         ; data-flow
                 [bidi "1.20.3"]                            ; frontend routing
                 [kibu/pushy "0.3.2"]                       ; HTML5 history

                 [cljs-http "0.1.41"]                       ; http async client
                 [cljsjs/js-yaml "3.3.1-0"]                 ; yaml parser
                 ]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-less "1.7.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :profiles
  {:dev {:dependencies []
         :figwheel     {:css-dirs ["resources/public/css"]}
         :plugins      [[lein-figwheel "0.5.7"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "lipstick.core/mount-root"}
     :compiler     {:main       lipstick.core
                    :output-to  "resources/public/js/compiled/app.js"
                    :output-dir "resources/public/js/compiled/out"
                    :asset-path "js/compiled/out"
                    :closure-defines {goog.DEBUG true}
                    :source-map-timestamp true}}
    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            lipstick.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})
