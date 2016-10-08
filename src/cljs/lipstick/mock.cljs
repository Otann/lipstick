(ns lipstick.mock
  (:require [lipstick.routes :refer [url-for]]
            [lipstick.components.schema :refer [schema]]))

;; Schema = Definition - named set of requirements (definition?)
;; Type - type of field

(def Gender {:name "Gender"
             :type :enum
             :item-type "string"
             :values ["Male", "Female"]})

(def Trait {:type :enum
            :item-type "int"
            :values [1, 2, 3]})

(def SiblingEnum {:name "SiblingType"
                  :type :enum
                  :item-type "string"
                  :values ["Sister" "Brother"]})


(def Address {:name "Address"
              :type :object
              :properties {"city" {:schema "string"}
                           "street" {:schema "string"}
                           "zip" {:schema "string"}
                           "location" {:schema {:name "Point"
                                                :type :array
                                                :item-schema {:type :array
                                                              :item-schema {:type "int"}}}}}})

(def Sibling {:name "Sibling"
              :type :object
              :properties {"first_name" {:schema "string"
                                         :description "Name given on birth"}
                           "last_name" {:schema "string"
                                        :description "Name inherited from parents"}
                           "relation" {:schema SiblingEnum}}})


(def Person {:name "Person"
             :type :object
             :properties {"first_name" {:schema "string"}
                          "last_name" {:schema "string"}
                          "groups" {:schema {:type :array :item-schema Trait}}
                          "gender" {:schema Gender :optional true}
                          "age" {:schema "int64"}
                          "home" {:schema Address}
                          "sibs" {:schema {:type :array :item-schema Sibling}}}})

(defn example [schemas]
  [:div
   [:h1 "This is an visualization of collapsible schemas"]

   [:h2 "Definitions from /swagger.yaml"]
   (doall (for [schema-data schemas]
            ^{:key (:name schema-data)}
            [schema schema-data]))

   [:h2 "Examples"]

   [:h3 "Object Example"]
   [schema Person]

   [:h3 "Array Example"]
   [schema {:name "Siblings" :type :array :item-schema Sibling}]

   [:h3 "Enum Example"]
   [schema Gender]])