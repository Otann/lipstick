(ns lipstick.components.source
  (:require [reagent.ratom :as r :include-macros true]
            [taoensso.timbre :as log]
            [re-frame.core :as rf]))

(defn atom-input [atom meta]
  [:input (into meta
                {:type "text"
                 :value @atom
                 :on-change #(reset! atom (-> % .-target .-value))})])

(defn config []
  (let [url (r/atom "swagger.yaml")
        config (rf/subscribe [:config])
        files (r/reaction (:files @config))
        on-click #(do (rf/dispatch [:load-swagger-spec @url])
                      (.preventDefault %))]
    (fn []
      (if @files
        [:form.config
         #_[:label "Spec options:"]
         [:div.input-group.input-block
          [:select.form-select
           {:on-change #(let [src (-> % .-target .-value)]
                         (log/debug "changed" src "url")
                         (rf/dispatch [:load-swagger-spec src])
                         )}
           (doall (for [{:keys [name src]} @files]
                    ^{:key name}
                    [:option {:value src} name]))]]]
        [:form
         [:div.input-group
          [atom-input url {:class "form-control"
                           :type "text"
                           :id "source"}]
          [:div.input-group-button
           [:button.btn {:type "submit"
                         :on-click on-click} "Load Spec"]]]]))))