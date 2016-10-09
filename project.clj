(defproject lipstick "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [binaryage/devtools "0.8.2"]               ; TODO: move to dev-deps
                 [com.taoensso/timbre "4.1.4"]              ; Clojure/Script logging

                 [reagent "0.6.0"]                          ; rendering
                 [re-frame "0.8.0"]                         ; data-flow
                 [bidi "1.20.3"]                            ; frontend routing
                 [secretary "1.2.3"]                        ; routing

                 [cljs-http "0.1.41"]                       ; http async client
                 [markdown-clj "0.9.89"]                    ; markdown parser
                 [cljsjs/js-yaml "3.3.1-0"]                 ; yaml parser
                 ]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-figwheel "0.5.7"]
            [lein-less "1.7.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js" "target"]

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :npm {:dependencies [[primer-css "4.2.0"]]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "lipstick.core/mount-root"}
     :compiler     {:main       lipstick.core
                    :output-to  "resources/public/js/app.js"
                    :output-dir "resources/public/js/out"
                    :asset-path "js/out"
                    :closure-defines {goog.DEBUG true}
                    :source-map-timestamp true}}
    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            lipstick.core
                    :output-to       "target/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})
