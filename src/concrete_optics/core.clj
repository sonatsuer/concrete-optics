(ns concrete-optics.core
  (:require [concrete-optics.base :as base] 
            [concrete-optics.iso.structures :as iso]))

;; Reexports from core
(def preview base/preview)
(def put base/put)
(def optic-compose base/optic-compose)

;; Reexports from iso 
(def mk-iso iso/mk-iso)
(def invert-iso iso/invert-iso)
(def eq iso/eq)

