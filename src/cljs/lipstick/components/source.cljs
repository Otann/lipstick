(ns lipstick.components.source
  (:require [reagent.ratom :as r :include-macros true]
            [taoensso.timbre :as log]
            [re-frame.core :as rf]))

(defn source []
  (let [specs (rf/subscribe [:specs])
        ;files (r/reaction (:files @config))
        on-change #(let [id (-> % .-target .-value js/parseInt)]
                    (log/debug "changed" id "url")
                    (rf/dispatch [:ui-selected-spec id]))]
    (fn []
      (if (and (not-empty @specs)
               (second @specs))
        [:form.source
         [:div.input-group.input-block
          [:select.form-select
           {:on-change on-change}
           (doall (for [{:keys [name id]} @specs]
                    ^{:key name}
                    [:option {:value id} name]))]]]))))