(ns lipstick.components.source
  (:require [reagent.ratom :as r :include-macros true]
            [taoensso.timbre :as log]
            [re-frame.core :as rf]
            [lipstick.rfnext.source-selector :as selector]
            [lipstick.rfnext.sources :as sources]))

(defn source []
  (let [idx   (rf/subscribe [::selector/selected])
        names (rf/subscribe [::sources/names])
        on-change #(let [idx (-> % .-target .-value js/parseInt)]
                    (rf/dispatch [::selector/select idx]))]
    (fn []
      (if (and (not-empty @names)
               (second @names))
        [:form.source
         [:div.input-group.input-block
          [:select.form-select
           {:on-change on-change
            :value @idx}
           (doall (for [{:keys [idx name]} @names]
              ^{:key name}
              [:option {:value idx} name]))]]]))))




