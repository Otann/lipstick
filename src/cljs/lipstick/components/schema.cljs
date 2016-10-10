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

(defn deref-schema [schema all-schemas]
  (if (= :reference (:type schema))
    (get all-schemas (:name schema))
    schema))

(defn open-brackets [{type :type :as schema} all-schemas]
  (if-not (= type :array)
    (open-bracket type)
    (let [derefed (deref-schema (:item-schema schema) all-schemas)]
      [:span
       (open-bracket type)
       (schema-name derefed)
       (open-brackets derefed all-schemas)])))


(defn close-brackets [{type :type :as schema} all-schemas]
  (if-not (= type :array)
    (close-bracket type)
    (let [derefed (deref-schema (:item-schema schema) all-schemas)]
      [:span
       (close-brackets derefed all-schemas)
       (close-bracket type)])))


(defn field-labels [field-name optional schema all-schemas]
  (if-not schema
    [[:span.field-name field-name] nil]
    [[:span.field-label {:class (when optional "optional")}
      [:span.field-name field-name] ": "
      (when optional "(Optional) ")
      (schema-name schema)
      (open-brackets schema all-schemas)]
     (close-brackets schema all-schemas)]))


(defn field-children [schema all-schemas]
  (case (:type schema)
    :object (->> schema
                 :properties
                 (map (fn [[k v]] [k v])))

    :enum (->> schema
               :values
               (map #(do [% nil])))

    :array (field-children (-> schema :item-schema) all-schemas)

    :reference (field-children (get all-schemas (:name schema)) all-schemas)

    nil))

(defn field [field-name properties all-schemas]
  (let [schema (deref-schema (:schema properties) all-schemas)
        [main-label tail-label] (field-labels field-name
                                              (:optional properties)
                                              schema
                                              all-schemas)
        children (field-children schema all-schemas)]
    (if (seq children)
      [collapsible {:collapsed true
                    :ellipsis ellipsis
                    :tail tail-label}
       main-label
       (->> children
            (map (fn [[k v]] [field k v all-schemas]))
            (with-keys))]

      [:div.field main-label tail-label])))

(defn schema [schema all-schemas]
  (let [schema-name (:name schema)]
    (case (:type schema)
      :object [collapsible {:class "schema"
                            :collapsed false
                            :ellipsis ellipsis
                            :tail [:span "}"]}
               [:span [:span.schema-name schema-name] " {"]
               (->> schema
                    :properties
                    (map (fn [[k v]] [field k v all-schemas])) with-keys)]

      :array [collapsible {:collapsed false
                           :ellipsis ellipsis
                           :tail [:span "}]"]}
              [:span [:span.schema-name schema-name] " [" [:span.schema-name (-> schema :item-schema :name)] "{"]
              (->> schema :item-schema :properties (map (fn [[k v]] [field k v all-schemas])) with-keys)]

      :enum [collapsible {:collapsed false
                          :ellipsis ellipsis
                          :tail [:span ")"]}
             [:span [:span.schema-name schema-name] " ("]
             (->> schema :values (map #(do [:div.field %])) with-keys)]

      [:div.schema
       [:span.schema-name (or (-> schema :name)
                              (str schema))]])))