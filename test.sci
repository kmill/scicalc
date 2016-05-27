// this is a definition of factorial, valid for integers
factorial := fun (n) -> (
  if n <= 1 then
    1
  else
    n * factorial(n - 1)
);

// here's fibonacci, starting with 0, 1.
fib := fun (n) ->
  if n <= 0 then
    0
  elif n == 1 then
    1
  else
    fib(n-1) + fib(n-2)
;

// that fibonacci is kind of slow.  here is a faster version
fib2 := fun (n) -> (
  a := 0;
  b := 1;
  m := 0;
  while m < n -> (
    c := b;
    b <- a + b;
    a <- c;
    m <- m + 1;
  );
  a;
);

test_fib := fun () -> (
  n := 0;
  while n < 20 -> (
    print(n); print(fib(n)); print(fib2(n)); print_line();
    n <- n + 1;
  );
);

test_fib();