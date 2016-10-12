(ns lipstick.config
  (:require [taoensso.timbre :as log]))


(def debug? ^boolean js/goog.DEBUG)


(def devtools-level-to-fn
  {:fatal js/console.error,
   :error js/console.error,
   :warn  js/console.warn,
   :info  js/console.info,
   :debug js/console.debug,
   :trace js/console.trace})

(def devtools-appender
  "Simple js/console appender which avoids pr-str
  and uses cljs-devtools to format output"
  {:enabled?   true
   :async?     false
   :min-level  nil
   :rate-limit nil
   :output-fn  nil
   :fn
   (fn [data]
     (let [{:keys [level ?ns-str vargs_]} data
           vargs (list* (str ?ns-str ":") (force vargs_))
           f (devtools-level-to-fn level js/console.log)]
       (.apply f js/console (to-array vargs))))})

(when (= "Google Inc." js/navigator.vendor)
  (log/merge-config! {:appenders {:console devtools-appender}}))


