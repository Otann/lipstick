(ns lipstick.components.collapsible
  (:require [reagent.core :as r]
            [lipstick.tools.utils :refer [with-keys join-classes]]))

(defn collapsible
  [{:keys [collapsed
           class
           ellipsis
           tail
           arrow-class
           arrow-open
           arrow-collapsed
           on-toggle
           render-collapsed]
    :or {collapsed true
         render-collapsed true
         arrow-open "-"
         arrow-collapsed "+"}}
   open-label children]
  [:div.collapsible
   {:class class}
   [:div.collapsible-label
    {:on-click on-toggle}
    (when (not-empty children)
      [:span.collapsible-arrow
       {:class arrow-class}
       (str (if collapsed arrow-collapsed arrow-open))])
    [:span.open-label open-label]
    (when collapsed [:span.close-label ellipsis tail])]
   [:div.collapsible-children
    {:class (when collapsed "collapsible-children-collapsed")}
    ; Important!
    ; Do not remove condition to cover
    ; circular dependencies!
    (when (or render-collapsed
              (not collapsed))
      [:div.collapsible-children-content
       (if (-> children first seq?)
         (with-keys children)
         children)])
    [:div tail]]])

(defn collapsible-stateful
  "Type2 Reagent component that can collapse
  it's content between two labels"
  [{:keys [collapsed] :as props}
   _ _]
  (let [state   (r/atom collapsed)
        toggle #(swap! state not)]
    (fn [_ open-label children]
      [collapsible
       (assoc props :collapsed @state
                    :on-toggle toggle)
       open-label
       children])))

(comment
  ; Example of usage
  [collapsible {:collapsed false
                :class "schema"
                :ellipsis "..."
                :tail "}"
                :arrow-class "arrow"}
   "Collapsed paragraph: {"
   [:p "This paragraph is enclosed"]])