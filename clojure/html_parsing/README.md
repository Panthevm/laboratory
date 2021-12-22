# Information

[FHIR resources list](https://www.hl7.org/fhir/resourcelist.html)

[Example resource (PaymentNotice)](https://www.hl7.org/fhir/paymentnotice.html)

[CSV file](/clojure/html_parsing/output.csv)

# Example output

``` edn
{;; ....
 "PaymentNotice"
 {:response      {:Cardinality "0..1", :Type "Reference"},
  :amount        {:Cardinality "1..1", :Type "Money"},
  :request       {:Cardinality "0..1", :Type "Reference"},
  :payment       {:Cardinality "1..1", :Type "Reference"},
  :recipient     {:Cardinality "1..1", :Type "Reference"},
  :created       {:Cardinality "1..1", :Type "dateTime"},
  :paymentStatus {:Cardinality "0..1", :Type "CodeableConcept"},
  :status        {:Cardinality "1..1", :Type "code"},
  :payee         {:Cardinality "0..1", :Type "Reference"},
  :paymentDate   {:Cardinality "0..1", :Type "date"},
  :identifier    {:Cardinality "0..*", :Type "Identifier"},
  :provider      {:Cardinality "0..1", :Type "Reference"}}
 ;; ....
 }
```
