(ns lipstick.components.source
  (:require [reagent.ratom :as r :include-macros true]
            [taoensso.timbre :as log]
            [re-frame.core :as rf]))

(defn source []
  (let [specs (rf/subscribe [:specs])
        selected-id (rf/subscribe [:ui-state :selected-spec-id])
        on-change #(let [id (-> % .-target .-value js/parseInt)]
                    (rf/dispatch [:ui-selected-spec id]))]
    (fn []
      (if (and (not-empty @specs)
               (second @specs))
        [:form.source
         [:div.input-group.input-block
          [:select.form-select
           {:on-change on-change
            :value (or @selected-id "")}
           (doall (for [{:keys [name id]} @specs]
                    ^{:key name}
                    [:option {:value id} name]))]]]))))




