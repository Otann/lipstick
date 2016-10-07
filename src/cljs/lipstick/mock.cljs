(ns lipstick.mock)

;; Schema = Definition - named set of requirements (definition?)
;; Type - type of field

(def Int {:name "int"
          :type :primitive})

(def String {:name "string"
             :type :primitive})

(def Gender {:name "Gender"
             :type :enum
             :item-schema :string
             :values ["Male", "Female"]})


(def SiblingEnum {:name "SiblingType"
                  :type :enum
                  :item-schema :string
                  :values ["Sister" "Brother"]})


(def Address {:name "Address"
              :type :object
              :properties {"city" {:schema String}
                           "street" {:schema String}
                           "zip" {:schema String}}})

(def Sibling {:name "Sibling"
              :type :object
              :properties {"first_name" {:schema String}
                           "last_name" {:schema String}
                           "relation" {:schema SiblingEnum}}})


(def Person {:name "Person"
             :type :object
             :properties {"first_name" {:schema "string"}
                          "last_name" {:schema String}
                          "gender" {:schema Gender :optional true}
                          "age" {:schema "int64"}
                          "home" {:schema Address}
                          "sibs" {:schema {:type :array :item-schema Sibling} :optional true}}})


(def SwaggerSample {:ErrorModel {:type :object
                                 :properties {"message" {:type :string
                                                         :required true}
                                              "code" {:type :integer
                                                      :required true
                                                      :minimum 100
                                                      :maximum 600}}}})