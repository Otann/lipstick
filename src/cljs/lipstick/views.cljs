(ns lipstick.views
  (:require [re-frame.core :as rf]
            [reagent.ratom :as r]
            [lipstick.routes :refer [url-for]]
            [lipstick.tools.utils :refer [with-keys]]
            [lipstick.components.spec :refer [swagger-spec]]
            [lipstick.components.schema :refer [schema]]
            [lipstick.components.source :refer [source]]
            [lipstick.components.auth :refer [auth-control]]
            [taoensso.timbre :as log]))


(defn home-page []
  (let [spec-data (rf/subscribe [:spec])]
    (fn []
      (log/debug "Rendering home page")
      [:div.container
       [:div.controls
        [auth-control]
        [source]]
       (if-let [spec @spec-data]
         [swagger-spec spec]
         [:p "loading spec..."])])))


(defn about-page []
  (let [spec-data (rf/subscribe [:spec])]
    (fn []
      [:div
       [:p [:a {:href (url-for :home-page)} "back to main page"]]
       [:div
        [:h1 "This is an visualization of collapsible schemas"]
        [:h2 "Definitions from /swagger.yaml"]
        (doall (for [[schema-name schema-data] (:definitions @spec-data)]
                 ^{:key schema-name}
                 [schema schema-name schema-data @spec-data]))]])))


(defn show-page
  [page-name]
  (case page-name
    :root       [home-page]
    :home-page  [home-page]
    :about-page [about-page]
    :auth [:p "auth page"]
    [:div (str "404? - " page-name)]))


(defn root-view []
  (let [active-page (rf/subscribe [:active-page])]
    (fn []
      (log/debug "Returning vdom for main panel")
      [show-page @active-page])))
