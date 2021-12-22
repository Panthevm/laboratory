(define tree
  '(0
    (11
     (2)
     (6))
    (7
     (5)
     (5
      (4)
      (2
       (2))))))

(define (solution accumulator node)
  (+ accumulator (/ (+ (car node) (fold-left solution 0 (cdr node))) 2)))

(newline)
(display (* (solution 0 tree) 2.0))
