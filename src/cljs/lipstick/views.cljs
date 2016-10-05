(ns lipstick.views
  (:require [re-frame.core :as rf]
            [lipstick.routes :refer [url-for]]
            [lipstick.components.tree-view :refer [tree]]))


(defn home-page []
  (let [spec (rf/subscribe [:spec])
        pet (-> @spec :definitions :Pet)]
    (fn []
      [:div (str "This is the Home Page.")
       [:div [:a {:href (url-for :about-page)}
              "go to About Page"]]
       [:code (str pet)]
       [tree "foo"
        [:div
         [:h1 "Header"]
         [tree "bar"
          [tree "baz"]]]]])))


(defn about-page []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href (url-for :home-page)}
            "go to Home Page"]]]))


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
