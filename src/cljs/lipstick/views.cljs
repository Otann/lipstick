(ns lipstick.views
  (:require [re-frame.core :as rf]
            [reagent.ratom :as r]
            [lipstick.routes :refer [url-for]]
            [lipstick.utils :refer [with-keys]]
            [lipstick.components.spec :refer [swagger-spec]]
            [lipstick.components.schema :refer [schema]]
            [lipstick.components.source :refer [source]]))


(defn home-page []
  (let [spec-data (rf/subscribe [:spec])]
    (fn []
      [:div.container
       (if-let [spec @spec-data]
         [swagger-spec spec]
         [:p "loading spec..."])
       [source]])))


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
    [:div (str "404? - " page-name)]))


(defn main-panel []
  (let [active-page (rf/subscribe [:active-page])]
    (fn [] [show-page @active-page])))
