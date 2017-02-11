(ns devstopfix.monster-test
  (:require [clojure.test :refer :all]
            [devstopfix.monster :refer :all])
  (:use [devstopfix.monsters.image-test :only [target-file]])
  (:import [java.io File]))



(deftest test-write-monster 
	(doall
		(for [x (range 10)]
		  (let [fout (target-file (str "monster-new-" x))]
		  	(save (new-monster ) fout)
		  	(is (.exists fout ))))))

(deftest test-write-monster-from-lave 
 	(save (new-monster-named "Lave Green Bug-Eyed Lizards") (target-file "monster-lave-green-bug-eyed-lizards")))
