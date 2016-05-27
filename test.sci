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
  print_line("n fib fib2");
  while n < 20 -> (
    print_line(n, fib(n), fib2(n));
    n <- n + 1;
  );
);

test_fib();

eratosthenes := fun (n) -> (
  arr := make_array(n, true);
  i := 2;
  while i < n -> (
    if arr[i] then (
      print_line(i);
      j := 2*i;
      while j < n -> (
        arr[j] <- false;
        j <- j + i;
      );
    );
    i <- i + 1;
  );
);

print_line("primes up to 100");
eratosthenes(100);

make_matrix := fun (n, m) -> (
  mat := [];
  i := 0;
  while i < n -> (
    push(mat, make_array(m, 0.0));
    i <- i + 1;
  );
  mat;
);

print_line(make_matrix(2,3));

// test block
print_line(block ret -> 22); // 22
print_line(block ret -> (ret(22); print_line(23))); // 22 (note no 23!)

// quicksort
qsort := fun (arr) -> (
  qsort_rec := fun (i, j) -> (
    if i < j then (
      pivot := arr[j];
      n := i; m := i;
      while n < j -> (
        if arr[n] < pivot then (
          v := arr[m];
          arr[m] <- arr[n];
          arr[n] <- v;
          m <- m + 1;
        );
        n <- n + 1;
      );
      arr[j] <- arr[m];
      arr[m] <- pivot;
      qsort_rec(i, m-1);
      qsort_rec(m+1, j);
    );
  );
  qsort_rec(0, len(arr)-1);
  arr;
);
print_line(qsort([1,2,3,4,5]));
print_line(qsort([1,3,5,2,4]));
print_line(qsort([3,5,1,2,4]));
print_line(qsort([5,4,3,2,1]));