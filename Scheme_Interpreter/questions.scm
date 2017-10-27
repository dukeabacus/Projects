#lang racket

(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cdar x) (cdr (car x)))
(define (cddr x) (cdr (cdr x)))

; Some utility functions that you may find useful to implement.
(define (map proc items)
  (if (null? items)
      '()
      (cons (proc (car items))
            (map proc (cdr items))))
  )


(define (cons-all first rests)
  (cond
    ((null? rests) (cons (cons first '()) nil)  )
    ( else  (map (lambda (x) (append (list first) x)) rests))
    )
  )

(define (zip pairs)
  (list (map car pairs) (map cadr pairs))
  )

;; Problem 17
;; Returns a list of two-element lists

(define (enumerate s)
  ; BEGIN PROBLEM 17
  (helper s 0)
  ; END PROBLEM 17
)

(define (helper s ind)
  (cond
    ((equal? s nil) nil)
    ((equal? (cdr s) nil) (cons (cons ind (cons (car s) nil)  )  nil ))
    (else (cons (cons ind (cons (car s) nil ) ) (helper (cdr s) (+ 1 ind)) ) )
    )
  )

;; Problem 18
;; List all ways to make change for TOTAL with DENOMS
(define (list-change total denoms)
  ;(print total)
  ;(print denoms)
  ; BEGIN PROBLEM 18
  (cond
    ((null? denoms) '())
    ((= 0 total) '())
    (( > (car denoms) total) (list-change total (cdr denoms)))
    (else (append  (cons-all (car denoms) (list-change (- total (car denoms)) denoms  ))    (list-change total (cdr denoms))  ) )
  )
)

  ; END PROBLEM 18

;; Problem 19
;; Returns a function that checks if an expression is the special form FORM
(define (check-special form)
  (lambda (expr) (equal? form (car expr))))

(define lambda? (check-special 'lambda))
(define define? (check-special 'define))
(define quoted? (check-special 'quote))
(define let?    (check-special 'let))

;; Converts all let special forms in EXPR into equivalent forms using lambda
(define (let-to-lambda expr)
  (cond ((atom? expr)
         ; BEGIN PROBLEM 19
         expr
         ; END PROBLEM 19
         )
        ((quoted? expr)
         ; BEGIN PROBLEM 19
         expr
         ; END PROBLEM 19
         )
        ((or (lambda? expr)
             (define? expr))
         (let ((form   (car expr))
               (params (cadr expr))
               (body   (cddr expr)))

           ; BEGIN PROBLEM 19
           (cons form (cons params (let-to-lambda body)))
           ; END PROBLEM 19
           ))
        ((let? expr)
         (let ((values (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 19
           (define lst (zip (let-to-lambda values)))
           (cons (cons 'lambda (cons (car lst) (let-to-lambda body)) ) (cadr lst))          
           ; END PROBLEM 19
           ))
        (else
         ; BEGIN PROBLEM 19
         (map let-to-lambda expr)
         ; END PROBLEM 19
         )))
