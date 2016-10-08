(ns lipstick.components.schema
  (:require [goog.string :as gstring]
            [lipstick.components.collapsible :refer [collapsible]]
            [lipstick.utils :refer [with-keys]]))


(def ellipsis
  "A constant to use to indicate collapsed state"
  (gstring/unescapeEntities "&hellip;"))


(def open-bracket {:object "{"
                   :array "["
                   :enum "("})

(def close-bracket {:object "}"
                    :array "]"
                    :enum ")"})


(defn schema-type [schema]
  (if (= :enum (:type schema))
    (:item-type schema)
    (:type schema)))

(defn schema-name [schema]
  (if-let [name (:name schema)]
    [:span (schema-type schema) " " [:span.schema-name name]]
    (or (schema-type schema)
        (str schema))))


(defn open-brackets [{type :type :as schema}]
  (if-not (= type :array)
    (open-bracket type)
    [:span (open-bracket type)
     (schema-name (:item-schema schema))
     (open-brackets (-> schema :item-schema))]))


(defn close-brackets [{type :type :as schema}]
  (if-not (= type :array)
    (close-bracket type)
    [:span
     (close-brackets (-> schema :item-schema))
     (close-bracket type)]))


(defn field-labels [field-name properties]
  (if-not properties
    [[:span.field-name field-name] nil]
    (let [schema (:schema properties)
          optional (:optional properties)]
      [[:span.field-label {:class (when optional "optional")}
        [:span.field-name field-name] ": "
        (when optional "(Optional) ")
        (schema-name schema)
        (open-brackets schema)]
       (close-brackets schema)])))


(defn field-children [schema]
  (case (:type schema)
    :object (->> schema
                 :properties
                 (map (fn [[k v]] [k v])))

    :enum (->> schema
               :values
               (map #(do [% nil])))

    :array (field-children (-> schema :item-schema))

    nil))

(defn field [field-name properties]
  (let [[main-label tail-label] (field-labels field-name properties)
        schema (:schema properties)
        children (field-children schema)]
    (if (seq children)
      [collapsible {:collapsed true
                    :ellipsis ellipsis
                    :tail tail-label}
       main-label
       (->> children
            (map (fn [[k v]] [field k v]))
            (with-keys))]

      [:div.field main-label])))

(defn schema [schema]
  (let [schema-name (:name schema)]
    (case (:type schema)
      :object [collapsible {:collapsed false
                            :ellipsis ellipsis
                            :tail [:span "}"]}
               [:span [:span.schema-name schema-name] " {"]
               (->> schema :properties (map (fn [[k v]] [field k v])) with-keys)]

      :array [collapsible {:collapsed false
                           :ellipsis ellipsis
                           :tail [:span "}]"]}
              [:span [:span.schema-name schema-name] " [" [:span.schema-name (-> schema :item-schema :name)] "{"]
              (->> schema :item-schema :properties (map (fn [[k v]] [field k v])) with-keys)]

      :enum [collapsible {:collapsed false
                          :ellipsis ellipsis
                          :tail [:span ")"]}
             [:span [:span.schema-name schema-name] " ("]
             (->> schema :values (map #(do [:div.field %])) with-keys)]

      [:div.schema
       [:span.schema-name (or (-> schema :name)
                              (str schema))]])))