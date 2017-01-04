(ns lipstick.components.source
  (:require [reagent.ratom :as r :include-macros true]
            [taoensso.timbre :as log]
            [re-frame.core :as rf]))

(defn source []
  (let [config (rf/subscribe [:config])
        files (r/reaction (:files @config))
        on-change #(let [src (-> % .-target .-value)]
                    (log/debug "changed" src "url")
                    (rf/dispatch [:load-swagger-spec src]))]
    (fn []
      (if (and (not-empty @files)
               (second @files))
        [:form.source
         [:div.input-group.input-block
          [:select.form-select
           {:on-change on-change}
           (doall (for [{:keys [name src]} @files]
                    ^{:key name}
                    [:option {:value src} name]))]]]))))