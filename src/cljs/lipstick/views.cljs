(ns lipstick.views
  (:require [re-frame.core :as rf]
            [lipstick.routes :refer [url-for]]
            [lipstick.utils :refer [with-keys]]
            [lipstick.components.spec :refer [swagger-spec]]
            [lipstick.mock :as m]))

(defn home-page []
  (let [spec-data (rf/subscribe [:spec])]
    (fn []
      [:div.container
       [:p [:a {:href (url-for :about-page)} "About Page"]]
       [swagger-spec @spec-data]])))


(defn about-page []
  (let [schemas (rf/subscribe [:schemas])]
    (fn []
      [:div
       [:p [:a {:href (url-for :home-page)} "go to Home Page"]]
       [:h1 "This is the About Page."]
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
