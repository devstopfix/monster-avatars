(ns avatars.monster-test
  (:require [clojure.test :refer :all]
            [avatars.monster :refer :all])
  (:use [avatars.monsters.image-test :only [target-file]])
  (:import [java.io File]))



(deftest test-write-monster 
	(doall
		(for [x (range 10)]
		  (let [fout (target-file (str "monster-new-" x))]
		  	(save (new-monster ) fout)
		  	(is (.exists fout ))))))

(deftest test-write-monster-from-lave 
 	(save (new-monster-named "Lave Green Bug-Eyed Lizards") (target-file "monster-lave-green-bug-eyed-lizards")))
