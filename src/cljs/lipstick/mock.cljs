(ns lipstick.mock)

;; Schema = Definition - named set of requirements (definition?)
;; Type - type of field

(def Gender {:name "Gender"
             :type :enum
             :item-type "string"
             :values ["Male", "Female"]})

(def Trait {:type :enum
            :item-type "int"
            :values [1, 2, 3, 4, 5]})

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