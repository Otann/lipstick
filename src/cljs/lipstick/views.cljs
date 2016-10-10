(ns lipstick.views
  (:require [re-frame.core :as rf]
            [reagent.ratom :as r]
            [lipstick.routes :refer [url-for]]
            [lipstick.utils :refer [with-keys]]
            [lipstick.components.spec :refer [swagger-spec]]
            [lipstick.mock :as m]
            [lipstick.swagger :as swag]))

(defn home-page []
  (let [spec-data (rf/subscribe [:spec])]
    (fn []
      [:div.container
       [:p [:a {:href (url-for :about-page)} "More mocked examples"]]
       [swagger-spec @spec-data]])))


(defn about-page []
  (let [spec-data (rf/subscribe [:spec])
        schemas (r/reaction (map #(apply swag/swag->schema %)
                                 (get @spec-data "definitions")))]
    (fn []
      [:div
       [:p [:a {:href (url-for :home-page)} "back to main page"]]
       [m/example @schemas]])))

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
